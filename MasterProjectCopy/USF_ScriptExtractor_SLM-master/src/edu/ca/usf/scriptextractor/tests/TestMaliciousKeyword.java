package edu.ca.usf.scriptextractor.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import edu.ca.usf.scriptextractor.AlphanumericUniformProbabilityStrategy;
import edu.ca.usf.scriptextractor.CharacterKeywordTransform;
import edu.ca.usf.scriptextractor.CharacterTransform;
import edu.ca.usf.scriptextractor.CompositeWordTransform;
import edu.ca.usf.scriptextractor.InputTransform;
import edu.ca.usf.scriptextractor.SLM;
import edu.ca.usf.scriptextractor.TermSequence;
import org.apache.commons.*;
import org.apache.commons.lang3.*;
public class TestMaliciousKeyword implements Runnable {
	Thread t;
	String dir;
	String thread_name;
	int file_limit;
	float trainPercent;
	int maliciousNGram;
	InputTransform maliciousTransform;
	SLM maliciousModel;
	
	
	TestMaliciousKeyword(String dir, String thread_name, int file_limit,int maliciousNGram,float trainPercent, InputTransform maliciousTransform, 
			SLM maliciousModel){
		
		t = new Thread(this, thread_name);
		this.dir = dir;
		this.file_limit = file_limit;
		this.trainPercent = trainPercent;
		this.maliciousNGram = maliciousNGram;
		this.maliciousTransform = maliciousTransform;
		this.maliciousModel = maliciousModel;
		
		t.start();
		
	}
	
	public static void main(String[] args) throws IOException {

		int maliciousNGram = 4;
		//InputTransform maliciousTransform = new CharacterTransform();
		InputTransform maliciousTransform = new CharacterKeywordTransform();
		SLM maliciousModel = new SLM(maliciousNGram);
		maliciousModel
				.setQueryStrategry(new AlphanumericUniformProbabilityStrategy());
		
	
		TestMaliciousKeyword thread1 = new TestMaliciousKeyword("dir1", "1", 1150, 4, 0.08f, maliciousTransform, maliciousModel);
		TestMaliciousKeyword thread2 = new TestMaliciousKeyword("dir2", "2", 1165, 4, 0.08f, maliciousTransform, maliciousModel);
		TestMaliciousKeyword thread3 = new TestMaliciousKeyword("dir3", "3", 1150, 4, 0.08f, maliciousTransform, maliciousModel);
		TestMaliciousKeyword thread4 = new TestMaliciousKeyword("dir4", "4", 1050, 4, 0.08f, maliciousTransform, maliciousModel);
		//TestMalicious thread5 = new TestMalicious("dir5", "5", 100, 4, 0.08f, maliciousTransform, maliciousModel);
		
		try {
			thread1.t.join();
			thread2.t.join();
			thread3.t.join();
			thread4.t.join();
			//thread5.t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/******* The code is added by anumeha shah for converting slm model to json****/
		JSONObject obj_maliciousCountsAB = new JSONObject();
		for(Map.Entry<TermSequence, Integer> entry : maliciousModel.countsAB.entrySet()){
			String key = entry.getKey().toString();
			key = key.substring(1, key.length()-1).replace(", ", "");
			
		
			int value = entry.getValue();
			
			if(value > 10000){
				
				
				System.out.printf("Key : %s and Value: %s %n", key, entry.getValue());
			}
		    try {
		    	if(value > 10)
		    		obj_maliciousCountsAB.put(key, value);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		FileWriter file_MaliciousCountsAB;
		try {
			file_MaliciousCountsAB = new FileWriter("KeywordMaliciousCountsAB.json");
			file_MaliciousCountsAB.write(obj_maliciousCountsAB.toString());
			file_MaliciousCountsAB.flush();
			file_MaliciousCountsAB.close(); 
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}                
		
		
	
		JSONObject obj_maliciousCountsA = new JSONObject();
		for(Map.Entry<TermSequence, Integer> entry : maliciousModel.countsA.entrySet()){
			String key = entry.getKey().toString();
			
			key = key.substring(1, key.length()-1).replace(", ", "");
			
			//System.out.println("key is" + key);
			try{
				key = StringEscapeUtils.unescapeJava(key);
			}
			catch(IllegalArgumentException ex){
				continue;
			}
			int value = entry.getValue();
		    //System.out.printf("Key : %s and Value: %s %n", entry.getKey().toString(), entry.getValue());
		    try {
		    	if(value > 10)
		    		obj_maliciousCountsA.put(key, value);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		FileWriter file_maliciousCountsA;
		try {
			file_maliciousCountsA = new FileWriter("KeywordMaliciousCountsA.json");
			file_maliciousCountsA.write(obj_maliciousCountsA.toString());
			file_maliciousCountsA.flush();
			file_maliciousCountsA.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}                
		
		
		JSONObject obj_total_malicious = new JSONObject();
		try {
			obj_total_malicious.put("totalWordsA", maliciousModel.totalWordsA);
			obj_total_malicious.put("totalWordsAB", maliciousModel.totalWordsAB);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		FileWriter file_totalMalicious;
		try {
			file_totalMalicious = new FileWriter("KeywordMaliciousTotal.json");
			file_totalMalicious.write(obj_total_malicious.toString());
			file_totalMalicious.flush();
			file_totalMalicious.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}                
		
		

		/******* The above code is added by anumeha shah for converting slm model to json****/
		
		
		
	}
	
	public void run(int maliciousNGram, float trainPercent, int fileLimit, InputTransform maliciousTransform, String dir) throws IOException {
		
		File maliciousDir = new File("/home2/ashah/malicious_50/"+ dir);
		//File maliciousDir = new File("/users/anumehashah/desktop/malicious/malicious_50/"+ dir);
		
		File[] maliciousFiles = maliciousDir.listFiles();

		List<File> maliciousTestScripts = new ArrayList<File>();

		
		int maliciousTrainCounter = 0;
		for (int i = 2; i < maliciousFiles.length && (i < fileLimit); i++) {
			System.out.println(i);
			train(maliciousModel, maliciousFiles[i], maliciousTransform);
		}

		System.out.println("maliciousTrainCounter , maliciousTestScript size = " +maliciousTrainCounter + ","
				+ maliciousTestScripts.size() + ",");
			
		
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
	}

	public static void train(SLM model, File inFile,
			InputTransform inputTransform) throws IOException {
		
		try {
			model.add(inputTransform.transform(fileToString(inFile)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		try {
			run(maliciousNGram, trainPercent,file_limit, maliciousTransform, dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}
	
	
}
