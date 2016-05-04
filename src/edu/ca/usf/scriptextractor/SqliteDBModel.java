package edu.ca.usf.scriptextractor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;


public class SqliteDBModel {

	public static void main(String[] args) throws IOException, SQLiteException {
		SQLiteConnection db = new SQLiteConnection(new File(
				"C:/Users/Chris/usf.db"));
		db.open(true);

		int learnScriptCount = 90;
		int testScriptCount = 10;
		int docCount = 8;
		SQLiteStatement st = db
				.prepare("SELECT S.script FROM documents D JOIN scripts S ON D.id = S.document_fk WHERE S.classification = '2009:malicious' OR S.classification = 'manual:malicious' LIMIT " + learnScriptCount);
				//.prepare("SELECT S.script FROM documents D JOIN scripts S ON D.id = S.document_fk WHERE S.classification = 'wepawet:benign' OR S.classification = 'ej_benign' LIMIT " + learnScriptCount);
		long startTime = System.currentTimeMillis();
		List<SLM> docs = new ArrayList<SLM>();
		List<String> scripts = new ArrayList<String>();
		CharacterKeywordTransform c = new CharacterKeywordTransform();
		try {


			
			for(int i = 0; i < docCount; i++){
				docs.add(new SLM( (int) (2 + Math.pow(2, i))));
			}
			int counter= 0;
			while (st.step()) {
				List<Object> script = c.transform(st.columnString(0));
	
				
				for(SLM doc : docs){
					doc.add(script);
				}
				counter++;
				System.out.println("Learned script " + counter);
			}
			
			 st = db
				.prepare("SELECT S.script FROM documents D JOIN scripts S ON D.id = S.document_fk WHERE S.classification = '2009:malicious' OR S.classification = 'manual:malicious' LIMIT " + learnScriptCount + "," + testScriptCount);
				//.prepare("SELECT S.script FROM documents D JOIN scripts S ON D.id = S.document_fk WHERE S.classification = 'wepawet:benign' OR S.classification = 'ej_benign' LIMIT " + learnScriptCount + "," + testScriptCount);
			 counter = 0;
			 while (st.step()) {
				 List<Object> script = c.transform(st.columnString(0));
				 //scripts.add(script);
				 System.out.println("Saving script " + counter);
				 
			 }
		
			for(int i = 0; i < docs.size(); i++){
				double total = 0.0;
				
				for(int j = 0; j < scripts.size(); j++){
					//total += docs.get(i).query(scripts.get(j));
				}
				total = total / (double)scripts.size();
				System.out.println("n = " + (int) (2 + Math.pow(2, i)) + ", P: " + total);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			st.dispose();
		}

		long endTime = System.currentTimeMillis();
		
		db.dispose();
	}
}
