package com.schnee.tweetgeister.analysis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

public class InterestingPhrasesTest extends TestCase {

    public void testGetPhrases() {
        
        Set<CharSequence> model = new HashSet<CharSequence>();
        
        model.add("@Beckland didn't see the #interinfo panel. What did I miss?");
        model.add("RT @jonzmikly: Give artists data and see what they come up with #interinfo #sxsw #sxtxstate");
        model.add("Give artists data and see what they come up with #interinfo #sxsw #sxtxstate");
        model.add("Quote of the day: RT @jonzmikly Give artists data and see what they come up with #interinfo #sxsw #sxtxstate");
        model.add("@lsherman Boy did I think of you watching the Twitter stream of the #interinfo panel! Did you get to see it? Would love to see animation");
        model.add("rise of Paris Hilton would like to see that. #interinfo");
        
        InterestingPhrases ip = new InterestingPhrases(model, TokenizedCharSequence.TOKENIZER_FACTORY);
        
        List<String[]> phrases = ip.getPhrases();
        
        //System.out.println(phrases);
        
        phrases = ip.getPhrases(2, 1, 8);
        
        assertNotNull(phrases);
//        
//        for (String[] strings : phrases) {
//            for (int i = 0; i < strings.length; i++) {
//                System.out.print(strings[i] + " ");
//            }
//            System.out.println("\n");
//        }
    }
    
    public void xtestGetPhrasesShort() {
        Set<CharSequence> model = new HashSet<CharSequence>();
        
        model.add("Panel is pretty but dumb #interinfo");
        model.add("pretty sure Prefuse.org doesn't watermark. java and flash libs avail.  #interinfo");
        model.add("At #interinfo - @GOOD is pretty awesome");
        model.add("RT @LCucinotta: At #interinfo - @GOOD is pretty awesome");
        
        InterestingPhrases ip = new InterestingPhrases(model, TokenizedCharSequence.TOKENIZER_FACTORY);
        
        List<String[]> phrases = ip.getPhrases();
        
        System.out.println(phrases);
        
        phrases = ip.getPhrases(2, 1, 3);
        
        for (String[] strings : phrases) {
            for (int i = 0; i < strings.length; i++) {
                System.out.print(strings[i] + " ");
            }
            System.out.println("\n");
        }
    }

}
