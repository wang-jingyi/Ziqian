package io.github.wang_jingyi.ZiQian.main;

import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;

public class AlgoProfile {
	
	// algorithm execution
	public static boolean newIteration = false; // decide if DataPrefix will be calculated again
	public static int iterationCount = 0;
	public static List<Predicate> predicates = new ArrayList<>();
	public static String model_name = null;
	
	public static boolean collect_training_data = true;
	
	public static List<String> vars = new ArrayList<>(); // triggered variables 
	public static List<Integer> varsLength;
	public static String result_output_path;
	
	public static String result = "false";
	
	public static boolean prefixCalculated;
	
	public static int SWAT_SAMPLE_STEP = 5;
	public static int SWAT_RECORD_STEP = 5;
	public static int SWAT_RUNNING_TIME = 5;
	public static boolean loop_first = false;
	
	
}
