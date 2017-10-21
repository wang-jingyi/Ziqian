package io.github.wang_jingyi.ZiQian.main;

import java.io.FileNotFoundException;

import io.github.wang_jingyi.ZiQian.utils.FileUtil;

public class SwatConfig {

	public static String MODEL_PATH = PlatformDependent.PROJECT_DIR + "/resource/case_study";
	public static String DATA_PATH = MODEL_PATH + "/training";
	public static String PROPERTY_LEARN_FILE = MODEL_PATH + "/swat.pctl";
	public static int PROPERTY_INDEX = 1;
	public static int BOUNDED_STEP = 100;

	// parameters for swat case study
	public static String SENSOR = "PIT501"; // sensor name in string
	public static double SENSOR_THRES = 30; // action threshold
	public static boolean HIGH = true;
	public static double SAFETY_THRESHOLD = 0.2; // how much more than the training unsafe probability
	public static int DATA_SIZE = 100000; // limit of data size for learning
	public static int STEP_SIZE = 60; // sample frequency
	public static double epsilon = 0.01; // parameter in the learning algorithm
	

	public static String DELIMITER = ",";
	public static String OUTPUT_MODEL_PATH = MODEL_PATH + "/output/" + SENSOR + "/" + SENSOR_THRES + "/" + SAFETY_THRESHOLD;
	public static String MODEL_NAME = "swat";

	// parameters for single trace learning of SWaT
	public static String TRAINING_LOG_PATH = MODEL_PATH + "/training";
	public static String TESTING_LOG = MODEL_PATH + "/testing";


	public static void writePropertyLearnFile() throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
		String prop = "swat_error";
		if(BOUNDED_STEP==-1){
			sb.append("P < " + SAFETY_THRESHOLD + "[F \"" + prop + "\"]\n");
			sb.append("P = ? " + "[F \"" + prop + "\"]\n");
		}
		else{
			sb.append("P < " + SAFETY_THRESHOLD + "[true U < " + BOUNDED_STEP + "\"" + prop + "\"]\n");
			sb.append("P = ? " + "[true U < " + BOUNDED_STEP + "\"" + prop + "\"]\n");
		}

		FileUtil.writeStringToFile(PROPERTY_LEARN_FILE, sb.toString());
	}

	public static void processDirs(){
		FileUtil.createDir(OUTPUT_MODEL_PATH);
		FileUtil.cleanDirectory(OUTPUT_MODEL_PATH);
	}
}
