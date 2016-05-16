package edu.ca.usf.scriptextractor.tests;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

public class TestAddOn {
	
	public static void main(String[] args){
		
		int fileLimit = 1000;
		File maliciousDir1 = new File("/Users/anumehashah/desktop/malicious/Testing_data/expjs/");
		File maliciousDir2 = new File("/Users/anumehashah/desktop/malicious/Testing_data/red1/");
		File benignDir = new File("/Users/anumehashah/desktop/finalScripts/benign_dir/benign_100/dir4/");
		
		File[] maliciousFiles1 = maliciousDir1.listFiles();
		File[] maliciousFiles2 = maliciousDir2.listFiles();
		File[] benignFiles = benignDir.listFiles();
		
		
		String maliciousOpFile = "M40.html";
		
		try {
			/*FileWriter MfWriter = new FileWriter(maliciousOpFile, true);
			BufferedWriter MbufferWritter = new BufferedWriter(MfWriter);
			MbufferWritter.write("<html>");
			MbufferWritter.write("<head>");
			for (int i = 475 ; i <= 500 && (i < fileLimit); i++) {
				
				String text1 = fileToString(maliciousFiles1[i]);
				String text2 = fileToString(maliciousFiles2[i]);
				if(! text1.matches("<\\/?(script|SCRIPT)[^>]*?>")){
					MbufferWritter.write("<script>");
					MbufferWritter.write(text1);
					MbufferWritter.write("</script>");
				}
				else{
					MbufferWritter.write(text1);
				}
				
				if(! text2.matches("<\\/?(script|SCRIPT)[^>]*?>")){
					MbufferWritter.write("<script>");
					MbufferWritter.write(text2);
					MbufferWritter.write("</script>");
				}
				else{
					MbufferWritter.write(text2);
				}
			       
			}	
			MbufferWritter.write("</head>");
			MbufferWritter.write("<body>");
			MbufferWritter.write("</body>");
			MbufferWritter.write("</html>");
			MbufferWritter.close();*/
			
			String benignOpFile = "B19.html";
			
			FileWriter BfWriter = new FileWriter(benignOpFile, true);
			BufferedWriter BbufferWritter = new BufferedWriter(BfWriter);
			BbufferWritter.write("<html>");
			BbufferWritter.write("<head>");
			for (int i = 200 ; i <= 250 && (i < fileLimit); i++) {
				
				String text = fileToString(benignFiles[i]);
				
				if(! text.matches("<\\/?(script|SCRIPT)[^>]*?>")){
					BbufferWritter.write("<script>");
					BbufferWritter.write(text);
					BbufferWritter.write("</script>");
				}
				else{
					BbufferWritter.write(text);
				}
				
				
			       
			}	
			BbufferWritter.write("</head>");
			BbufferWritter.write("<body>");
			BbufferWritter.write("</body>");
			BbufferWritter.write("</html>");
			BbufferWritter.close();
			
			
			
			System.out.println("done");
		} catch (IOException e) {
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
		String scriptString = script.toString();
		
		return scriptString;
	}
}
