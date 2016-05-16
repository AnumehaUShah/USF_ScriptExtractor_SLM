package edu.ca.usf.scriptextractor.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.ca.usf.scriptextractor.AlphanumericUniformProbabilityStrategy;
import edu.ca.usf.scriptextractor.CompositeWordTransform;
import edu.ca.usf.scriptextractor.InputTransform;
import edu.ca.usf.scriptextractor.SLM;

public class Classify {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Options cliOptions = new Options();
		Map<String, Object> options = new HashMap<String, Object>();
		int defaultNgramSize = 3;

		cliOptions.addOption(OptionBuilder.withArgName("directory").hasArg()
				.withDescription("Path to directory of files to classify")
				.withLongOpt("input-path").create("inputDir"));

		cliOptions.addOption(OptionBuilder.withArgName("file").hasArg()
				.withDescription("Path to serialized benign model")
				.withLongOpt("benign-path").create("bpath"));

		cliOptions.addOption(OptionBuilder.withArgName("file").hasArg()
				.withDescription("Path to serialized malicious model")
				.withLongOpt("malicious-path").create("mpath"));

		CommandLineParser cliParser = new BasicParser();
		CommandLine cli = null;
		try {
			cli = cliParser.parse(cliOptions, args);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			System.err.println("Unable to parse command line arguments:"
					+ e1.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java DetailedTestRunner", cliOptions);
			return;
		}

		if (!cli.hasOption("bpath") || !cli.hasOption("mpath")) {
			System.err.println("Benign and malicious paths are required");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java DetailedTestRunner", cliOptions);
			return;
		}
		if (!cli.hasOption("inputDir") || !cli.hasOption("mpath")) {
			System.err.println("Path to files to be classified is required");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java DetailedTestRunner", cliOptions);
			return;
		}

		SLM benignModel = loadModel(cli.getOptionValue("bpath"));
		SLM maliciousModel = loadModel(cli.getOptionValue("mpath"));

		InputTransform inputTransform = new CompositeWordTransform();
		int maxSize = 200000;
		File classifyDir = new File(cli.getOptionValue("inputDir"));

		double benignP,maliciousP;
		
		System.out.println("filename,maliciousP,benignP");
		for (File file : classifyDir.listFiles()) {
			if (file.length() > maxSize) {
				System.out.println("Skipping benign file " + file.getName()
						+ ", size " + file.length() + "<" + maxSize);
				continue;
			} else {
				
				try {
					maliciousP = maliciousModel.query(inputTransform
							.transform(fileToString(file)));
					benignP = benignModel.query(inputTransform.transform(fileToString(file)));
					System.out.println(file.getName() + "," + maliciousP + "," + benignP);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

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

	public static String fileToString(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		StringBuffer script = new StringBuffer();
		int curChar;
		while ((curChar = fis.read()) != -1) {
			script.append((char) curChar);
		}
		String scriptString = script.toString().replaceAll(
				"(<script|<SCRIPT|<\\/script>|<\\/SCRIPT>)", "");
		return scriptString;
		// System.out.println(scriptString);
		// return script.toString();
	}
}
