package com.schnee.tweetgeister.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import junit.framework.TestCase;

public class TokenizedCharSequenceTest extends TestCase {

    public void testFactory() {

        TokenizerFactory f = TokenizedCharSequence.tokenizerFactory();

        List<String> strings = Arrays
                .asList("#clayshirky is wearing the \"three wolf shirt.\" Awesome.",
                        "pub-going loose page: http://ow.ly/1kdbc fb page: women fb loose forward consortium pub-going forward women #sxswi consortium",
                        "??RT @Beckland: RT @scottpierce #clayshirky #sxswi Consortium of Pub-going Loose and Forward Women FB page: http://ow.ly/1kdBc",
                        "RT @BrianGerry: Cool infographics eye candy slide just shown www.flickr.com/photos/86583664@N00/3039196916  #interinfo");

        for (String s : strings) {
            char[] charArray = s.toCharArray();
            Tokenizer t = f.tokenizer(charArray, 0, charArray.length);

            String[] tokenized = t.tokenize();

            for (int i = 0; i < tokenized.length; i++) {
                System.out.println(tokenized[i]);
            }
            System.out.println();
        }

    }
}
