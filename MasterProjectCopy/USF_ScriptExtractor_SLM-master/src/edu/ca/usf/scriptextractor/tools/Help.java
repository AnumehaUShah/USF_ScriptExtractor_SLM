package edu.ca.usf.scriptextractor.tools;

public class Help {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Usage: java -cp USF_ScriptExtractor_SLM <classname> <arguments>");
		System.out.println("Available tools:");
		System.out.println("\tedu.ca.usf.scriptextractor.tools.Classify");
		System.out.println("\t\tClassify a directory of files given serialized SLM models");
		System.out.println("\tedu.ca.usf.scriptextractor.tools.CreateModel");
		System.out.println("\t\tTrain and serialize benign and malicious models based on training data");

	}

}
