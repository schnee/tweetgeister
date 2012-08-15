package com.schnee.tweetgeister.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.search.TopDocs;

public class SearchFreq implements Comparable<SearchFreq> {
	private List<String> terms;
	private int docFreq;
	private TopDocs tds;

	public SearchFreq(List<String> termList, int docFreq, TopDocs tds) {
		super();
		this.terms = termList;
		this.docFreq = docFreq;
		this.tds = tds;
	}

	public List<String> getTerms() {
		return terms;
	}

	public int getDocFreq() {
		return docFreq;
	}

	public TopDocs getTds() {
		return tds;
	}

	public int compareTo(SearchFreq o) {
		if (docFreq > o.docFreq) {
			return 1;
		} else if (docFreq < o.docFreq) {
			return -1;
		} else {
			// compare the two lists...
			if (terms.size() > o.terms.size()) {
				return 1;
			} else if (terms.size() < o.terms.size()) {
				return -1;
			} else {
				//the are the same size
				List<String> sortedTerms = new ArrayList<String>(terms);
				List<String> sortedOTerms = new ArrayList<String>(o.getTerms());
				Collections.sort(sortedTerms);
				Collections.sort(sortedOTerms);
				
				for(int i = 0; i < sortedTerms.size(); i++){
					int compare = sortedTerms.get(i).compareTo(sortedOTerms.get(i));
					if(compare!=0){
						return compare;
					}
				}
				return 0;
			}
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		for (String term : terms) {
			sb.append(term).append(" ");
		}
		sb.append(docFreq);
		return sb.toString();
	}

}
