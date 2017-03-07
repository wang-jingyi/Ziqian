package io.github.wang_jingyi.ZiQian.swat;

public class SwatConfig {
	
	public static String SWAT_EVALUATION_ROOT = "/Users/wangjingyi/swat_evaluation";
	public static String DATE = "06072015";
	public static String MODEL_PATH = SWAT_EVALUATION_ROOT + "/" + DATE;
	public static String DATA_PATH = MODEL_PATH + "/paths";
	public static int DATA_SIZE = Integer.MAX_VALUE;
	public static int STEP_SIZE = 1;
	
	public static String OUTPUT_MODEL_PATH = MODEL_PATH + "/output";
	public static String PROPERTY_LEARN_FILE = MODEL_PATH + "/swat.pctl";
	public static int PROPERTY_INDEX = 1;
	public static int BOUNDED_STEP = -1;
	public static double SAFETY_THRESHOLD = 0.9;
	
	
	
}
