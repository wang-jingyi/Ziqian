package io.github.wang_jingyi.ZiQian.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	public static void cleanDirectory(String path){
		File dir = new File(path);
		if(!dir.exists()){
			return;
		}
		for(File file : dir.listFiles()){
			file.delete();
		}
	}

	public static void writeObject(String fileName, Object obj) throws FileNotFoundException, IOException{
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				new FileOutputStream(fileName));
		objectOutputStream.writeObject(obj);
		objectOutputStream.flush();
		objectOutputStream.close();
	}

	public static Object readObject(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream objectInputStream = new ObjectInputStream(
				new FileInputStream(fileName));
		Object obj =  objectInputStream.readObject();
		objectInputStream.close();
		return obj;
	}


	public static boolean isDirEmpty(String dirPath) throws IOException {
		if(new File(dirPath).list().length>0){
			return false;
		}
		return true;

	}

	public static void writeStringToFile(String filepath, String str) throws FileNotFoundException{
		PrintWriter out = new PrintWriter(filepath);
		out.println(str);
		out.close();
	}

	public static void appendStringToFile(String filepath, String str) throws FileNotFoundException{

		File file =new File(filepath);

		//if file doesnt exists, then create it
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//true = append file
		try {
			FileWriter fw = new FileWriter(filepath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println(str);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeAlphabetToCSV(String filepath, List<String> alphabet) throws IOException{
		FileWriter writer = new FileWriter(filepath); 
		int counter = 1;
		for(String str: alphabet) {
			if(counter!=alphabet.size()){
				writer.write(str + ",");
				counter++;
			}
			else{
				writer.write(str);
			}
		}
		writer.close();
	}

	// each list is a value of one variable
	public static void writeSplitDataToCSV(String fp, List<String> head, List<List<String>> data) throws IOException{
		FileWriter writer = new FileWriter(fp); 
		long dataLength = data.get(0).size();

		// write header
		for(int i=0; i<head.size(); i++){
			if(i==head.size()-1){
				writer.write(head.get(i) + "\n");
			}
			else{
				writer.write(head.get(i) + ",");
			}
		}

		// write data
		for(int i=0; i<dataLength; i++){
			for(int j=0; j<head.size(); j++){
				if(j==head.size()-1){ // last value of a line
					writer.write(data.get(j).get(i) + "\n");
				}
				else{
					writer.write(data.get(j).get(i) + ",");
				}
			}
		}
		writer.close();
	}

	public static void writeDataToCSV(String filepath, List<List<String>> data) throws IOException{
		FileWriter writer = new FileWriter(filepath); 
		for(List<String> strs: data) {
			int counter = 1;
			for(String s : strs){
				if(counter!=strs.size()){
					writer.write(s + ",");
					counter++;
				}
				else{
					writer.write(s);
				}
			}
			writer.write("\n");
		}
		writer.close();
	}

	public static void writeDataToCSV(String filepath, List<String> head, List<List<String>> data) throws IOException{
		FileWriter writer = new FileWriter(filepath); 

		// write header
		for(int i=0; i<head.size(); i++){
			if(i==head.size()-1){
				writer.write(head.get(i) + "\n");
			}
			else{
				writer.write(head.get(i) + ",");
			}
		}
		
		// write data
		for(List<String> strs: data) {
			int counter = 1;
			for(String s : strs){
				if(counter!=strs.size()){
					writer.write(s + ",");
					counter++;
				}
				else{
					writer.write(s);
				}
			}
			writer.write("\n");
		}
		writer.close();
	}

	public static List<String> readObservationFromCSV(String csvFileName, int obslen) throws IOException{
		String line = null;
		BufferedReader stream = null;
		List<String> obs = new ArrayList<String>();
		int i=0;
		try {
			stream = new BufferedReader(new FileReader(csvFileName));
			while ((line = stream.readLine()) != null) {
				String[] splitted = line.split(",");
				for (String data : splitted){
					if(i>=obslen){
						break;
					}
					obs.add(data);
					i++;
				}
				assert stream.readLine()==null : "more than two lines in the str.csv for single observation";
			}
		} finally {
			if (stream != null)
				stream.close();
		}
		return obs;

	}

	public static List<String> readObservationFromCSV(String csvFileName) throws IOException{
		String line = null;
		BufferedReader stream = null;
		List<String> obs = new ArrayList<String>();
		try {
			stream = new BufferedReader(new FileReader(csvFileName));
			while ((line = stream.readLine()) != null) {
				String[] splitted = line.split(",");
				for (String data : splitted){
					obs.add(data);
				}
				assert stream.readLine()==null : "more than two lines in the str.csv for single observation";
			}
		} finally {
			if (stream != null)
				stream.close();
		}
		return obs;

	}

	public static List<String> buildAlphabetFromCSV(String csvFileName) throws IOException{
		String line = null;
		BufferedReader stream = null;
		List<String> alphabet = new ArrayList<String>();
		try {
			stream = new BufferedReader(new FileReader(csvFileName));
			while ((line = stream.readLine()) != null) {
				String[] splitted = line.split(",");
				for (String data : splitted){
					if(StringUtil.getStringIndex(data, alphabet)==-1){
						alphabet.add(data);	
					}
				}
				assert stream.readLine()==null : "more than two lines in the single observation data";
			}
		} finally {
			if (stream != null)
				stream.close();
		}
		return alphabet;
	}

	public static List<String> readAlphabetFromCSV(String csvFileName) throws IOException{
		String line = null;
		BufferedReader stream = null;
		List<String> alphabet = new ArrayList<String>();
		try {
			stream = new BufferedReader(new FileReader(csvFileName));
			while ((line = stream.readLine()) != null) {
				String[] splitted = line.split(",");
				for (String data : splitted){
					alphabet.add(data);
				}
				assert stream.readLine()==null : "more than two lines in the alphabet.csv";
			}
		} finally {
			if (stream != null)
				stream.close();
		}
		return alphabet;
	}

	// file type 1: CSV_STRS, txt/csv file
	public static List<List<String>> readTracesFromCSV(String csvFileName, int strLength) throws IOException {
		String line = null;
		BufferedReader stream = null;
		List<List<String>> csvData = new ArrayList<List<String>>();
		int len = 0;
		try {
			stream = new BufferedReader(new FileReader(csvFileName));
			while ((line = stream.readLine()) != null && len!=strLength) {
				String[] splitted = line.split(",");
				List<String> dataLine = new ArrayList<String>(splitted.length);
				for (String data : splitted){
					dataLine.add(data);
					len++;
					if(len==strLength){
						break;
					}
				}
				csvData.add(dataLine);
			}
		} finally {
			if (stream != null)
				stream.close();
		}
		return csvData;
	}


	// file type 2: PRISM_EXPORTED_PATH, translate prism exported path data to list<String> as events for learning input
	public static List<String> readPathData(String filePath, int pathLength){
		List<String> events = new ArrayList<String>();
		int len = 0;
		try {
			// read file content from file
			FileReader reader = new FileReader(filePath);
			BufferedReader br = new BufferedReader(reader);
			String str = null;
			str = br.readLine(); // omit the first introduction line
			while((str = br.readLine()) != null) {
				len++;
				String[] strs = str.split(" ");
				String useStr = "";
				for(int i=2; i<strs.length; i++){ // data starts from the 3rd element
					useStr = useStr + strs[i];
				}
				events.add(useStr);
				if(len==pathLength){
					break;
				}
			}
			br.close();
			reader.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return events;
	}

	public static List<String> filesInDir(String dirPath){
		List<String> files = new ArrayList<String>();
		File folder = new File(dirPath);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if(file.getAbsolutePath().endsWith(".txt") || file.getAbsolutePath().endsWith(".csv")){
				files.add(file.getAbsolutePath());
			}
		}
		return files;
	}

	public static void createDir(String dirPath){
		File theDir = new File(dirPath);
		if(!theDir.exists()){
			theDir.mkdirs();
		}
	}

}
