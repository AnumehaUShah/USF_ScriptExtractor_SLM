package edu.ca.usf.scriptextractor;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Treats alpha-numeric sequences as uniformly probable.
 * 
 * If the term sequence is made up entirely of Character
 * objects, all of which have alpha-numeric values, then
 * the probability is (1/62)^n, where n is the length of
 * the sequence
 *
 */
public class AlphanumericUniformProbabilityStrategy implements
		ProbabilityStrategy , Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7517197510546509475L;
	private static double UniformProb = 1.0 / 62.0;
	private static Set<Character> AlphaNumSet;
	
	static {
		AlphaNumSet = new HashSet<Character>();
		//0-9
		for(int i = 48; i <= 57; i++){
			AlphaNumSet.add((char)i);
		}
		//A-Z
		for(int i = 65; i <= 90; i++){
			AlphaNumSet.add((char)i);
		}
		//a-z
		for(int i = 97; i <= 122; i++){
			AlphaNumSet.add((char)i);
		}
	}
	@Override
	public double getP(TermSequence wordSequence,
			Map<TermSequence, Integer> count, int total) {
	
		double p = 1.0;
		
		for(Object term: wordSequence.getTerms()){
			if(term instanceof Character){
				if(AlphaNumSet.contains(term)){
					p *= UniformProb;
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		}
		
		//System.out.println("P(" + wordSequence + ") = " + p);
		return p;
		
	}

}
