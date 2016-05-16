package edu.ca.usf.scriptextractor;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TermSequence implements Serializable{
		/**
	 * 
	 */
	private static final long serialVersionUID = 1872032119928292455L;
		List<Object> terms;

		public TermSequence(List<Object> terms) {
			this.terms = terms;
		}
		public List<Object> getTerms(){
			return terms;
		}


		@Override
		public int hashCode() {
			int hashCode = 0;
			for (Object term : terms) {
				hashCode += term.hashCode();
			}
			return hashCode;
		}

		public String toString() {
			return terms.toString();
		}

		public boolean equals(Object o) {
			if (o instanceof TermSequence) {
				return ((TermSequence) o).terms.equals(terms);
			} else {
				return false;
			}
		}
	}