package edu.ca.usf.scriptextractor.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ca.usf.scriptextractor.AlphanumericUniformProbabilityStrategy;
import edu.ca.usf.scriptextractor.CharacterKeywordTransform;
import edu.ca.usf.scriptextractor.CharacterTransform;
import edu.ca.usf.scriptextractor.CompositeWordTransform;
import edu.ca.usf.scriptextractor.InputTransform;
import edu.ca.usf.scriptextractor.SLM;

public class Test {

	public static void main(String[] args) throws IOException {

		InputTransform benignTransform = new CharacterTransform();
		//InputTransform benignTransform = new CharacterKeywordTransform();
		 InputTransform maliciousTransform = new CharacterTransform();
		//InputTransform maliciousTransform = new CharacterKeywordTransform();

		// System.out.println("Benign N-Gram Size, Malicious N-Gram Size, Benign Train Count, Benign Test Count, Malicious ")
		for (int i = 1; i <= 4; i++) {
			//for (int k = 1; k <= 10; k++) {
				// for (int j = 0; j <= 5; j++) {
				run(i, i, 0.8f, 10000, benignTransform, maliciousTransform);
			//}
			// }
		}
	}

	private static void run(int benignNGram, int maliciousNGram,
			float trainPercent, int fileLimit, InputTransform benignTransform,
			InputTransform maliciousTransform) throws IOException {
		// int nGramSize = 3;
		// float trainPercent = 0.8f;

		// CharacterTransform t = new CharacterTransform();
		// WordClassTransform t = new WordClassTransform();
		// CompositeWordTransform t = new CompositeWordTransform();
		File benignDir = new File("data/scripts/benign/");
		// File benignDir = new File("testData/benign/");
		File maliciousDir = new File("data/scripts/malicious/");

		File[] benignFiles = benignDir.listFiles();
		File[] maliciousFiles = maliciousDir.listFiles();

		SLM benignModel = new SLM(benignNGram);
		SLM maliciousModel = new SLM(maliciousNGram);
		maliciousModel
				.setQueryStrategry(new AlphanumericUniformProbabilityStrategy());

		List<File> benignTestScripts = new ArrayList<File>();
		List<File> maliciousTestScripts = new ArrayList<File>();

		System.out.print(benignNGram + "," + maliciousNGram + ",");
		int benignTrainCounter = 0;

		for (int i = 0; i < benignFiles.length && (i < fileLimit); i++) {
			// for(int i = 0; i < maliciousFiles.length; i++){
			if (Math.random() < trainPercent) {
				benignTrainCounter++;
				train(benignModel, benignFiles[i], benignTransform);
			} else {
				benignTestScripts.add(benignFiles[i]);
			}
		}

		int maliciousTrainCounter = 0;
		for (int i = 0; i < maliciousFiles.length && (i < fileLimit); i++) {
			if (Math.random() < trainPercent) {
				maliciousTrainCounter++;
				train(maliciousModel, maliciousFiles[i], maliciousTransform);
			} else {
				maliciousTestScripts.add(maliciousFiles[i]);
			}
		}

		// maliciousModel.dump();
		// if(true){return;}

		System.out.print(benignTrainCounter + "," + benignTestScripts.size()
				+ ",");

		System.out.print(maliciousTrainCounter + ","
				+ maliciousTestScripts.size() + ",");

		double trueBenign = 0;
		double trueMalicious = 0;
		double falseBenign = 0;
		double falseMalicious = 0;
		double benignP, maliciousP;

		double averageBenignScriptBenignModelScore = 0.0;
		double averageBenignScriptMaliciouscModelScore = 0.0;
		for (int i = 0; i < benignTestScripts.size(); i++) {
			String script = fileToString(benignTestScripts.get(i));
			// System.out.println(t.transform(benignTestScripts.get(i)));
			benignP = benignModel.query(benignTransform.transform(script));
			maliciousP = maliciousModel.query(maliciousTransform
					.transform(script));
			averageBenignScriptBenignModelScore += benignP;
			averageBenignScriptMaliciouscModelScore += maliciousP;
			if (benignP > maliciousP) {
				trueBenign++;
			} else {
				falseMalicious++;
			}

		}
		averageBenignScriptBenignModelScore = averageBenignScriptBenignModelScore
				/ (double) benignTestScripts.size();
		averageBenignScriptMaliciouscModelScore = averageBenignScriptMaliciouscModelScore
				/ (double) benignTestScripts.size();

		double averageMaliciousScriptBenignModelScore = 0.0;
		double averageMaliciousScriptMaliciousModelScore = 0.0;
		for (int i = 0; i < maliciousTestScripts.size(); i++) {
			benignP = benignModel.query(benignTransform
					.transform(fileToString(maliciousTestScripts.get(i))));
			maliciousP = maliciousModel.query(benignTransform
					.transform(fileToString(maliciousTestScripts.get(i))));
			// System.out.println(fileToString(maliciousTestScripts.get(i)));
			// System.out.println("P(malicious) = " + maliciousP);
			// System.out.println("P(benign) = " + benignP);
			averageMaliciousScriptBenignModelScore += benignP;
			averageMaliciousScriptMaliciousModelScore += maliciousP;
			if (maliciousP > benignP) {
				trueMalicious++;
			} else {
				falseBenign++;
			}
		}

		averageMaliciousScriptBenignModelScore = averageMaliciousScriptBenignModelScore
				/ (double) maliciousTestScripts.size();
		averageMaliciousScriptMaliciousModelScore = averageMaliciousScriptMaliciousModelScore
				/ (double) maliciousTestScripts.size();

		double accuracy = (trueBenign + trueMalicious)
				/ (trueBenign + trueMalicious + falseBenign + falseMalicious);
		double benignPrecision = trueBenign / (trueBenign + falseBenign);
		double maliciousPrecision = trueMalicious
				/ (trueMalicious + falseMalicious);
		System.out.println(trueBenign + "," + falseMalicious + ","
				+ trueMalicious + "," + falseBenign + "," + accuracy + ","
				+ benignPrecision + "," + maliciousPrecision + ","
				+ averageBenignScriptBenignModelScore + ","
				+ averageBenignScriptMaliciouscModelScore + "," 
				+ averageMaliciousScriptBenignModelScore + ","
				+ averageMaliciousScriptMaliciousModelScore);
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
