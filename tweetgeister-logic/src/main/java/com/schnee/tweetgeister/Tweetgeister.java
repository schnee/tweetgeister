package com.schnee.tweetgeister;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import twitter4j.TwitterException;

import com.schnee.tweetgeister.analysis.TokenizedCharSequence;
import com.schnee.tweetgeister.data.Node;
import com.schnee.tweetgeister.data.TGTweet;
import com.schnee.tweetgeister.data.Tree;
import com.schnee.tweetgeister.util.Bitly;
import com.schnee.tweetgeister.util.Csv;
import com.schnee.tweetgeister.util.TwitterUtil;

public class Tweetgeister {
    
 

    private static boolean searchTwitter = true;

    private static String csvFileName = "";

    private static String queryString = "";

    private static String outputPath = "";

    private static String dirString = "";

    private static String announceTo = "";
    
    private static String base = "";

    private static boolean announce = false;

    private static String tgUrl = "";

    private static boolean trends = false;

    public static void main(String[] args) {
        loadProps();

        try {
            TreeSet<CharSequence> inputSet = new TreeSet<CharSequence>();

            if (searchTwitter) {

                Tweetgeister tg = new Tweetgeister();
                tg.cluster(queryString, outputPath, announce, base, announceTo);

            } else {
                inputSet = Csv.parse(csvFileName);

                System.out.println("Total Tweets: " + inputSet.size());

                Date min = TwitterUtil.getMinDate(inputSet);

                Date max = TwitterUtil.getMaxDate(inputSet);

                System.out.println(min + "\n" + max);

                TwitterUtil.addAgeToTweets(inputSet, min, max);

                Clusterer cl = new Clusterer(inputSet, TokenizedCharSequence.TOKENIZER_FACTORY);

                Tree<CharSequence> mindmap = cl.buildTree();

                fillTree(mindmap, outputPath);

                if (announce) {
                    tellTwitter(tgUrl, announceTo, queryString);
                }

                System.out.println("finished");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void cluster(String query, String outputPath, boolean announce, String base, String announceTo) {
        try {
            
            
            TreeSet<CharSequence> inputSet = new TreeSet<CharSequence>();

            inputSet = TwitterUtil.search(query);

            System.out.println("Total Tweets: " + inputSet.size());

            Date min = TwitterUtil.getMinDate(inputSet);

            Date max = TwitterUtil.getMaxDate(inputSet);

            System.out.println(min + "\n" + max);

            TwitterUtil.addAgeToTweets(inputSet, min, max);

            Clusterer cl = new Clusterer(inputSet, TokenizedCharSequence.TOKENIZER_FACTORY);

            Tree<CharSequence> mindmap = cl.buildTree();

            fillTree(mindmap, outputPath);

            if (announce) {
                String tgUrl = makeUrl(base, query, "");
                tellTwitter(tgUrl, announceTo, query);
            }

            System.out.println("finished");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void tellTwitter(String tgUrl, String announceTo, String queryString) {

        String small = Bitly.makeSmall(tgUrl);

        if (small == null || small.isEmpty()) {
            System.err.println("Bitly is down");
            return;
        }

        StringBuilder msg = new StringBuilder();
        msg.append(small).append(" ");

        if (announceTo != null) {
            if (!announceTo.isEmpty()) {
                if (!announceTo.startsWith("@")) {
                    msg.append("@");
                }
                msg.append(announceTo).append(" ");
            }
        }
        msg.append(queryString).append(" #tweetgeist ready");

        System.out.println(msg);

        try {
            TwitterUtil.updateStatus(msg.toString());
        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Message: " + e.getMessage());
            if(e.getMessage().contains("Status is a duplicate")){
                try {
                    TwitterUtil.updateStatus("RT: " + msg.toString() + " " + System.currentTimeMillis());
                } catch (TwitterException e1) {
                    // TODO Auto-generated catch block
                    System.out.println("Message: " + e.getMessage());
                }
            }
        }
    }

    protected static void loadProps() {
        dirString = System.getProperty("tg.d");
        if (dirString == null || dirString.isEmpty()) {
            System.err.println("Null tg.d");
            System.exit(-1);
        }

        String trS = System.getProperty("tg.tr");

        if (trS != null) {
            trends = Boolean.parseBoolean(trS);
        }

        if (trends) {
            queryString = TwitterUtil.getTopTrendAsQueryString();
            if (queryString == null || queryString.isEmpty()) {
                System.err.println("Couldn't get top trend");
                System.exit(0);
            }
        } else {
            queryString = System.getProperty("tg.s");
        }
        if (queryString == null || queryString.isEmpty()) {
            searchTwitter = false;

            csvFileName = System.getProperty("tg.csv");

            if (csvFileName == null || csvFileName.isEmpty()) {
                System.err.println("one of tg.s, tg.tr, or tg.csv must be defined");
            } else {
                System.out.println("Tweetgeisting " + csvFileName);
                outputPath = makeFilename(csvFileName, dirString, "js", "");
            }

        } else {
            System.out.println("Tweetgeisting " + queryString);
            outputPath = makeFilename(queryString, dirString, "js", "");
        }

        announceTo = System.getProperty("tg.to");

        String annS = System.getProperty("tg.a");
        if (annS != null) {

            announce = Boolean.parseBoolean(annS);
        }
        base = System.getProperty("tg.base");

        if (searchTwitter) {
            tgUrl = makeUrl(base, queryString, "");
        } else {
            tgUrl = makeUrl(base, csvFileName, "");
        }
    }

    public static String makeUrl(String base, String qString, String date) {
        String temp = base;

        if (!temp.endsWith("/")) {
            temp += "/";
        }

        if (trends) {
            temp += "trends/";
        }

        String tQstr = makeSafeWord(qString);

        temp += tQstr;
        temp += "-"+date;
        temp += ".html";

        return temp;
    }

    public static String makeFilename(String qString, String dirString, String ext, String date) {
        String filesep = System.getProperty("file.separator");
        if (!dirString.endsWith(filesep)) {
            dirString += filesep;
        }

        if (trends) {
            dirString += "trends" + filesep;
        }

        String tQstr = makeSafeWord(qString);

        String fileName = tQstr + "-" + date;

        String pathname = dirString + fileName + "." + ext;

        return pathname;
    }

    public static String makeSafeWord(String qString) {
        String tQstr = qString.replaceAll("[#|@]*", "");
        tQstr = tQstr.replaceAll(" ", "-");
        return tQstr;
    }

    public static void fillTree(Tree<CharSequence> t, String pathname) throws IOException {
        Node<CharSequence> rootElement = t.getRootElement();
        JSONObject rootJSON = new JSONObject();

        JsonConfig jconfig = new JsonConfig();
        jconfig.setExcludes(new String[] {"createdAt", "fromUserId", "id", "geoLocation", "isoLanguageCode",
                "profileImageUrl", "source", "toUser", "toUserId", "numberOfChildren", "annotations", "location"});

        fillTree(rootElement, rootJSON, jconfig);

        BufferedWriter out = new BufferedWriter(new FileWriter(new File(pathname)));

        out.append("var ").append("root").append(" = ");

        out.append(rootJSON.toString()).append(";");

        out.close();

    }

    public static void fillTree(Node<CharSequence> rootElement, JSONObject rootJSON, JsonConfig jconfig) {
        List<Node<CharSequence>> children = rootElement.getChildren();

        for (Node<CharSequence> node : children) {

            List<Node<CharSequence>> children2 = node.getChildren();

            if (children2.size() > 0) {
                //dealing with a topic node
                CharSequence data = node.getData();
                if (data instanceof TGTweet) {
                    TGTweet topic = (TGTweet) data;
                    JSONObject topicObj = new JSONObject();
                    //pass the node and the topic Obj down to the next level
                    fillTree(node, topicObj, jconfig);
                    rootJSON.put(topic.getText(), topicObj);
                }
            } else {
                CharSequence data = node.getData();
                if (data instanceof TGTweet) {
                    TGTweet tweet = (TGTweet) data;
                    JSONObject current = JSONObject.fromObject(tweet, jconfig);

                    rootJSON.put(tweet.getId(), current);
                }
            }
        }
    }

    private CharSequence cleanLines(CharSequence theData) {

        //theData = theData.replaceAll("[\\n\\r]+", " ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < theData.length(); i++) {
            char toAppend = 'a';
            switch (theData.charAt(i)) {
            case '\r':
                toAppend = ' ';
                break;
            case '\n':
                toAppend = ' ';
                break;
            case '"':
                toAppend = ' ';
                break;
            case '\\':
                toAppend = ' ';
                break;
            default:
                toAppend = theData.charAt(i);
            }
            sb.append(toAppend);
        }
        String retVal = sb.toString();

        return retVal;

    }
}
