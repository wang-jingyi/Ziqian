package io.github.wang_jingyi.ZiQian.profile;

import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;

public class AlgoProfile {
	
	// algorithm execution
	public static boolean newIteration = false; // decide if DataPrefix will be calculated again
	public static int iterationCount = 0;
	public static List<Predicate> predicates = new ArrayList<>();
	
	public static List<String> vars = new ArrayList<>(); // triggered variables 
	public static List<Integer> varsLength;
	
	public static String result = "false";
	
	public static boolean prefixCalculated;
	
	
}
