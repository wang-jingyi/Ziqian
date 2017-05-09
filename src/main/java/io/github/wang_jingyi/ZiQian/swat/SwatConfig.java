package io.github.wang_jingyi.ZiQian.swat;

import java.io.FileNotFoundException;

import io.github.wang_jingyi.ZiQian.run.PlatformDependent;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

public class SwatConfig {
	
	public static String SWAT_EVALUATION_ROOT = PlatformDependent.SWAT_EVALUATION_PATH;
	public static String BATCH = "data_batch_1";
	public static String SENSOR = "LIT401";
	public static double SENSOR_THRES = 1000; 
	public static String MODEL_PATH = SWAT_EVALUATION_ROOT + "/" + BATCH;
	public static String DATA_PATH = MODEL_PATH + "/normal_traces";
	public static int DATA_SIZE = 5000;
	public static int STEP_SIZE = 5;
	public static String PROPERTY_LEARN_FILE = MODEL_PATH + "/swat.pctl";
	public static int PROPERTY_INDEX = 1;
	public static int BOUNDED_STEP = -1;
	public static double SAFETY_THRESHOLD = 0.4;
	public static String DELIMITER = ",";
	public static String OUTPUT_MODEL_PATH = MODEL_PATH + "/output/" + SENSOR + "/" + SENSOR_THRES + "/" + SAFETY_THRESHOLD;
	
	public static void writePropertyLearnFile() throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
		String prop = "swat_error";
		sb.append("P < " + SAFETY_THRESHOLD + "[F \"" + prop + "\"]\n");
		sb.append("P = ? " + "[F \"" + prop + "\"]\n");
		FileUtil.writeStringToFile(PROPERTY_LEARN_FILE, sb.toString());
	}
}
