package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.List;

public class ALConfig {

	public static boolean sparse = false;   // if the matrix is sparse
	public static int stateNumber; 	// number of states
	public static int newSampleNumber; // number of new samples
	public static int pathLength;
	
	public static boolean ido = true;
	
	public static int totalSensorNumber = 5;
	public static List<Integer> sensorIndex = new ArrayList<Integer>();
	public static boolean sensorIndexAdded = false;
	
}
