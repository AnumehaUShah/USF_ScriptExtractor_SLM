package edu.ca.usf.scriptextractor.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.ca.usf.scriptextractor.AlphanumericUniformProbabilityStrategy;
import edu.ca.usf.scriptextractor.CharacterKeywordTransform;
import edu.ca.usf.scriptextractor.CompositeWordTransform;
import edu.ca.usf.scriptextractor.InputTransform;
import edu.ca.usf.scriptextractor.SLM;

public class DetailedTestRunner {
	private static Options cliOptions;
	private static Map<String, Object> options;
	private static Logger logger = Logger
			.getLogger("edu.ca.usf.scriptextractor.SLM");

	private static void ShowUsage() {

	}

	public static void main(String[] args) throws IOException {
		cliOptions = new Options();
		int defaultDocLimit = 1000;
		int defaultNgramSize = 3;
		int defaultNfoldSize = 5;
		String defaultLogFile = "detailedTestRunner.log";
		String defaultDetailFilePrefix = "slmOutput_";
		
		cliOptions.addOption(OptionBuilder.withArgName("directory").hasArg()
				.withDescription("Path to directory with benign files, or path to file containing a list of benign files")
				.withLongOpt("benign-path").create("bpath"));
		cliOptions.addOption(OptionBuilder.withArgName("directory").hasArg()
				.withDescription("Path to directory with malicious files, or path to file containing a list of malicious files")
				.withLongOpt("malicious-path").create("mpath"));
		cliOptions.addOption(OptionBuilder
				.withArgName("STRING")
				.hasArg()
				.withDescription(
						"Classification details file prefix (default " + defaultDetailFilePrefix + ")")
				.withLongOpt("details-prefix").create("p"));
		cliOptions.addOption(OptionBuilder
				.withArgName("file")
				.hasArg()
				.withDescription(
						"Log file path (default " + defaultLogFile + ")")
				.withLongOpt("log-file").create("l"));
		cliOptions.addOption(OptionBuilder
				.withArgName("COUNT")
				.hasArg()
				.withDescription(
						"Number of documents from benign and malicious each to test (default "
								+ defaultDocLimit + ")")
				.withLongOpt("doc-limit").create("c"));
		cliOptions.addOption(OptionBuilder
				.withArgName("SIZE")
				.hasArg()
				.withDescription(
						"N-Gram Size (default " + defaultNgramSize + ")")
				.withLongOpt("ngram-size").create("g"));
		cliOptions.addOption(OptionBuilder
				.withArgName("COUNT")
				.hasArg()
				.withDescription(
						"Number of n-folds to test (default "
								+ defaultNfoldSize + ")")
				.withLongOpt("n-fold-count").create("n"));

		options = new HashMap<String, Object>();
		CommandLineParser cliParser = new BasicParser();
		CommandLine cli = null;
		try {
			cli= cliParser.parse(cliOptions, args);
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
		
		options.put("benignPath",cli.getOptionValue("bpath"));
		options.put("maliciousPath",cli.getOptionValue("mpath"));
		options.put("nFoldCount",  cli.getOptionValue("n",String.valueOf(defaultNfoldSize)));
		options.put("nGramSize",  cli.getOptionValue("g",String.valueOf(defaultNgramSize)));
		options.put("detailsPrefix",  cli.getOptionValue("p",defaultDetailFilePrefix));
		options.put("logFile",  cli.getOptionValue("l",defaultLogFile));
		options.put("docLimit",  cli.getOptionValue("c",String.valueOf(defaultDocLimit)));
		
		Handler fh = new FileHandler((String) options.get("logFile"));
		Logger.getLogger("").addHandler(fh);
		Logger.getLogger("edu.ca.usf.scriptextractor.SLM").setLevel(
				Level.FINEST);
		
		run(Integer.valueOf((String)options.get("nGramSize")),Integer.valueOf((String)options.get("nFoldCount")));
	}

	private static Queue<File> createFileListFromFile(File listingsFile) throws IOException{
		Queue<File> fileList = new LinkedList<File>();
		String line;
		FileInputStream fis = new FileInputStream(listingsFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		while ((line = br.readLine()) != null) {
		    fileList.add(new File(line));
		}
		
		return fileList;
	}
	private static void run(int nGramSize, int nFoldTestNumber)
			throws IOException {

		int docLimit = Integer.valueOf((String)options.get("docLimit"));
		File benignDir = new File((String) options.get("benignPath"));
		
		File maliciousDir = new File(
				(String) options.get("maliciousPath"));
		// InputTransform inputTransform = new CharacterKeywordTransform();
		InputTransform inputTransform = new CompositeWordTransform();
		Queue<File> benignFiles = new LinkedList<File>();
		Queue<File> maliciousFiles = new LinkedList<File>();
		Queue<File> benignTestFiles;
		Queue<File> maliciousTestFiles;

		int maxSize = 100000;
		List<File> allFiles = new ArrayList<File>();
		Random r = new Random();
		if(benignDir.isDirectory()){
			logger.fine("Reading benign files from directory " + benignDir);
			for (File file : benignDir.listFiles()) {
				if (file.length() > maxSize) {
					logger.fine("Skipping benign file " + file.getName()
							+ ", size " + file.length() + "<" + maxSize);
					continue;
				}
				allFiles.add(file);
			}
			for (int i = 0; i < docLimit; i++) {
				if (allFiles.size() == 0) {
					break;
				}
	
				benignFiles.offer(allFiles.remove(r.nextInt(allFiles.size())));
			}
		} else {
			logger.fine("Reading benign files from file " + benignDir);
			benignFiles = createFileListFromFile(benignDir);
		}

		if(maliciousDir.isDirectory()){
			logger.fine("Reading malicious files from directory " + benignDir);
			for (File file : maliciousDir.listFiles()) {
				if (file.length() > maxSize) {
					logger.fine("Skipping malicious file " + file.getName()
							+ ", size " + file.length() + "<" + maxSize);
					continue;
				}
				allFiles.add(file);
			}
			for (int i = 0; i < docLimit; i++) {
				if (allFiles.size() == 0) {
					break;
				}
				// logger.finest("Adding " + file.getName() + " to malicious list");
				maliciousFiles.offer(allFiles.remove(r.nextInt(allFiles.size())));
			}
		} else {
			logger.fine("Reading malicious files from file " + benignDir);
			maliciousFiles = createFileListFromFile(maliciousDir);
		}
		
		int benignTestFileCount = benignFiles.size() / nFoldTestNumber;
		int maliciousTestFileCount = maliciousFiles.size() / nFoldTestNumber;

		double benignP, maliciousP;

		for (int curNfoldCount = 0; curNfoldCount < nFoldTestNumber; curNfoldCount++) {
			FileWriter fstream = new FileWriter( ((String)options.get("detailsPrefix")) + curNfoldCount
					+ ".csv");
			PrintWriter out = new PrintWriter(fstream);
			out.println("ngramsize,nfoldcount,bTestSize,bTrainSize,mTestSize,mTrainSize,class,filename,filesize,pBenign,pMalicious,pDelta,classifiedAs,truePositive,trueNegative,falsePositive,falseNegative");
			logger.finest("Testing nfold " + curNfoldCount + " of "
					+ nFoldTestNumber);
			// Set up test file list
			benignTestFiles = new LinkedList<File>();
			separateTestFiles(benignFiles, benignTestFiles, benignTestFileCount);

			maliciousTestFiles = new LinkedList<File>();
			separateTestFiles(maliciousFiles, maliciousTestFiles,
					maliciousTestFileCount);

			SLM benignModel = new SLM(nGramSize);
			benignModel.setWordLimit(1000);
			SLM maliciousModel = new SLM(nGramSize);
			maliciousModel.setWordLimit(1000);
			maliciousModel
					.setQueryStrategry(new AlphanumericUniformProbabilityStrategy());

			logger.finest("Training benign model");
			// Train models
			for (File file : benignFiles) {
				logger.finest("Training benign file " + file.getName()+ ", size "
						+ file.length());
				logger.finest("Memory free:" + Runtime.getRuntime().freeMemory());
				train(benignModel, file, inputTransform);
			}
			logger.finest("Training malicious model");
			for (File file : maliciousFiles) {
				logger.finest("Training malicious file" + file.getName() + ", size "
						+ file.length());
				logger.finest("Memory free:" + Runtime.getRuntime().freeMemory());
				train(maliciousModel, file, inputTransform);
			}
			benignModel.closeModel();
			maliciousModel.closeModel();

			/*
			 * True Positive = Malicious identified as such False Positive =
			 * Benign identified as malicious True Negative = Benign identified
			 * as such False Negative = Malicious identified as benign
			 */
			for (File file : benignTestFiles) {
				logger.finest("Testing benign " + file.getName());
				out.print(nGramSize + ",");
				out.print((curNfoldCount + 1) + ",");
				out.print(benignTestFiles.size() + "," + benignFiles.size()
						+ ",");
				out.print(maliciousTestFiles.size() + ","
						+ maliciousFiles.size() + ",");
				out.print("benign,");
				out.print("\"" + file.getName() + "\"" + ",");
				String script = fileToString(file);

				if (script.length() == 0) {
					out.print("0,0,0,0,blank,\n");
					continue;
				} else if (script.length() < 50) {
					out.print(script.length() + ",0,0,0,tooshort,\n");
					continue;
				} else {
					out.print(script.length() + ",");
				}

				benignP = benignModel.query(inputTransform.transform(script));
				maliciousP = maliciousModel.query(inputTransform
						.transform(script));

				if (benignP == 0) {
					int foobar = 1;
					foobar++;
				}
				out.print(benignP + "," + maliciousP + ","
						+ (Math.abs(Math.abs(benignP) - Math.abs(maliciousP)))
						+ ",");

				if (benignP > maliciousP) {
					out.print("trueNegative,0,1,0,0");
				} else {
					out.print("falsePositive,0,0,1,0");
				}
				out.println("");

			}
			for (File file : maliciousTestFiles) {
				logger.finest("Testing malicious " + file.getName());
				out.print(nGramSize + ",");
				out.print((curNfoldCount + 1) + ",");
				out.print(benignTestFiles.size() + "," + benignFiles.size()
						+ ",");
				out.print(maliciousTestFiles.size() + ","
						+ maliciousFiles.size() + ",");
				out.print("malicious,");
				out.print("\"" + file.getName() + "\"" + ",");
				String script = fileToString(file);

				if (script.length() == 0) {
					out.print("0,0,0,0,blank,\n");
					continue;
					/*
					 * } else if(script.length() < 100){
					 * out.print(script.length() + ",0,0,0,tooshort,\n");
					 * continue;
					 */
				} else {
					out.print(script.length() + ",");
				}

				benignP = benignModel.query(inputTransform.transform(script));
				maliciousP = maliciousModel.query(inputTransform
						.transform(script));

				out.print(benignP + "," + maliciousP + ","
						+ (Math.abs(Math.abs(benignP) - Math.abs(maliciousP)))
						+ ",");

				if (benignP > maliciousP) {
					out.print("falseNegative,0,0,0,1");
				} else {
					out.print("truePositive,1,0,0,0");
				}
				out.println("");
			}
			out.close();
			/*
			 * System.out.println("Benign model top 1000:");
			 * benignModel.dump(1000);
			 * System.out.println("Malicious model top 1000:");
			 * maliciousModel.dump(1000);
			 */
			benignFiles.addAll(benignTestFiles);
			maliciousFiles.addAll(maliciousTestFiles);

		}
	}

	private static void separateTestFiles(Queue<File> sourceFiles,
			Queue<File> destinationTestFiles, int testFileCount) {
		for (int j = 0; j < testFileCount; j++) {
			destinationTestFiles.offer(sourceFiles.poll());
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

	public static void train(SLM model, File inFile,
			InputTransform inputTransform) throws IOException {
		// WordClassTransform t = new WordClassTransform();
		// CompositeWordTransform t = new CompositeWordTransform();
		// List<WordClass> words = t.transform(inFile);
		// System.out.println(words);
		try {
			model.add(inputTransform.transform(fileToString(inFile)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// /model.add(fileToString(inFile));
		// model.add(t.transform(fileToString(inFile)));
	}
}
