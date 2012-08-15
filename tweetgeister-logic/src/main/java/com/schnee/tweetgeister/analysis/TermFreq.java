package com.schnee.tweetgeister.analysis;

import org.apache.lucene.index.Term;

public class TermFreq implements Comparable<TermFreq> {
	private Term term;
	private int docFreq;
	
	public TermFreq(Term term, int docFreq) {
		super();
		this.term = term;
		this.docFreq = docFreq;
	}

	public Term getTerm() {
		return term;
	}

	public int getDocFreq() {
		return docFreq;
	}

	public int compareTo(TermFreq o) {
		if(docFreq > o.docFreq){
			return 1;
		} else if (docFreq < o.docFreq){
			return -1;
		} else {
			return (term.text().compareTo(o.term.text()));
		}
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + docFreq;
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TermFreq other = (TermFreq) obj;
		if (docFreq != other.docFreq)
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TermFreq [docFreq=" + docFreq + ", term=" + term.text() + "]";
	}
	
	

}
