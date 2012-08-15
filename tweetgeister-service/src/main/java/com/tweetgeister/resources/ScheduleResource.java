package com.tweetgeister.resources;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.RejectedExecutionException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.schnee.tweetgeister.Clusterer;
import com.schnee.tweetgeister.Tweetgeister;
import com.schnee.tweetgeister.analysis.TokenizedCharSequence;
import com.schnee.tweetgeister.data.Tree;
import com.schnee.tweetgeister.util.TwitterUtil;
import com.sun.jersey.api.view.Viewable;

import freemarker.template.Template;

@Path("/schedule")
public class ScheduleResource {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");

    private TaskExecutor taskExecutor;

    private FreeMarkerConfigurer freemarkerConfig;

    private String outDir;

    private String base;

    public void setOutDir(String outDir) {
        this.outDir = outDir;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setConfig(FreeMarkerConfigurer config) {
        this.freemarkerConfig = config;
    }

    @Produces(MediaType.TEXT_PLAIN)
    @POST
    public Response schedule(@FormParam(value = "hashtag") String hashtag, @FormParam(value = "tweetTo") String tweetTo) {

        try {
            taskExecutor.execute(new TweetgeisterTask(hashtag, tweetTo));
            return Response.ok(hashtag).build();
        } catch (RejectedExecutionException e) {
            return Response.notModified("Failed to sched " + hashtag).build();
        }
    }

    @Produces(MediaType.TEXT_HTML)
    @GET
    public Viewable schedule() {
        final Map<String, Object> model = new HashMap<String, Object>(0);
        return new Viewable("schedule.ftl", model);
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    private class TweetgeisterTask implements Runnable {

        private String hashTag;

        private String tweetTo;

        private String date;

        public TweetgeisterTask(String hashTag, String tweetTo) {
            super();
            this.hashTag = hashTag;
            this.tweetTo = tweetTo;

            date = sdf.format(Calendar.getInstance().getTime());
        }

        public void run() {

            try {
                Map<String, String> model = new HashMap<String, String>();
                TreeSet<CharSequence> inputSet = new TreeSet<CharSequence>();

                inputSet = TwitterUtil.search(hashTag);

                System.out.println("Total Tweets: " + inputSet.size());

                Date min = TwitterUtil.getMinDate(inputSet);

                Date max = TwitterUtil.getMaxDate(inputSet);

                model.put("tweetCount", Integer.toString(inputSet.size()));
                model.put("oldest", min.toString());
                model.put("youngest", max.toString());
                model.put("hashtag", hashTag);

                System.out.println(min + "\n" + max);

                TwitterUtil.addAgeToTweets(inputSet, min, max);

                System.out.println("aged");

                Clusterer cl = new Clusterer(inputSet, TokenizedCharSequence.TOKENIZER_FACTORY);

                System.out.println("clustered");

                Tree<CharSequence> mindmap = cl.buildTree();

                System.out.println("treed");

                String jsFile = Tweetgeister.makeFilename(hashTag, outDir, "js", date);

                model.put("jsname", Tweetgeister.makeSafeWord(hashTag) + "-" + date);

                model.put("root", Tweetgeister.makeSafeWord(hashTag));

                Tweetgeister.fillTree(mindmap, jsFile);

                System.out.println("outputed: " + jsFile);

                Template tpl = freemarkerConfig.getConfiguration().getTemplate("tg-search-base.ftl");

                System.out.println("Got template ");

                String htmlFile = Tweetgeister.makeFilename(hashTag, outDir, "html", date);

                BufferedWriter out = new BufferedWriter(new FileWriter(new File(htmlFile)));

                tpl.process(model, out);

                if (tweetTo != null && tweetTo.length() > 0) {
                    String tgUrl = Tweetgeister.makeUrl(base, hashTag, date);
                    System.out.println(tgUrl);
                    Tweetgeister.tellTwitter(tgUrl, tweetTo, hashTag);
                }

                System.out.println("finished");

            } catch (Exception e) {
                System.out.println("Exception " + e.getMessage());
                System.out.println(e);
            }

        }

    }

}
