package io.github.wang_jingyi.ZiQian.singtel;

import java.io.FileNotFoundException;

import io.github.wang_jingyi.ZiQian.main.PlatformDependent;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

public class SingtelConfig {
	
	
	// general path settings
		public static final String PROJECT_ROOT = System.getProperty("user.dir");
		public static final String PRISM_PATH = PlatformDependent.PRISM_PATH;
		public static final String TMP_PATH = PROJECT_ROOT + "/tmp";


		/**  set initially  **/
		public static final boolean SWAT = false; // take extra care of swat, simulation is done differently
		public static final boolean EGL = false; // egl case
		public static final String MODEL_NAME = "singtel";
		public static final String MODEL_SETTING = "7_weeks";

		public static final int STEP_SIZE = 1;
		public static final int DATA_SIZE = 10000; // data size of learning
		public static final int CONVERGE_TEST_DATA_SIZE = 50000; // data size of learning for convergence test
		public static int PROPERTY_INDEX = 1; // index of the property in the property file
		public static int BOUNDED_STEP = 100; // bounded step of the property, -1 if unbounded
		public static double SAFETY_THRESHOLD = 0.3; // safety threshold of safety property
		public static boolean TERMINATE_SAMPLE = false; // if a sample would terminate
		public static boolean SELECTIVE_DATA_COLLECTION = false; // if all data is collected or each one a trace
		public static String LEARN_METHOD = "AA";

		// model, learning setting
		public static String modelPath = "/lar/"+MODEL_NAME+"/" + MODEL_SETTING +"/paths";
		public static String DATA_PATH = PlatformDependent.CAV_MODEL_ROOT + modelPath;
		public static String DATA_PATH_LOT = PlatformDependent.CAV_MODEL_ROOT + modelPath +"/lot";
		public static String OUTPUT_MODEL_PATH = PlatformDependent.CAV_MODEL_ROOT + "/lar/" + MODEL_NAME + "/" + 
				MODEL_SETTING + "/lar_models/thres_" + SAFETY_THRESHOLD;
		public static String TESTING_PATH = PlatformDependent.CAV_MODEL_ROOT + "/lar/" + MODEL_NAME + "/" + MODEL_SETTING + "/lar_paths";
		public static String AA_OUTPUT_PATH = PlatformDependent.CAV_MODEL_ROOT + "/lar/" + MODEL_NAME + "/" + MODEL_SETTING + "/AA";
		public static String GA_OUTPUT_PATH = PlatformDependent.CAV_MODEL_ROOT + "/lar/" + MODEL_NAME + "/" + MODEL_SETTING + "/GA";

		// property setting
		public static String PROPERTY_LEARN_FILE = OUTPUT_MODEL_PATH + "/" + MODEL_NAME + "_learn.pctl";
		public static String PROPERTY_FILE = PlatformDependent.CAV_MODEL_ROOT + "/lar/" + MODEL_NAME + "/" + MODEL_NAME + ".pctl";
		public static String DELIMITER = ",";


		// algorithm settings

		public static void initConfig(){
			FileUtil.createDir(DATA_PATH);
			FileUtil.createDir(DATA_PATH_LOT);
			FileUtil.createDir(OUTPUT_MODEL_PATH);
			FileUtil.createDir(TESTING_PATH);
			FileUtil.createDir(AA_OUTPUT_PATH);
			FileUtil.createDir(GA_OUTPUT_PATH);
		}
		
		public static void writePropertyLearnFile() throws FileNotFoundException{
			StringBuilder sb = new StringBuilder();
			String prop = "is_target_label";
			if(BOUNDED_STEP==-1){
				sb.append("P <= " + SAFETY_THRESHOLD + "[F \"" + prop + "\"]\n");
				sb.append("P = ? " + "[F \"" + prop + "\"]\n");
			}
			else{
				sb.append("P <= " + SAFETY_THRESHOLD + "[true U<=" + BOUNDED_STEP +" \"" + prop + "\"]\n");
				sb.append("P = ? " + "[true U<=" + BOUNDED_STEP +" \"" + prop + "\"]\n");
			}
			
			FileUtil.writeStringToFile(PROPERTY_LEARN_FILE, sb.toString());
		}

}
