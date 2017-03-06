package io.github.wang_jingyi.ZiQian.swat;

import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.ListUtil;
import io.github.wang_jingyi.ZiQian.utils.NumberUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cern.jet.random.engine.MersenneTwister;

public class DataProcessor {

	public static void extractHASERealData(String dirPath, String targetFilePath) throws IOException{

		// find the latest start time and earliest end time
		String latestStartTime = "6/7/2013 10:16:15 AM";
		String earliestEndTime = "6/7/2016 10:16:15 AM";

		for(String fileName : FileUtil.filesInDir(dirPath)){
			int timeIndex = -1;
			if(fileName.endsWith(".csv")){
				System.out.println("file in processing: " + fileName);
				try {
					// read file content from file
					FileReader reader = new FileReader(fileName);
					BufferedReader br = new BufferedReader(reader);
					String str = null;
					str = br.readLine(); // omit the first introduction line
					String[] vars = str.split(", ");
					for(int i=0; i<vars.length; i++){
						if(vars[i].equalsIgnoreCase("Timestamp")){
							timeIndex = i;
							break;
						}
					}
					String startTime = br.readLine().split(", ")[timeIndex];
					String endTime = "";
					while((str = br.readLine()) != null) {
						endTime = str.split(", ")[timeIndex];
					}
					br.close();
					reader.close();
					if(compareTime(startTime, latestStartTime)==1){
						latestStartTime = startTime;
					}
					if(compareTime(endTime, earliestEndTime)==-1){
						earliestEndTime = endTime;
					}

				}catch(FileNotFoundException e) {
					e.printStackTrace();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("latest start time: " + latestStartTime);
		System.out.println("earliest end time: " + earliestEndTime);

		List<List<String>> dataset = new ArrayList<List<String>>();
		List<String> sensors = new ArrayList<String>();
		// trim the data from the latest start time to earliest end time
		for(String fileName : FileUtil.filesInDir(dirPath)){
			int timeIndex = -1;
			int valueIndex = -1;
			List<String> data = new ArrayList<String>();
			if(fileName.endsWith(".csv")){
				System.out.println("reading: " + fileName);
				String[] names = fileName.split("/");
				String name = names[names.length-1];
				name = name.substring(0, name.length()-4);
				sensors.add(name);
				try {
					// read file content from file
					FileReader reader = new FileReader(fileName);
					BufferedReader br = new BufferedReader(reader);
					String str = null;
					str = br.readLine(); // omit the first introduction line
					String[] vars = str.split(", ");
					for(int i=0; i<vars.length; i++){
						if(vars[i].equalsIgnoreCase("Timestamp")){
							timeIndex = i;
							continue;
						}
						if(vars[i].equalsIgnoreCase("Value")){
							valueIndex = i;
							continue;
						}
					}
					while((str = br.readLine()) != null) {
						String[] strs = str.split(", ");
						String currentTime = strs[timeIndex];
						if(compareTime(currentTime, latestStartTime)!=-1 && compareTime(currentTime, earliestEndTime)!=1){
							data.add(strs[valueIndex]);
						}
					}
					br.close();
					reader.close();
					dataset.add(data);
				}catch(FileNotFoundException e) {
					e.printStackTrace();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		FileUtil.writeSplitDataToCSV(targetFilePath, sensors, dataset);
		System.out.println("writing complete...");
	}

	private static void splitLongTrace(List<List<String>> dataset, List<String> vars, String targetDirPath) throws IOException{
		// split dataset into traces of random length
		int lengthOfData = dataset.size();
		System.out.println("total length of data: " + lengthOfData);
		int pathCount = 0;
		int lineCount = 0;
		MersenneTwister rnd = new MersenneTwister();
		while(lineCount<lengthOfData){
			int pathLength = (int) (480 + 240 * rnd.nextDouble()); // split trace into traces of 8-12 mins
			List<List<String>> pathData = new ArrayList<List<String>>();
			for(int i=0; i<pathLength; i++){
				pathData.add(dataset.get(lineCount));
				lineCount ++;
				if(lineCount>=lengthOfData){break;}
			}
			System.out.println("writing path + " + pathCount);
			FileUtil.writeDataToCSV(targetDirPath + "/path_"+pathCount+".csv", vars, pathData);
			pathCount++;
		}

	}


	// if time1 is later than time2, return 1, else, same return 0, earlier return -1
	// to fix am/pm problem
	private static int compareTime(String time1, String time2){

		List<String> t1 = adjustAmPm(time1);
		List<String> t2 = adjustAmPm(time2);

		assert t1.size()==t2.size() : "cannot compare time";
		for(int i=0; i<t1.size(); i++){
			int t11 = Integer.valueOf(t1.get(i));
			int t21 = Integer.valueOf(t2.get(i));
			if(t11>t21){
				return 1;
			}
			else if(t11<t21){
				return -1;
			}
		}
		return 0;
	}

	private static List<String> adjustAmPm(String time){
		String ap = time.substring(time.length()-2);
		List<String> tl = NumberUtil.extractNumbersFromString(time);
		if(ap.equals("PM")){
			String hour = tl.get(tl.size()-3);
			int h = Integer.valueOf(hour);
			String ahour = String.valueOf(h+12);
			tl.set(tl.size()-3, ahour);
		}
		return tl;
	}


	public static void main(String[] args) throws IOException{
		//		String time1 = "6/7/2015 10:18:33 AM";
		//		String time2 = "6/7/2015 06:13:33 AM";
		//		System.out.println("compare: " + compareTime(time1, time2));
		
		String sourcePath = "/Users/jingyi/Documents/swat_dataset/original/06072015";
		String dirpath = "/Users/jingyi/Documents/swat_dataset/original/06072015/paths";
		FileUtil.createDir(dirpath);
		FileUtil.cleanDirectory(dirpath);
		String targetfilepath = "/Users/jingyi/Documents/swat_dataset/original/06072015.csv";
		extractHASERealData(sourcePath, targetfilepath);
		
		List<String> sensors= new ArrayList<String>();
		List<List<String>> dataset = new ArrayList<List<String>>();
		try {
			// read file content from file
			FileReader reader = new FileReader(targetfilepath);
			BufferedReader br = new BufferedReader(reader);
			String str = null;
			str = br.readLine(); // omit the first introduction line
			String[] vars = str.split(",");
			sensors = ListUtil.arrayToList(vars);
			System.out.println("sensors: " + sensors);
			while((str = br.readLine()) != null) {
				String[] line = str.split(",");
				List<String> ld = ListUtil.arrayToList(line);
				dataset.add(ld);
			}
			br.close();
			reader.close();

		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		splitLongTrace(dataset,sensors,dirpath);
	}
}
