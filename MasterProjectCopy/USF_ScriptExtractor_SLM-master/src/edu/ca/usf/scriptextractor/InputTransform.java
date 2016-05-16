package edu.ca.usf.scriptextractor;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;


abstract public class InputTransform implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4832374163036191694L;

	abstract public List<Object> transform(String input);
}
