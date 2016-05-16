package edu.ca.usf.scriptextractor;
import java.util.Map;


/**
 * Classes implementing the ProbabilityStrategy can
 * selectively calculate probabilities based on 
 * conditions specific to the algorithm being implemented.
 *
 */
public interface ProbabilityStrategy {

	/**
	 * Allows an algorithm to selectively calculate the probability
	 * 
	 * If the method determines that it will calculate the probability
	 * for the input, it will calculate and return the probability. If
	 * the method chooses not to calculate the probability, then the
	 * method returns a value less than 0. The calling code is then
	 * responsible to interpret a value of less than 0 as an indication
	 * that the strategy did not calculate a value, and that
	 * the calling code should use a default method.
	 * 
	 * @param wordSequence
	 * @param count
	 * @param total
	 * @return
	 */
	public double getP(TermSequence wordSequence,
			Map<TermSequence, Integer> count, int total);
}
