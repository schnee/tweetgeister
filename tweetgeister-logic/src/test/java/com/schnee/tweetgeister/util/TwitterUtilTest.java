package com.schnee.tweetgeister.util;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;

import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.TwitterException;

public class TwitterUtilTest {

    //@Test
    public void testUpdateStatus() {
        try {
            TwitterUtil.updateStatus("is this thing on?");
        } catch (TwitterException e) {
            e.printStackTrace();
            fail();
        }
        assertTrue(true);
    }

    @Ignore
    @Test
    public void testIsUser() {
        try {
            assertTrue(TwitterUtil.isUser("schnee"));
            
        } catch (TwitterException e) {
            fail(e.getMessage());
        }
    }

    @Ignore
    @Test
    public void testGetUserTimeline() {
        try {
            Set<CharSequence> timeline = TwitterUtil.getUserTimeline("@schnee");

            System.out.println(timeline.size());
            for (CharSequence tweet : timeline) {
                System.out.println(tweet);
            }

        } catch (TwitterException e) {
            fail(e.getMessage());
        }
    }

    //@Test
    public void testGetLocations() {
        try {
            int usWoeId = TwitterUtil.getUSTrendLocationWoeId();

            assertTrue(usWoeId == 23424977);

            Trends trends = TwitterUtil.getCurrentTrends(usWoeId);

            for (int i = 0; i < trends.getTrends().length; i++) {
                Trend trend = trends.getTrends()[i];
                System.out.println(trend);
            }

        } catch (TwitterException e) {
            fail(e.getMessage());
        }
    }

    //@Test
    public void testTrends() {
        try {
            Trends currentTrends = TwitterUtil.getCurrentTrends();

            Trend[] trends = currentTrends.getTrends();

            for (int i = 0; i < trends.length; i++) {
                Trend trend = trends[i];
                System.out.println(trend);
            }

        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testGetTopTrend() {
        try {
            Trend top = TwitterUtil.getTopTrend();
            System.out.println(top);
        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
