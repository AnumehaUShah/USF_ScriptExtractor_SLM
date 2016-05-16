package edu.ca.usf.scriptextractor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Transform the input into a list of WordClass objects for special word groups,
 * and PassthroughWord objects for the remainder
 * 
 * @author Chris
 * 
 */
public class CompositeWordTransform extends InputTransform implements Serializable{


	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5300299482626832076L;


	public CompositeWordTransform(){
    	
    }
	@Override
	public List<Object> transform(String input){
		List<Object> words = new ArrayList<Object>();
		StringBuffer currentWord = new StringBuffer();

	

		
		int cur;
		for(int i = 0; i < input.length(); i++){
			cur = input.charAt(i);
			if (cur == 32 || cur == 9 || cur == 13) {
				//ignore whitespace
				
				addWord(words, new WordClass(WordClass.WORDTYPE.WHITESPACE),
						currentWord);
			} else if (cur < 32) {

				addWord(words, new WordClass(WordClass.WORDTYPE.PUNCTUATION,cur),
						currentWord);

			} else if (cur == 37) {
				addWord(words, new WordClass(WordClass.WORDTYPE.PERCENT),
						currentWord);
			} else if (cur < 48) {
				addWord(words, new WordClass(WordClass.WORDTYPE.PUNCTUATION,cur),
						currentWord);
			} else if (cur < 58) {
				currentWord.append((char) cur);
			} else if (cur < 65) {
				addWord(words, new WordClass(WordClass.WORDTYPE.PUNCTUATION,cur),
						currentWord);
			} else if (cur < 91) {
				currentWord.append((char) cur);
			} else if (cur < 97) {
				addWord(words, new WordClass(WordClass.WORDTYPE.PUNCTUATION,cur),
						currentWord);
				
			} else if (cur < 123) {
				currentWord.append((char) cur);
			} else if (cur < 127) {
				addWord(words, new WordClass(WordClass.WORDTYPE.PUNCTUATION,cur),
						currentWord);
			}
		}

		// collapse
		return words;
	}
	
	
	private void addWord(List<Object> words, WordClass word,
			StringBuffer currentWord) {
		/*
		 * // don't count multiple punctuation if (word.getType() ==
		 * WordClass.WORDTYPE.PUNCTUATION && words.size() > 0 &&
		 * words.get(words.size() - 1).getType() ==
		 * WordClass.WORDTYPE.PUNCTUATION) { //
		 * System.out.println("Skipping punctuation"); return; }
		 */
		if (currentWord.length() > 0) {
			String currentWordAsString = currentWord.toString();
			if (Pattern.matches("[0-9]+", currentWordAsString)) {
				words.add(new WordClass(WordClass.WORDTYPE.DIGIT));

			} else if (Pattern.matches(".[a-fA-F0-9][a-fA-F0-9]+",
					currentWordAsString)) {
				// Match hex string like R84c84c88c14
				for (int i = 0; i < currentWordAsString.length() / 4; i++) {
					words.add(new WordClass(WordClass.WORDTYPE.HEX));
				}
			} else {
				words.add(new WordClass(currentWord.toString()));
			}
		}
		currentWord.setLength(0);
		words.add(word);
	}


}
