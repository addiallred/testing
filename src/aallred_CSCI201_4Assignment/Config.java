package aallred_CSCI201_4Assignment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class Config {
	private int serverport = -1;
	private String hostname = null;
	private String DBC = null;
	private String DBU = null;
	private String DBP = null;
	private String wordFile = null;
	public Config(String filename) {
		Properties p = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		try {
			p.load(is);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		hostname = p.getProperty("ServerHostname");
		try {
			serverport = Integer.parseInt(p.getProperty("ServerPort"));
		}catch(NumberFormatException e) {
			//System.out.println("Invalid selection");
		}
		DBC = p.getProperty("DBConnection");
		DBU = p.getProperty("DBUsername");
		DBP = p.getProperty("DBPassword");
		wordFile = p.getProperty("SecretWordFile");
	}public boolean valid() {
		if(DBU == null || DBC == null || DBP == null || serverport == -1 || wordFile == null || hostname == null) {
			return false;
		}
		return true;
	}public String getDBC() {
		return DBC; 
	}public String getDBU() {
		return DBU;
	}public String getDBP() {
		return DBP;
	}public String getWordFile() {
		return wordFile;
	}public int getPort() {
		return serverport;
	}public String getHostName() {
		return hostname;
	}
}
