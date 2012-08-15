package com.schnee.tweetgeister.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.util.AttributeSource;

public class WhitespaceQuoteTokenizer extends CharTokenizer {
	
	private String quotes = "\"\'";

	public WhitespaceQuoteTokenizer(AttributeFactory factory, Reader input) {
		super(factory, input);
		// TODO Auto-generated constructor stub
	}

	public WhitespaceQuoteTokenizer(AttributeSource source, Reader input) {
		super(source, input);
		// TODO Auto-generated constructor stub
	}

	public WhitespaceQuoteTokenizer(Reader input) {
		super(input);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean isTokenChar(char c) {
		boolean isWhiteSpace = Character.isWhitespace(c);
		boolean isQuote = quotes.contains(String.valueOf(c));
		
		boolean isToken = !(isWhiteSpace || isQuote);
		
		return isToken;
	}

}
