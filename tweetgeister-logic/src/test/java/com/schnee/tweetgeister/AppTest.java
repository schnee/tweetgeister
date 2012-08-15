package com.schnee.tweetgeister;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.junit.Test;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import com.aliasi.util.Distance;
import com.schnee.tweetgeister.analysis.TokenizedCharSequence;
import com.schnee.tweetgeister.data.Node;
import com.schnee.tweetgeister.data.TGTweet;
import com.schnee.tweetgeister.data.Tree;
import com.schnee.tweetgeister.util.Csv;
import com.schnee.tweetgeister.util.TwitterUtil;

/**
 * Unit test for simple App.
 */
public class AppTest{

  
    @Test
    public void dummyTest() {
        assertTrue(true);
    }
    /**
     * Rigourous Test :-)
     */
    public void xxxtestApp() {

        Twitter twitter = new TwitterFactory().getInstance("schnee", "c0ld3r");

        // get the last 20 posts from people in your friend's list

        List<Status> statuses = null;
        try {
            statuses = twitter.getFriendsTimeline();
        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (Status status : statuses) {
            System.out.println(status.getUser().getName() + ":" + status.getText() + " " + status.getSource() + " "
                    + status.getCreatedAt());
        }

        try {
            Trends trends = twitter.getTrends();

            Trend[] trendsArr = trends.getTrends();
            for (int i = 0; i < trendsArr.length; i++) {
                Trend trend = trendsArr[i];
                System.out.println(trend);
                Query query = new Query(trend.getName());

                QueryResult search = twitter.search(query);

                List<Tweet> tweets = search.getTweets();

                for (Tweet tweet : tweets) {
                    System.out.println(tweet.toString());
                }

            }

        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void xtestCleaner() {
        // The factory instance is re-useable and thread safe.

        try {
            Twitter twitter = new TwitterFactory().getInstance();

            List<Tweet> allTweets = new ArrayList<Tweet>();
            Query query = new Query("schnee");

            query.setLang("en");

            int hits = 100;
            int page = 1;

            // System.out.println(query.toString());
            QueryResult result = twitter.search(query);
            List<Tweet> tweets = result.getTweets();
            allTweets.addAll(tweets);
            hits = tweets.size();
            System.out.println("page: " + page + " hits: " + hits + " all tweets:" + allTweets.size());
            page++;

            System.out.println("hits: " + allTweets.size());

            for (Tweet tweet : allTweets) {
                cleanLines(tweet.getText());
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void xxtestSearch() {
        // The factory instance is re-useable and thread safe.

        String qString = System.getProperty("tg.s");
        if (qString == null || qString.isEmpty()) {
            System.out.println("Null tg.s");
            qString = "#xchat";
        }

        try {
            Set<CharSequence> inputSet = TwitterUtil.search(qString);
            Clusterer cl = new Clusterer(inputSet, TokenizedCharSequence.TOKENIZER_FACTORY);

            Tree<CharSequence> mindmap = cl.buildTree();

            String fileName = qString.replaceAll("#|@", "");

            String pathname = "./protogeist/" + fileName + ".js";

            System.out.println(pathname);

            fillTree(mindmap, pathname);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void xxxtestLoadFromCSV() {

        try {
            String pathname = "xinnovate.csv";
            Set<CharSequence> is = Csv.parse(pathname);

            System.out.println("Attempting to cluster " + is.size() + " items");

            Clusterer cl = new Clusterer(is, TokenizedCharSequence.TOKENIZER_FACTORY);

            Tree<CharSequence> mindmap = cl.buildTree();

            String jsfilename = "./protogeist/xinnovate.js";

            fillTree(mindmap, jsfilename);

            System.out.println("\nDone: " + jsfilename);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    protected void fillTree(Tree<CharSequence> t, String pathname) throws IOException {
        Node<CharSequence> rootElement = t.getRootElement();
        JSONObject rootJSON = new JSONObject();

        JsonConfig jconfig = new JsonConfig();
        jconfig.setExcludes(new String[] {"createdAt", "fromUserId", "id", "geoLocation", "isoLanguageCode",
                "profileImageUrl", "source", "toUser", "toUserId", "numberOfChildren"});

        fillTree(rootElement, rootJSON, jconfig);

        BufferedWriter out = new BufferedWriter(new FileWriter(new File(pathname)));

        out.append("var ").append("root").append(" = ");

        out.append(rootJSON.toString()).append(";");

        out.close();

    }

    protected void fillTree(Node<CharSequence> rootElement, JSONObject rootJSON, JsonConfig jconfig) {
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

    final Distance<TokenizedCharSequence> COSINE_DISTANCE = new Distance<TokenizedCharSequence>() {
        public double distance(TokenizedCharSequence doc1, TokenizedCharSequence doc2) {
            double oneMinusCosine = 1.0 - doc1.cosine(doc2);
            if (oneMinusCosine > 1.0)
                return 1.0;
            else if (oneMinusCosine < 0.0)
                return 0.0;
            else
                return oneMinusCosine;
        }
    };
}
