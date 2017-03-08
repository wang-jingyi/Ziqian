package io.github.wang_jingyi.ZiQian.swat;

import java.io.FileNotFoundException;

import io.github.wang_jingyi.ZiQian.utils.FileUtil;

public class SwatConfig {
	
	public static String SWAT_EVALUATION_ROOT = System.getProperty("user.home") + "/swat_evaluation";
	public static String DATE = "06072015";
	public static String MODEL_PATH = SWAT_EVALUATION_ROOT + "/" + DATE;
	public static String DATA_PATH = MODEL_PATH + "/paths";
	public static int DATA_SIZE = 10000;
	public static int STEP_SIZE = 1;
	
	public static String OUTPUT_MODEL_PATH = MODEL_PATH + "/output";
	public static String PROPERTY_LEARN_FILE = MODEL_PATH + "/swat.pctl";
	public static int PROPERTY_INDEX = 1;
	public static int BOUNDED_STEP = -1;
	public static double SAFETY_THRESHOLD = 0.9;
	public static String DELIMITER = " ";
	
	
	public static void writePropertyLearnFile() throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
		String prop = "swat_error";
		sb.append("P < " + SAFETY_THRESHOLD + "[F \"" + prop + "\"]\n");
		sb.append("P = ? " + "[F \"" + prop + "\"]\n");
		FileUtil.writeStringToFile(PROPERTY_LEARN_FILE, sb.toString());
	}
}
