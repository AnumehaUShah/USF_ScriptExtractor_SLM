package edu.ca.usf.scriptextractor;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CharacterTransform extends InputTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6271515393579058041L;

	@Override
	public List<Object> transform(String input) {
		List<Object> characters = new ArrayList<Object>();
		
		char[] inputAsChars = input.toCharArray();
		//System.out.println("here the lengh = " + inputAsChars.length);
		for(int i = 0; i < inputAsChars.length; i++){
			characters.add(inputAsChars[i]);
		}
		
		return characters;
	}

}
