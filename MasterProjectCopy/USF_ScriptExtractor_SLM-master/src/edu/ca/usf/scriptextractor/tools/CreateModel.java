package edu.ca.usf.scriptextractor.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class CreateModel {

	public static void main(String[] args) {
		Options cliOptions = new Options();
		Map<String, Object> options = new HashMap<String, Object>();
		int defaultNgramSize = 3;

		cliOptions
				.addOption(OptionBuilder
						.withArgName("directory")
						.hasArg()
						.withDescription(
								"Path to directory with benign files, or path to file containing a list of benign files")
						.withLongOpt("benign-path").create("bpath"));

		cliOptions
		.addOption(OptionBuilder
				.withArgName("file")
				.hasArg()
				.withDescription(
						"Destination for benign model")
				.withLongOpt("benign-model").create("bmodel"));
		
		cliOptions
		.addOption(OptionBuilder
				.withArgName("file")
				.hasArg()
				.withDescription(
						"Destination for malicious model")
				.withLongOpt("malicious-model").create("mmodel"));
		
		
		cliOptions
				.addOption(OptionBuilder
						.withArgName("directory")
						.hasArg()
						.withDescription(
								"Path to directory with malicious files, or path to file containing a list of malicious files")
						.withLongOpt("malicious-path").create("mpath"));

		cliOptions.addOption(OptionBuilder
				.withArgName("SIZE")
				.hasArg()
				.withDescription(
						"N-Gram Size (default " + defaultNgramSize + ")")
				.withLongOpt("ngram-size").create("g"));

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

		if (! cli.hasOption("bpath")|| !cli.hasOption("mpath")) {
			System.err.println("Benign and malicious paths are required");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java DetailedTestRunner", cliOptions);
			return;
		}
		
		options.put("benignPath", cli.getOptionValue("bpath"));
		options.put("maliciousPath", cli.getOptionValue("mpath"));
		options.put("benignModelPath", cli.getOptionValue("bmodel","benign.ser"));
		options.put("maliciousModelPath", cli.getOptionValue("mmodel","malicious.ser"));
		options.put("nGramSize",
				cli.getOptionValue("g", String.valueOf(defaultNgramSize)));

		SLM benignModel = new SLM(Integer.valueOf((String) options.get("nGramSize")));
		benignModel.setWordLimit(1000);
		SLM maliciousModel = new SLM(Integer.valueOf((String) options.get("nGramSize")));
		maliciousModel.setWordLimit(1000);
		maliciousModel
				.setQueryStrategry(new AlphanumericUniformProbabilityStrategy());

		InputTransform inputTransform = new CompositeWordTransform();
		File benignDir = new File((String) options.get("benignPath"));
		File maliciousDir = new File((String) options.get("maliciousPath"));
		int maxSize = 200000;

		trainModelFromDir(benignModel, inputTransform, benignDir, maxSize);
		trainModelFromDir(maliciousModel, inputTransform, maliciousDir, maxSize);
		
		benignModel.setWordLimit(2000);
		maliciousModel.setWordLimit(2000);
		benignModel.closeModel();
		maliciousModel.closeModel();
		
		saveModelToFile(benignModel, (String) options.get("benignModelPath"));
		saveModelToFile(maliciousModel, (String) options.get("maliciousModelPath"));
	      
	}

	private static void saveModelToFile(SLM model, String filename) {
		try
	      {
	         FileOutputStream fileOut =
	         new FileOutputStream(filename);
	         ObjectOutputStream out =
	                            new ObjectOutputStream(fileOut);
	         out.writeObject(model);
	         out.close();
	          fileOut.close();
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}

	private static void trainModelFromDir(SLM model,
			InputTransform inputTransform, File directory, int maxSize) {
		if (directory.isDirectory()) {
			for (File file : directory.listFiles()) {
				if (file.length() > maxSize) {
					System.out.println("Skipping benign file " + file.getName()
							+ ", size " + file.length() + "<" + maxSize);
					continue;
				}
				try {
					model.add(inputTransform.transform(fileToString(file)));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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
