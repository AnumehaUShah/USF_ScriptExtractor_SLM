package edu.ca.usf.scriptextractor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents an instance of a Statistical Language Model
 * 
 */
public class SLM implements Serializable {

	private static final long serialVersionUID = -8945967390442185328L;

	private ProbabilityStrategy queryStrategy = null;

	/**
	 * We maintain two corpus counts, A and B. In the example of a tri-gram, we
	 * would ABC,BCD in countA, and AB,BC in countB.
	 * 
	 * tri-gram P(ABCD) ~= P(C|AB) * P(D|BC)
	 * 
	 */
	Map<TermSequence, Integer> countsAB;
	Map<TermSequence, Integer> countsA;

	int totalWordsA = 0;
	int totalWordsAB = 0;

	int wordLimit = 0;

	boolean modelClosed = false;

	double defaultProbabilitiy = 0;
	int nGramSize;

	/**
	 * Default constructor uses a trigram
	 */
	public SLM() {
		this(3);
	}

	/**
	 * Prevents future queries from affecting the probabilities, and culls the
	 * probabilities to the wordLimit
	 */
	public void closeModel() {
		modelClosed = true;
		
		countsA = getCulledWordCount(getSortedProbabilities(
				true, countsA),countsA);
		totalWordsA = sumTerms(countsA);
		defaultProbabilitiy = 1.0 / totalWordsA;
		/**
		 * The AB counts are only used if we have more than a unigram
		 */
		if (nGramSize > 1) {
			countsAB = getCulledWordCount(getSortedProbabilities(
					true, countsAB),countsAB);
			totalWordsAB = sumTerms(countsAB);
			defaultProbabilitiy = 1.0 / totalWordsAB;
		}

	}
	
	private int sumTerms(Map<TermSequence,Integer> counts){
		int sum = 0;
		for(Integer count: counts.values()){
			sum += count;
		}
		return sum;
	}

	private HashMap<TermSequence, Integer> getCulledWordCount(
			TreeMap<Double, List<TermSequence>> sorted,Map<TermSequence,Integer> counts) {
		HashMap<TermSequence, Integer> culledCounts = new HashMap<TermSequence, Integer>();
		int counter = 0;
		LOOP: for (Double probabilityKey : sorted.keySet()) {
			for (TermSequence curTermSequence : sorted.get(probabilityKey)) {
				culledCounts.put(curTermSequence, counts.get(curTermSequence));
				if (counter++ > wordLimit) {
					break LOOP;
				}
			}
		}
		return culledCounts;
	}

	public void setWordLimit(int wordLimit) {
		this.wordLimit = wordLimit;
	}

	public void setQueryStrategry(ProbabilityStrategy queryStrategry) {
		this.queryStrategy = queryStrategry;
	}

	public SLM(int nGramSize) {
		this.nGramSize = nGramSize;
		countsAB = new HashMap<TermSequence, Integer>();
		countsA = new HashMap<TermSequence, Integer>();
	}

	private void incrementCountAB(TermSequence sequence) {
		incrementCount(sequence, countsAB);
	}

	private void incrementCountA(TermSequence sequence) {
		incrementCount(sequence, countsA);
	}

	private void incrementCount(TermSequence sequence,
			Map<TermSequence, Integer> count) {
		if (count == countsA) {
			totalWordsA++;
		} else {
			totalWordsAB++;
		}

		if (count.containsKey(sequence)) {
			count.put(sequence, count.get(sequence) + 1);
		} else {
			count.put(sequence, 1);
		}
	}

	/**
	 * Adds an input string to the document
	 * 
	 * This method iterates over the string to add each term in the string to
	 * the corpus.
	 * 
	 * For example, given the sequence ABCDEF, and using a tri-gram, we would
	 * increment the term count for the following:
	 * 
	 * ABC,AB BCD,BC CDE,CD DEF,
	 * 
	 * @param input
	 * @throws Exception 
	 */
	public void add(List<Object> wordSequence) throws Exception {
		if(modelClosed){
			throw new Exception("Tried to add new sequence to closed model");
		}
		if (nGramSize == 1) {
			for (int i = 0; i < wordSequence.size(); i++) {
				incrementCountA(new TermSequence(new ArrayList(wordSequence.subList(i, i + 1))));
			}
		} else {
			for (int i = 0; i < wordSequence.size() - (nGramSize); i++) {
				TermSequence ab = new TermSequence(new ArrayList(wordSequence.subList(i, i
						+ (nGramSize))));

				incrementCountAB(ab);
				TermSequence a = new TermSequence(new ArrayList(wordSequence.subList(i, i
						+ nGramSize - 1)));
				incrementCountA(a);

			}
		}

	}

	private double getPAB(TermSequence wordSequence) {
		return getP(wordSequence, countsAB, totalWordsAB);
	}

	private double getPA(TermSequence wordSequence) {
		return getP(wordSequence, countsA, totalWordsA);
	}

	private double getP(TermSequence wordSequence,
			Map<TermSequence, Integer> count, int total) {

		if (queryStrategy != null) {
			double p = queryStrategy.getP(wordSequence, count, total);
			if (p >= 0) {
				return p;
			}
		}
		if (!count.containsKey(wordSequence)) {
			if (modelClosed) {
				return this.defaultProbabilitiy;
			}
			// +1 data smoothing
			incrementCount(wordSequence, count);
			total++;
		}

		double p = (double) count.get(wordSequence) / (double) total;

		return p;
	}

	public double query(List<Object> wordSequence) {
		double probability = 0.0f;

		if (nGramSize == 1) {
			double pA = 0.0;

			for (int i = 0; i < wordSequence.size(); i++) {
				TermSequence foo = new TermSequence(wordSequence.subList(i,
						i + 1));
				pA = getPA(foo);

				
				if (pA > java.lang.Float.MIN_VALUE) {
					probability += Math.log(pA);

				}
				
				if (Math.exp(probability) == 0.0) {
					// System.exit(0);
				}

			}
		} else {
			for (int i = 0; i <= wordSequence.size() - nGramSize; i++) {
				/**
				 * Calculates P(B|A)
				 * 
				 * This is calculated using the following equation: P(B|A) =
				 * P(A^B)/P(A)
				 */
				TermSequence a = new TermSequence(wordSequence.subList(i, i
						+ nGramSize - 1));

				TermSequence ab = new TermSequence(wordSequence.subList(i, i
						+ nGramSize));

				double pAB = getPAB(ab);

				double pA = getPA(a);

				if (pAB > java.lang.Float.MIN_VALUE
						&& pA > java.lang.Float.MIN_VALUE) {
					probability += Math.log(pAB / pA);
				}
			}

		}
		// System.out.println(probability);
		// return Math.exp(probability);
		return probability;
	}

	public void dump() {
		dump(0);
	}

	public TreeMap<Double, List<TermSequence>> getSortedProbabilities() {
		if (nGramSize == 1) {
			return getSortedProbabilities(true, countsA);
		} else {
			return getSortedProbabilities(true, countsAB);
		}
	}

	public TreeMap<Double, List<TermSequence>> getSortedProbabilities(
			boolean descending, Map<TermSequence, Integer> termCounts) {
		TreeMap<Double, List<TermSequence>> sortedP;
		if (descending) {
			sortedP = new TreeMap<Double, List<TermSequence>>(
					Collections.reverseOrder());
		} else {
			sortedP = new TreeMap<Double, List<TermSequence>>();

		}

		Double p;

		for (TermSequence ts : termCounts.keySet()) {
			p = getPAB(ts);
			if (!sortedP.containsKey(p)) {
				sortedP.put(p, new ArrayList<TermSequence>());
			}
			sortedP.get(p).add(ts);
		}
		return sortedP;
	}

	public void dump(int limit) {

		TreeMap<Double, List<TermSequence>> sortedP = getSortedProbabilities();
		int counter = 0;

		LOOP: for (Double pk : sortedP.keySet()) {
			for (TermSequence termSequence : sortedP.get(pk)) {
				if (limit == 0 || (counter++ < limit)) {
					System.out.println(pk + "," + termSequence);
				} else {
					break LOOP;
				}
			}
			// System.out.println(sortedP.get(pk));
		}
	}
}
