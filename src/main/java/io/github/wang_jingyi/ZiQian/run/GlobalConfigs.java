package io.github.wang_jingyi.ZiQian.run;

public class GlobalConfigs {
	
	public static String PROJECT_ROOT = System.getProperty("user.dir");
	public static boolean SELECTIVE_DATA_COLLECTION = true;
	public static boolean TERMINATE_SAMPLE = false;
	public static String OUTPUT_MODEL_PATH = "";
	
	public static double[] minSensorValues = {Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE};
	public static double[] maxSensorValues = new double[5];
	public static int newStateNumber = 0;
	

}
