package io.github.iwilkey.teknetcore.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class FileUtilities {
	
	public static final String DATA_DIR = "/home/opc/legends/plugins/TeknetCore/data/";
	
	public static ArrayList<String[]> readDataFileLines(String name) {
		ArrayList<String[]> ret = new ArrayList<String[]>();
		if(!fileExists(DATA_DIR + name + ".dat")) {
			System.out.println("Data file \"" + name + "\" does not exist in current context!");
			return ret;
		}
		try {
			File reg = new File(DATA_DIR + name + ".dat");
			Scanner reader = new Scanner(reg);
			while(reader.hasNextLine()) {
			    String data = reader.nextLine();
			    String[] tokens = data.split(" ");
			    ret.add(tokens);
			}
			reader.close();
		} catch(IOException e) {
			System.out.println("An internal server error occurred while trying to open a TeknetCore file.");
		    e.printStackTrace();
		}
		return ret;
	}
	
	public static void clearDataFile(String name) {
		try {
			new PrintWriter(DATA_DIR + name + ".dat").close();
		} catch(FileNotFoundException e) {
			System.out.println("Data file \"" + name + "\" does not exist in current context!");
			return;
		}
	}
	
	public static void appendDataEntryTo(String name, String entry) {
		try {
			FileWriter w = new FileWriter(DATA_DIR + name + ".dat", true);
			w.write(entry + "\n");
			w.close();
		} catch (IOException e) {
			System.out.println("An internal server error occurred while trying to write to a TeknetCore file.");
			e.printStackTrace();
		}
	}
	
	public static boolean fileExists(String path) {
		File reg = new File(path);
		return reg.exists();
	}
	
	public static boolean createDataFile(String name) {
		try {
			File file = new File(DATA_DIR + name + ".dat");
			file.createNewFile();
			return fileExists(DATA_DIR + name + ".dat");
		} catch(IOException e) {
			System.out.println("An internal server error occurred while trying to create a TeknetCore file.");
		    e.printStackTrace();
		    return false;
		}
	}
}
