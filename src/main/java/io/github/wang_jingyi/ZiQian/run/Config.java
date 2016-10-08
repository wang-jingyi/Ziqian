package io.github.wang_jingyi.ZiQian.run;

import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.utils.FileUtil;


public class Config {

	// general path settings
	public static final String PROJECT_ROOT = System.getProperty("user.dir");
	public static final String PRISM_PATH = PlatformDependent.PRISM_PATH;
	public static final String TMP_PATH = PROJECT_ROOT + "/tmp";


	/**  set initially  **/
	public static boolean abstraction = true; // if abstraction is deployed, used when translating models to .pm
	public static final boolean SWAT = false; // take extra care of swat, simulation is done differently
	public static final boolean EGL = false; // egl case
//		public static final String MODEL_NAME = "nand";
	//	public static String MODEL_NAME = "egl";
//	public static String MODEL_NAME = "crowds";
		public static final String MODEL_NAME = "swat";
//	public static final String MODEL_SETTING = "TotalRuns=5,CrowdSize=10";
//		public static final String MODEL_SETTING = "N=20,K=2";
	//	public static String MODEL_SETTING = "L=2,N=10";
	public static int ELG_L = 2;
	public static int EGL_N = 10;
	public static final int SWAT_SAMPLE_STEP = 10; // in ms
	public static final int SWAT_RECORD_STEP = 5; // in s
	public static final int SWAT_RUNNING_TIME = 10; // in minutes
	public static final String MODEL_SETTING = SWAT_SAMPLE_STEP + "," + SWAT_RECORD_STEP;
	public static final int DATA_SIZE = 20000; // data size of learning
	public static int PROPERTY_INDEX = 1; // index of the property in the property file
	public static int BOUNDED_STEP = -1; // bounded step of the property, -1 if unbounded
	public static double SAFETY_THRESHOLD = 0.2; // safety threshold of safety property
	public static boolean TERMINATE_SAMPLE = true; // if a sample would terminate
	public static boolean LOOP_FIRST = false; // if loop is put in front of spurious transitions
	public static boolean SELECTIVE_DATA_COLLECTION = false; // if all data is collected or each one a trace
	public static String LEARN_METHOD = "AA";

	// updated while running
	public static List<String> vars = new ArrayList<>();
	public static List<Integer> varsLength = new ArrayList<>();

	// model, learning setting
	public static String ORIG_MODEL_FILE = PlatformDependent.MODEL_ROOT + "/" + MODEL_NAME + "/" + MODEL_NAME + ".pm";
	public static String modelPath = "/"+MODEL_NAME+"/" + MODEL_SETTING +"/paths";
	public static String DATA_PATH = PlatformDependent.MODEL_ROOT + modelPath;
	public static String OUTPUT_MODEL_PATH = PlatformDependent.MODEL_ROOT + "/" + MODEL_NAME + "/" + MODEL_SETTING + "/lar_models";
	public static String TESTING_PATH = PlatformDependent.MODEL_ROOT + "/" + MODEL_NAME + "/" + MODEL_SETTING + "/lar_paths";
	public static String AA_OUTPUT_PATH = PlatformDependent.MODEL_ROOT + "/" + MODEL_NAME + "/" + MODEL_SETTING + "/AA";
	public static String GA_OUTPUT_PATH = PlatformDependent.MODEL_ROOT + "/" + MODEL_NAME + "/" + MODEL_SETTING + "/GA";

	// property setting
	public static String PROPERTY_LEARN_FILE = PlatformDependent.MODEL_ROOT + "/" + MODEL_NAME + "/" + MODEL_NAME + "_learn.pctl";
	public static String PROPERTY_FILE = PlatformDependent.MODEL_ROOT + "/" + MODEL_NAME + "/" + MODEL_NAME + ".pctl";


	// algorithm settings

	public static void initConfig(){
		FileUtil.createDir(DATA_PATH);
		FileUtil.createDir(OUTPUT_MODEL_PATH);
		FileUtil.createDir(TESTING_PATH);
		FileUtil.createDir(AA_OUTPUT_PATH);
		FileUtil.createDir(GA_OUTPUT_PATH);
	}
	
}
