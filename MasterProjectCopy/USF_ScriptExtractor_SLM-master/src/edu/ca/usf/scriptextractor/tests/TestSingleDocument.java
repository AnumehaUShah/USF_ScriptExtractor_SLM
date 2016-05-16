package edu.ca.usf.scriptextractor.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import edu.ca.usf.scriptextractor.CharacterKeywordTransform;
import edu.ca.usf.scriptextractor.CompositeWordTransform;
import edu.ca.usf.scriptextractor.InputTransform;
import edu.ca.usf.scriptextractor.SLM;

public class TestSingleDocument {

	public static void main(String[] args){
		InputTransform inputTransform = new CompositeWordTransform();
		SLM benignModel = new SLM();
		SLM maliciousModel = new SLM();
		try {
			train(benignModel,new File("data/scripts/benign/100027918306303293559180742452221210154"),inputTransform);
			benignModel.dump();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String fileToString(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		StringBuffer script = new StringBuffer();
		int curChar;
		while ((curChar = fis.read()) != -1) {
			script.append((char) curChar);
		}
		String scriptString = script.toString().replaceAll("<\\/?(script|SCRIPT)[^>]*?>", "");
		return scriptString;
	}

	public static void train(SLM model, File inFile,InputTransform inputTransform)
			throws IOException {
		System.out.println(inputTransform.transform(fileToString(inFile)));
		try {
			model.add(inputTransform.transform(fileToString(inFile)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
