package edu.ca.usf.scriptextractor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

public class SqlToFile {

	public static void main(String[] arg) throws SQLiteException, NoSuchAlgorithmException, IOException {
		SQLiteConnection db = new SQLiteConnection(new File(
				"C:/Users/Chris/usf.db"));
		db.open(true);
		
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		SQLiteStatement st = db.prepare("SELECT S.script,S.classification FROM " +
				"documents D JOIN scripts S ON D.id = S.document_fk " +
				"WHERE S.classification IN ('2009:malicious','manual:malicious','wepawet:benign','ej_benign')");
		FileWriter fstream;
		while (st.step()) {
			String script = st.columnString(0);
			String classification = st.columnString(1);
			String filename = "C:/Users/Chris/workspace/USF_ScriptExtractor_SLM/data/scripts/";
			if(classification.equals("ej_benign") || classification.equals("wepawet:benign")){
				filename += "benign/" + (new BigInteger(1,md5.digest(script.getBytes()))).toString();
			} else {
				filename += "malicious/" + (new BigInteger(1,md5.digest(script.getBytes()))).toString();
			}
			fstream = new FileWriter(filename,true);
			fstream.write(script);
			fstream.close();
		}
		
	}
}
