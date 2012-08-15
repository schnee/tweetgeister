package com.schnee.tweetgeister.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

public class TrailingPunctuationFilter extends TokenFilter {

	private TermAttribute termAtt;
	
	

	public TrailingPunctuationFilter(TokenStream input) {
		super(input);
		termAtt = addAttribute(TermAttribute.class);
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (input.incrementToken()) {
			final char[] buffer = termAtt.termBuffer();
			int length = termAtt.termLength();

			//if the last character not a letter or digit, get rid of it
			while (!Character.isLetterOrDigit(buffer[length-1])){
				if(length ==1 ){
					return false;
				}
				termAtt.setTermLength(length-1);
				length = termAtt.termLength();
			}
			return true;
		} else {
			return false;
		}
	}

}
