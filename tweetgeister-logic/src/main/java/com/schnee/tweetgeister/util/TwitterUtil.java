package com.schnee.tweetgeister.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import twitter4j.Location;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

import com.schnee.tweetgeister.data.TGTweet;

public class TwitterUtil {

    private static final String TWEETGEISTER_CONSUMER_SECRET = "nkAJ94rrmpDDgEc7jhISp4QrQx1NpI59U3BwB4F4";

    private static final String TWEETGEISTER_CONSUMER_KEY = "24c8pF8V3vDLeQUhHd1aAA";

    private static String twtgeisterToken = "157004998-NlEqQ1l0WK4Z4LiolKj3YVtOKhyre58YKszE63NV";

    private static String twtgeisterTokenSecret = "PNTqkhHyje9fMCfhqSyONYIMnyNC1givV8OS2plRm8";

    public static AccessToken getAccessToken() {
        AccessToken tok = new AccessToken(twtgeisterToken, twtgeisterTokenSecret);

        return tok;
    }

    public static void updateStatus(String message) throws TwitterException {

        Twitter twitter = new TwitterFactory().getOAuthAuthorizedInstance(TWEETGEISTER_CONSUMER_KEY,
                TWEETGEISTER_CONSUMER_SECRET, getAccessToken());

        twitter.updateStatus(message);

    }

    public static TreeSet<CharSequence> getUserTimeline(String screenName) throws TwitterException {
        Twitter twttr = new TwitterFactory().getInstance();

        int page = 1;
        int rpp = 50;
        int hits = rpp;

        TreeSet<CharSequence> tweets = new TreeSet<CharSequence>();

        while (page < 20 && hits == rpp) {
            System.out.print(page+" ");
            Paging paging = new Paging(page, rpp);
            ResponseList<Status> userTimeline = twttr.getUserTimeline(screenName, paging);

            hits = userTimeline.size();
            System.out.println(hits);
            
            for (Status status : userTimeline) {
                TGTweet tgt = new TGTweet(status.getCreatedAt(), screenName, -1, status.getId(), null, null, null,
                        status.getText(), null, -1);
                tweets.add(tgt);
            }

            page++;
        }
        
        return tweets;

    }

    public static String getTopTrendAsQueryString() {
        Trend tr = null;
        try {
            tr = getTopTrend();
        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (tr != null) {
            return tr.getQuery();
        } else {
            return null;
        }
    }

    public static Trend getTopTrend() throws TwitterException {

        Trends trends = TwitterUtil.getCurrentTrends();
        return trends.getTrends()[0];

    }

    public static int getUSTrendLocationWoeId() throws TwitterException {
        Twitter twttr = new TwitterFactory().getInstance();

        ResponseList<Location> availableTrends = twttr.getAvailableTrends();

        int usWoeId = 1;

        for (Location location : availableTrends) {
            String countryCode = location.getCountryCode();
            if (countryCode != null && countryCode.equalsIgnoreCase("US") && location.getPlaceCode() == 12) {
                usWoeId = location.getWoeid();
            }

        }

        return usWoeId;

    }

    public static Trends getCurrentTrends(int woeid) throws TwitterException {
        Twitter twttr = new TwitterFactory().getInstance();

        return twttr.getLocationTrends(woeid);
    }

    public static Trends getCurrentTrends() throws TwitterException {

        Twitter twttr = new TwitterFactory().getInstance();

        return twttr.getCurrentTrends(false);

        //return twttr.getLocationTrends(getUSTrendLocationWoeId());

    }
    
    public static boolean isUser(String user) throws TwitterException {
        Twitter twttr = new TwitterFactory().getInstance();
        
        User tUser = twttr.showUser(user);
        
        if(tUser != null && tUser.getId() > 0){
            return true;
        } else {
            return false;
        }
        
    }

    public static TreeSet<CharSequence> search(String qString) throws TwitterException {
        Twitter twttr = new TwitterFactory().getInstance();

        List<Tweet> allTweets = new ArrayList<Tweet>();
        Query query = new Query(qString);
        query.setRpp(100);
        query.setLang("en");

        int hits = 100;
        int page = 1;
        while (allTweets.size() < 1500 && page < 16 && hits == 100) {

            query.setPage(page);

            // System.out.println(query.toString());
            QueryResult result = twttr.search(query);
            List<Tweet> tweets = result.getTweets();
            allTweets.addAll(tweets);
            hits = tweets.size();
            //System.out.println("page: " + page + " hits: " + hits + " all tweets:" + allTweets.size());
            page++;
        }

        TreeSet<CharSequence> inputSet = new TreeSet<CharSequence>();

        for (Tweet tweet : allTweets) {
            TGTweet tgt = new TGTweet(tweet);
            inputSet.add(tgt);
        }

        return inputSet;
    }

    public static void addAgeToTweets(TreeSet<CharSequence> inputSet, Date min, Date max) {
        long diff = max.getTime() - min.getTime();

        for (CharSequence tweet : inputSet) {
            if (tweet instanceof TGTweet) {
                TGTweet tgt = (TGTweet) tweet;

                double a = (double) (tgt.getCreatedAt().getTime() - min.getTime()) / (double) diff;

                int age = (int) Math.round(a * 100);

                tgt.setAge(age);

            }

        }
    }

    public static Date getMaxDate(TreeSet<CharSequence> inputSet) {
        Date max = null;
        CharSequence last = inputSet.last();

        if (last instanceof TGTweet) {
            TGTweet tgt = (TGTweet) last;
            max = tgt.getCreatedAt();
        }
        return max;
    }

    public static Date getMinDate(TreeSet<CharSequence> inputSet) {
        Date min = null;
        CharSequence first = inputSet.first();
        if (first instanceof TGTweet) {
            TGTweet tgt = (TGTweet) first;
            min = tgt.getCreatedAt();
        }
        return min;
    }

    public static void main(String args[]) throws Exception {
        // The factory instance is re-useable and thread safe.
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(TWEETGEISTER_CONSUMER_KEY, TWEETGEISTER_CONSUMER_SECRET);
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
            String pin = br.readLine();
            try {
                if (pin.length() > 0) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                } else {
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if (401 == te.getStatusCode()) {
                    System.out.println("Unable to get the access token.");
                } else {
                    te.printStackTrace();
                }
            }
        }
        //persist to the accessToken for future reference.
        storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
        Status status = twitter.updateStatus(args[0]);
        System.out.println("Successfully updated the status to [" + status.getText() + "].");
        System.exit(0);
    }

    private static void storeAccessToken(int useId, AccessToken accessToken) {
        System.out.println(useId);
        System.out.println("Token: " + accessToken.getToken());
        System.out.println("TokenSecret: " + accessToken.getTokenSecret());
    }

}
