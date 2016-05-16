package edu.ca.usf.scriptextractor;

import java.io.Serializable;

/**
 * Represents a word which belongs to a particular category
 * 
 * Comparison is done simply by comparing the class itself, rather than
 * any specific values.
 * 
 */
public class WordClass extends Term implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1093028714423825369L;

	enum WORDTYPE{WHITESPACE,PUNCTUATION,PASSTHROUGH,PERCENT,DIGIT,HEX};
	
	private WORDTYPE wordType;
	private String passthroughWord = "";
	private int value = 0;
	
	public WordClass(WORDTYPE wordType){
		this.wordType = wordType;
	}
	public WordClass(WORDTYPE wordType, int value){
		this(wordType);
		this.value = value;
	}
	public WORDTYPE getType(){
		return wordType;
	}
	public WordClass(String passthroughWord){
		this(WORDTYPE.PASSTHROUGH);
		this.passthroughWord = passthroughWord;
	}
	public boolean equals(Object o){
		if(! (o instanceof WordClass)){
			return false;
		}
		if(wordType == WORDTYPE.PASSTHROUGH){
			return ((WordClass)o).passthroughWord.equals(passthroughWord);
		} else if(wordType == WORDTYPE.PUNCTUATION){
			return ((WordClass)o).value == value;
		} else {
			return ((WordClass)o).wordType == wordType;
		}
	}
	public int hashCode(){
		return wordType.hashCode() ^ passthroughWord.hashCode();
	}
	
	public String toString(){
		if(wordType == WORDTYPE.PASSTHROUGH){
			return passthroughWord;
		} else if(wordType == WORDTYPE.PUNCTUATION){
			return String.valueOf((char)value);
		} else {
			return wordType.toString();
		}
	}
}
