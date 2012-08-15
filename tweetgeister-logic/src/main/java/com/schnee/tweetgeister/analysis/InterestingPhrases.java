package com.schnee.tweetgeister.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.aliasi.lm.TokenizedLM;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.ScoredObject;

public class InterestingPhrases {

    private static int NGRAM = 3;

    private static int MIN_COUNT = 5;

    private static int NGRAM_REPORTING_LENGTH = 2;

    private static int MAX_COUNT = 8;

    private Set<CharSequence> texts;

    private TokenizerFactory tf;

    public InterestingPhrases(Set<CharSequence> texts, TokenizerFactory tf) {
        super();

        this.texts = texts;
        this.tf = tf;

    }

    public List<String[]> getPhrases() {

        return getPhrases(NGRAM_REPORTING_LENGTH, MIN_COUNT, MAX_COUNT);

    }
    
    public List<String[]> getPhrases(int ngram, int min, int max)
    {
        List<String[]> phrases = new ArrayList<String[]>();
        TokenizedLM model = buildModel(tf, NGRAM);

        model.sequenceCounter().prune(3);

        SortedSet<ScoredObject<String[]>> coll = model.collocationSet(ngram, min, max);

        for (ScoredObject<String[]> nGram : coll) {
            String[] toks = nGram.getObject();
            phrases.add(toks);
        }

        return phrases;
    }

    private TokenizedLM buildModel(TokenizerFactory tokenizerFactory, int ngram) {

        TokenizedLM model = new TokenizedLM(tokenizerFactory, ngram);

        for (CharSequence text : this.texts) {
            model.handle(text);
        }
        return model;
    }

}
