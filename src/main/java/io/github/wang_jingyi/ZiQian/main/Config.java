package io.github.wang_jingyi.ZiQian.main;

import io.github.wang_jingyi.ZiQian.utils.FileUtil;

import java.io.FileNotFoundException;


public class Config {

	// general path settings
	public static final String PROJECT_ROOT = System.getProperty("user.dir");
	public static final String PRISM_PATH = PlatformDependent.PRISM_PATH;
	public static final String TMP_PATH = PROJECT_ROOT + "/tmp";


	/**  set initially  **/
	public static final boolean SWAT = false; // take extra care of swat, simulation is done differently
	public static final boolean EGL = false; // egl case
//		public static String MODEL_NAME = "egl";
	public static String MODEL_NAME = "crowds";
//			public static final String MODEL_NAME = "swat";
		public static final String MODEL_SETTING = "TotalRuns=5,CrowdSize=20";
//			public static final String MODEL_SETTING = "N=60,K=1";
//		public static String MODEL_SETTING = "L=2,N=10";
	public static int ELG_L = 2;
	public static int EGL_N = 10;
	public static final int SWAT_SAMPLE_STEP = 5; // in ms
	public static final int SWAT_RECORD_STEP = 1; // in s
	public static final int SWAT_RUNNING_TIME = 10; // in minutes
//	public static final String MODEL_SETTING = SWAT_SAMPLE_STEP + "," + SWAT_RECORD_STEP;
	public static final int STEP_SIZE = 1;
	public static final int DATA_SIZE = 20000; // data size of learning
	public static final int CONVERGE_TEST_DATA_SIZE = 100000; // data size of learning for convergence test
	public static int PROPERTY_INDEX = 1; // index of the property in the property file
	public static int BOUNDED_STEP = -1; // bounded step of the property, -1 if unbounded
	public static double SAFETY_THRESHOLD = 0.10332; // safety threshold of safety property
	public static boolean TERMINATE_SAMPLE = true; // if a sample would terminate
	public static boolean SELECTIVE_DATA_COLLECTION = false; // if all data is collected or each one a trace
	public static String LEARN_METHOD = "GA";

	// model, learning setting
	public static String ORIG_MODEL_FILE = PlatformDependent.CAV_MODEL_ROOT + "/lar/" + MODEL_NAME + "/" + MODEL_NAME + ".pm";
	public static String modelPath = "/lar/"+MODEL_NAME+"/" + MODEL_SETTING +"/paths";
	public static String DATA_PATH = PlatformDependent.CAV_MODEL_ROOT + modelPath;
	public static String DATA_PATH_LOT = PlatformDependent.CAV_MODEL_ROOT + modelPath +"/lot";
	public static String OUTPUT_MODEL_PATH = PlatformDependent.CAV_MODEL_ROOT + "/lar/" + MODEL_NAME + "/" + 
			MODEL_SETTING + "/lar_models_" + LEARN_METHOD + "/thres_" + SAFETY_THRESHOLD;
	public static String TESTING_PATH = PlatformDependent.CAV_MODEL_ROOT + "/lar/" + MODEL_NAME + "/" + MODEL_SETTING + "/lar_paths_AA/thres_" + SAFETY_THRESHOLD;
	public static String AA_OUTPUT_PATH = PlatformDependent.CAV_MODEL_ROOT + "/lar/" + MODEL_NAME + "/" + MODEL_SETTING + "/AA";
	public static String GA_OUTPUT_PATH = PlatformDependent.CAV_MODEL_ROOT + "/lar/" + MODEL_NAME + "/" + MODEL_SETTING + "/GA";
	public static String BASIC_OUTPUT_PATH = PlatformDependent.CAV_MODEL_ROOT + "/lar/" + MODEL_NAME + "/" + MODEL_SETTING + "/" + LEARN_METHOD + "_basic";
	

	// property setting
	public static String PROPERTY_LEARN_FILE = OUTPUT_MODEL_PATH + "/" + MODEL_NAME + "_learn.pctl";
	public static String PROPERTY_FILE = PlatformDependent.CAV_MODEL_ROOT + "/lar/" + MODEL_NAME + "/" + MODEL_NAME + ".pctl";
	public static String DELIMITER = " ";


	// algorithm settings

	public static void initConfig(){
		FileUtil.createDir(DATA_PATH);
		FileUtil.createDir(DATA_PATH_LOT);
		FileUtil.createDir(OUTPUT_MODEL_PATH);
		FileUtil.createDir(TESTING_PATH);
		FileUtil.createDir(AA_OUTPUT_PATH);
		FileUtil.createDir(GA_OUTPUT_PATH);
		FileUtil.createDir(BASIC_OUTPUT_PATH);
	}
	
	public static void writePropertyLearnFile() throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
		String prop = "";
		if(MODEL_NAME.equals("swat")){
			prop = "swat_error";
		}
		if(MODEL_NAME.equals("egl")){
			prop = "unfairA";
		}
		else if(MODEL_NAME.equals("nand")){
			prop = "reliable";
		}
		else if(MODEL_NAME.equals("crowds")){
			prop = "positive";
		}
		sb.append("P <= " + SAFETY_THRESHOLD + "[F \"" + prop + "\"]\n");
		sb.append("P = ? " + "[F \"" + prop + "\"]\n");
		FileUtil.writeStringToFile(PROPERTY_LEARN_FILE, sb.toString());
	}

}
