package com.schnee.tweetgeister;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TweetgeisterTest {

    @Test
    public void testMakePath() {
        
        String path = Tweetgeister.makeFilename("#innovate or #xinnovate", "theDir", "js", "1203");
        
        assertEquals("theDir/innovate-or-xinnovate-1203.js", path);
        
    }

    @Test
    public void testMakeUrl() {
        String base = "http://www.tweetgeister.com/";
        String query = "#xinnovate";
        
        String theUrl = Tweetgeister.makeUrl(base, query, "1203");
        
        assertEquals("http://www.tweetgeister.com/xinnovate-1203.html", theUrl);
    }


}
