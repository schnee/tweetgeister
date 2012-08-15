package com.schnee.tweetgeister.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

public class UtilsTest {

    @Ignore
    @Test
    public void testExtractUsers() {
       
        Set<String> users = Utils.extractUsers("@schnee OR @jessicaalba @schnee austin texas");
        
        assertEquals(users.size(), 2);
       
        assertTrue(users.contains("schnee"));
        
        assertTrue(users.contains("jessicaalba"));
        
    }

}
