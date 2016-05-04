package edu.ca.usf.scriptextractor.tests;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import edu.ca.usf.scriptextractor.AlphanumericUniformProbabilityStrategy;
import edu.ca.usf.scriptextractor.CharacterKeywordTransform;
import edu.ca.usf.scriptextractor.InputTransform;
import edu.ca.usf.scriptextractor.SLM;
import edu.ca.usf.scriptextractor.TermSequence;


public class CalcProbVersusNgram {

	public static void main(String[] args) throws IOException {

		InputTransform transform = new CharacterKeywordTransform();
		// InputTransform benignTransform = new CompositeWordTransform();
		//File inputDir = new File("data/scripts/benign/");
		File inputDir = new File("C://Users//Chris//Documents//jsred//jsred");
		//File inputDir = new File("data/scripts/malicious/");
		for (int i = 1; i <= 4; i++) {
			run(i, inputDir, 1000, transform);
		}
	}

	private static void run(int nGramSize, File dir, int fileLimit,
			InputTransform inputTransform) throws IOException {

		SLM model = new SLM(nGramSize);
		model.setQueryStrategry(new AlphanumericUniformProbabilityStrategy());
		File[] inputFiles = dir.listFiles();

		//System.out.print(nGramSize + ",");

		for (int i = 0; i < inputFiles.length && (i < fileLimit); i++) {
			train(model, inputFiles[i], inputTransform);
		}
		TreeMap<Double, List<TermSequence>> desc = model
				.getSortedProbabilities();
		TreeMap<Double, List<TermSequence>> asc = model
		.getSortedProbabilities(false);
		double average = 0.0;
		
		int counter = 0;
		int limit = 100;
		boolean ascending = false;
		TreeMap<Double, List<TermSequence>> sortedSet;
		if(ascending){
			sortedSet = asc;
		} else {
			sortedSet = desc;
		}
		LOOP: for(Double pKey: sortedSet.keySet()){
			for(TermSequence sequence: sortedSet.get(pKey)){
				//System.out.println(sequence);
				if(counter ++ > limit){
					break LOOP;
				}
				average += pKey;
			}
		}
		System.out.println( average / 100.0);
		

	}

	public static String fileToString(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		StringBuffer script = new StringBuffer();
		int curChar;
		while ((curChar = fis.read()) != -1) {
			script.append((char) curChar);
		}
		String scriptString = script.toString().replaceAll(
				"<\\/?(script|SCRIPT)[^>]*?>", "");
		return scriptString;
		// System.out.println(scriptString);
		// return script.toString();
	}

	public static void train(SLM model, File inFile,
			InputTransform inputTransform) throws IOException {
		// WordClassTransform t = new WordClassTransform();
		// CompositeWordTransform t = new CompositeWordTransform();
		// List<WordClass> words = t.transform(inFile);
		// System.out.println(words);
		model.add(inputTransform.transform(fileToString(inFile)));
		// /model.add(fileToString(inFile));
		// model.add(t.transform(fileToString(inFile)));
	}
}
