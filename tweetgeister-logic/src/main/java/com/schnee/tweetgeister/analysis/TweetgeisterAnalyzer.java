package com.schnee.tweetgeister.analysis;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.util.Version;

public final class TweetgeisterAnalyzer extends Analyzer {

	private final Set<?> stopWords;
	private final boolean enablePositionIncrements;

	/**
	 * An unmodifiable set containing some common English words that are not
	 * usually useful for searching.
	 */
	public static final Set<?> ENGLISH_STOP_WORDS_SET;

	static {
		final List<String> stopWords = Arrays.asList("a", "an", "and", "are",
				"as", "at", "be", "but", "by", "for", "if", "in", "into", "is",
				"it", "no", "not", "of", "on", "or", "such", "that", "the",
				"their", "then", "there", "these", "they", "this", "to", "was",
				"will", "with");
		final CharArraySet stopSet = new CharArraySet(stopWords.size(), false);
		stopSet.addAll(stopWords);
		ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
	}

	/**
	 * Builds an analyzer which removes words in {@link #ENGLISH_STOP_WORDS_SET}
	 * .
	 * 
	 * @param matchVersion
	 *            See <a href="#version">above</a>
	 */
	public TweetgeisterAnalyzer(Version matchVersion) {
		stopWords = ENGLISH_STOP_WORDS_SET;
		enablePositionIncrements = StopFilter
				.getEnablePositionIncrementsVersionDefault(matchVersion);
	}

	/**
	 * Builds an analyzer with the stop words from the given set.
	 * 
	 * @param matchVersion
	 *            See <a href="#version">above</a>
	 * @param stopWords
	 *            Set of stop words
	 */
	public TweetgeisterAnalyzer(Version matchVersion, Set<?> stopWords) {
		this.stopWords = stopWords;
		enablePositionIncrements = StopFilter
				.getEnablePositionIncrementsVersionDefault(matchVersion);
	}

	/**
	 * Builds an analyzer with the stop words from the given file.
	 * 
	 * @see WordlistLoader#getWordSet(File)
	 * @param matchVersion
	 *            See <a href="#version">above</a>
	 * @param stopwordsFile
	 *            File to load stop words from
	 */
	public TweetgeisterAnalyzer(Version matchVersion, File stopwordsFile)
			throws IOException {
		stopWords = WordlistLoader.getWordSet(stopwordsFile);
		this.enablePositionIncrements = StopFilter
				.getEnablePositionIncrementsVersionDefault(matchVersion);
	}

	/**
	 * Builds an analyzer with the stop words from the given reader.
	 * 
	 * @see WordlistLoader#getWordSet(Reader)
	 * @param matchVersion
	 *            See <a href="#version">above</a>
	 * @param stopwords
	 *            Reader to load stop words from
	 */
	public TweetgeisterAnalyzer(Version matchVersion, Reader stopwords)
			throws IOException {
		stopWords = WordlistLoader.getWordSet(stopwords);
		this.enablePositionIncrements = StopFilter
				.getEnablePositionIncrementsVersionDefault(matchVersion);
	}

	/** Filters LowerCaseTokenizer with StopFilter. */
	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new TrailingPunctuationFilter(new StandardFilter(new LowerCaseFilter(new StopFilter(enablePositionIncrements, new WhitespaceQuoteTokenizer(
				reader), stopWords, true))));
	}

	/** Filters WhitespaceTokenizer with StopFilter. */
	private class SavedStreams {
		Tokenizer source;
		TokenStream result;
	};

	@Override
	public TokenStream reusableTokenStream(String fieldName, Reader reader)
			throws IOException {
		SavedStreams streams = (SavedStreams) getPreviousTokenStream();
		if (streams == null) {
			streams = new SavedStreams();
			streams.source = new WhitespaceQuoteTokenizer(reader);
			streams.result = new TrailingPunctuationFilter(new StandardFilter(new LowerCaseFilter(new StopFilter(enablePositionIncrements,
					streams.source, stopWords, true))));
			setPreviousTokenStream(streams);
		} else
			streams.source.reset(reader);
		return streams.result;
	}
}
