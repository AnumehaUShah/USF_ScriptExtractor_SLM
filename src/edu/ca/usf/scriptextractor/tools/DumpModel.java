package edu.ca.usf.scriptextractor.tools;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import edu.ca.usf.scriptextractor.SLM;

public class DumpModel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SLM benignModel = loadModel(args[0]);
		benignModel.dump();

	}
	private static SLM loadModel(String filename) {
		SLM model = null;

		FileInputStream fileIn;
		try {
			fileIn = new FileInputStream(filename);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			model = (SLM) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;

	}

}
