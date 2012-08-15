package com.schnee.tweetgeister.analysis;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.aliasi.tokenizer.EnglishStopTokenizerFactory;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.StopTokenizerFactory;
import com.aliasi.tokenizer.TokenLengthTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.tokenizer.WhitespaceNormTokenizerFactory;
import com.aliasi.util.Counter;
import com.aliasi.util.ObjectToCounterMap;

public class TokenizedCharSequence implements CharSequence {

    final String mString;

    final char[] mText; // don't really need to store

    final ObjectToCounterMap<String> mTokenCounter = new ObjectToCounterMap<String>();

    final double mLength;

    public TokenizedCharSequence(String input) throws IOException {
        mString = input;
        mText = input.toCharArray();
        Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(mText, 0, mText.length);
        String token;
        while ((token = tokenizer.nextToken()) != null)
            mTokenCounter.increment(token.toLowerCase());
        mLength = length(mTokenCounter);
    }

    public double cosine(TokenizedCharSequence thatDoc) {
        return product(thatDoc) / (mLength * thatDoc.mLength);
    }

    double product(TokenizedCharSequence thatDoc) {
        double sum = 0.0;
        for (String token : mTokenCounter.keySet()) {
            int count = thatDoc.mTokenCounter.getCount(token);
            if (count == 0)
                continue;
            // tf = sqrt(count); sum += tf1 * tf2
            sum += Math.sqrt(count * mTokenCounter.getCount(token));
        }
        return sum;
    }

    public String toString() {
        return mString;
    }

    static double length(ObjectToCounterMap<String> otc) {
        double sum = 0.0;
        for (Counter counter : otc.values()) {
            double count = counter.doubleValue();
            sum += count; // tf =sqrt(count); sum += tf * tf
        }
        return Math.sqrt(sum);
    }

    public static final TokenizerFactory TOKENIZER_FACTORY = tokenizerFactory();

    static TokenizerFactory tokenizerFactory() {

        Set<String> stopSet = new HashSet<String>();
        //stopSet.add("interinfo");
        stopSet.add("sxsw");
        stopSet.add("sxswi");
        //stopSet.add("#");
        //stopSet.add("@");
        stopSet.add("rt");

        //"((https?://)?([-\\w\\.]+)+(:\\d+)?(/([\\w/_\\.]*(\\?\\S+)?)?)?)|"

        String regex = "(((ht|f)tp(s?))\\://)?([-\\w\\.]+)+(\\:[0-9]+)*"; // this gets the scheme and the host and the port

        regex = regex + "(/($|[a-zA-Z0-9\\.\\,\\;\\?\\'\\\\+&@%\\$#\\=~_\\-]+))*"; // this seems to handle path and query string
        regex = regex +"|(#|@)?\\w+"; //these are simple words, twitterized

        TokenizerFactory factory = new RegExTokenizerFactory(regex);//new RegExTokenizerFactory("\\S+"); //IndoEuropeanTokenizerFactory.INSTANCE;// 
        factory = new WhitespaceNormTokenizerFactory(factory);
        factory = new LowerCaseTokenizerFactory(factory);
        factory = new EnglishStopTokenizerFactory(factory);
        factory = new StopTokenizerFactory(factory, stopSet);
        factory = new TokenLengthTokenizerFactory(factory, 2, Integer.MAX_VALUE);
        // factory = new PorterStemmerTokenizerFactory(factory);

        return factory;
    }

    public int length() {
        // TODO Auto-generated method stub
        return mText.length;
    }

    public char charAt(int index) {
        // TODO Auto-generated method stub
        return mText[index];
    }

    public CharSequence subSequence(int start, int end) {
        // TODO Auto-generated method stub
        return mString.subSequence(start, end);
    }

}
