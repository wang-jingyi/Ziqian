package io.github.wang_jingyi.ZiQian.main;

import io.github.wang_jingyi.ZiQian.utils.FileUtil;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TimeProfile {
	
	public static double main_time;
	public static long main_start_time;
	public static long main_end_time;
	
	public static List<Double> iteration_times = new ArrayList<Double>();
	public static long iteration_start_time;
	public static long iteration_end_time;
	
	public static List<Double> learning_times = new ArrayList<Double>();
	public static long learning_start_time;
	public static long learning_end_time;
	
	public static List<Double> prism_model_check_times = new ArrayList<Double>();
	public static long pmc_start_time;
	public static long pmc_end_time;
	
	public static List<Double> ce_generation_times = new ArrayList<Double>();
	public static long ce_generation_start_time;
	public static long ce_generation_end_time;
	
	public static List<Double> hypothesis_testing_times = new ArrayList<Double>();
	public static long ht_start_time;
	public static long ht_end_time;
	
	public static List<Double> spurious_check_times = new ArrayList<Double>();
	public static long spurious_start_time;
	public static long spurious_end_time;
	
	public static List<Double> refine_times = new ArrayList<Double>();
	public static long refine_start_time;
	public static long refine_end_time;
		
	public static StringBuilder sb = new StringBuilder();
	
	public static void outputTimeProfile() throws FileNotFoundException{
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		sb.append("\n++++++ Running time profile for the run starting at " + dateFormat.format(date) + " ++++++\n");
		sb.append("--- Verification result: " + AlgoProfile.result + " ---\n");
		sb.append("--- Time for whole algorithm: " + main_time + " s\n");
		sb.append("=== Time for each iteration: " + iteration_times + " s ===\n");
		sb.append("::: Divided time for each iteration :::\n");
		sb.append("- Time for learning: " + learning_times + " s\n");
		sb.append("- Time for PRISM model checking: " + prism_model_check_times + " s\n");
		sb.append("- Time for counterexample generation: " + ce_generation_times + " s\n");
		sb.append("- Time for hypothesis testing: " + hypothesis_testing_times + " s\n");
		sb.append("- Time for identifying spurious transtions: " + spurious_check_times + " s\n");
		sb.append("- Time for generating a new predicate: " + refine_times + " s");
		System.out.println(sb.toString());
	}
	
	
	public static void outputTimeProfile(String filePath) throws FileNotFoundException{
		FileUtil.appendStringToFile(filePath, sb.toString());
	}
	
	public static double nanoToSeconds(long elapsedTime){
		return (double)elapsedTime / 1000000000.0;
	}


	public static double millisToSeconds(long elapsedTime) {
		return (double)elapsedTime;
	}
	
	
}
