package io.github.wang_jingyi.ZiQian.profile;

import io.github.wang_jingyi.ZiQian.utils.FileUtil;

import java.io.FileNotFoundException;


public class TimeProfile {
	
	public static long mainStartTime;
	public static long mainEndTime;
	public static long mainTime;
	
	public static long iterationEndTime;
	public static long iterationTime;
	
	public static long simulationStartTime;
	public static long simulationEndTime;
	public static long simulationTime;
	
	public static long extractDataStartTime;
	public static long extractDataEndTime;
	public static long extractDataTime;
	
	public static long learnStartTime;
	public static long learnEndTime;
	public static long learnTime;
	
	public static long dataPrefixStartTime;
	public static long dataPrefixEndTime;
	public static long dataPrefixTime;
	public static int dataPrefixExes = 0;
	
	public static long coreStartTime;
	public static long coreEndTime;
	public static long coreTime;
	
	public static long translateStartTime;
	public static long translateEndTime;
	public static long translateTime;
	
	public static long formatPMStartTime;
	public static long formatPMEndTime;
	public static long formatPMTime;
	
	public static long sampleStartTime;
	public static long sampleEndTime;
	
	public static StringBuilder sb = new StringBuilder();
	
	public static void outputTimeProfile() throws FileNotFoundException{
//		simulationTime = simulationEndTime-simulationStartTime;
//		extractDataTime = extractDataEndTime-extractDataStartTime;
//		learnTime = learnEndTime-learnStartTime; // whole process learn 
//		dataPrefixTime = dataPrefixEndTime-dataPrefixStartTime; // calculate the prefix tree
//		coreTime = coreEndTime-coreStartTime; // including prefix tree calculation and learning 
//		translateTime = translateEndTime-translateStartTime; // translate to PM model 
//		formatPMTime = formatPMEndTime-formatPMStartTime; // format to .pm file
//		mainTime = mainEndTime-mainStartTime; 
		
//		String filePath = gc.getLearnTypeFolder() + "/time_profile_" + gc.getObsLength() + ".txt";
//		FileIOs.writeStringToFile(filePath, sb.toString());
		
//		System.out.println("Time for simulation: " + nanoToSeconds(simulationTime) + " s.");
//		System.out.println("Time for extracting relavent data: " + nanoToSeconds(extractDataTime) + " s." );
//		System.out.println("Time for the whole process of learning: " + nanoToSeconds(learnTime) + "s.");
//		System.out.println("Time for preparing data prefixes information: " + nanoToSeconds(dataPrefixTime) + " s.");
//		System.out.println("Numbers of preparing data prefixes information: " + dataPrefixExes + ".");
//		System.out.println("Time for core learning: " + nanoToSeconds(coreTime) + " s.");
//		System.out.println("Time for translate learned model to prism format: " + nanoToSeconds(translateTime) + " s.");
//		System.out.println("Time for formating learned model to .pm file: " + nanoToSeconds(formatPMTime) + " s.");
		
	}
	
	
	public static void outputTimeProfile(String filePath) throws FileNotFoundException{
//		simulationTime = simulationEndTime-simulationStartTime;
//		extractDataTime = extractDataEndTime-extractDataStartTime;
//		learnTime = learnEndTime-learnStartTime; // whole process learn 
//		dataPrefixTime = dataPrefixEndTime-dataPrefixStartTime; // calculate the prefix tree
//		coreTime = coreEndTime-coreStartTime; // including prefix tree calculation and learning 
//		translateTime = translateEndTime-translateStartTime; // translate to PM model 
//		formatPMTime = formatPMEndTime-formatPMStartTime; // format to .pm file
		mainTime = mainEndTime - mainStartTime;
		
//		sb.append("Time for simulation: " + nanoToSeconds(simulationTime) + " s.\n");
//		sb.append("Time for extracting relavent data: " + nanoToSeconds(extractDataTime) + " s.\n");
//		sb.append("Time for the whole process of learning: " + nanoToSeconds(learnTime) + "s.\n");
//		sb.append("Learned model size: " + RunProfile.learnedModelSize + ".\n");
//		sb.append("Time for preparing data prefixes information: " + nanoToSeconds(dataPrefixTime) + " s.\n");
//		sb.append("Total number of prefixes:" + RunProfile.prefixCount + ".\n");
//		sb.append("Time for core learning: " + nanoToSeconds(coreTime) + " s.\n");
//		sb.append("Time for translate learned model to prism format: " + nanoToSeconds(translateTime) + " s.\n");
//		sb.append("Time for formating learned model to .pm file: " + nanoToSeconds(formatPMTime) + " s.\n");
		System.out.println("Time for the whole algorithm: " + nanoToSeconds(mainTime) + " s.\n");
		sb.append("Time for the whole algorithm: " + nanoToSeconds(mainTime) + " s.\n");
		FileUtil.writeStringToFile(filePath, sb.toString());
		
//		System.out.println("Time for simulation: " + nanoToSeconds(simulationTime) + " s.");
//		System.out.println("Time for extracting relavent data: " + nanoToSeconds(extractDataTime) + " s." );
//		System.out.println("Time for the whole process of learning: " + nanoToSeconds(learnTime) + "s.");
//		System.out.println("Time for preparing data prefixes information: " + nanoToSeconds(dataPrefixTime) + " s.");
//		System.out.println("Numbers of preparing data prefixes information: " + dataPrefixExes + ".");
//		System.out.println("Time for core learning: " + nanoToSeconds(coreTime) + " s.");
//		System.out.println("Time for translate learned model to prism format: " + nanoToSeconds(translateTime) + " s.");
//		System.out.println("Time for formating learned model to .pm file: " + nanoToSeconds(formatPMTime) + " s.");
		
	}
	
	public static double nanoToSeconds(long elapsedTime){
		return (double)elapsedTime / 1000000000.0;
	}
	
	
}
