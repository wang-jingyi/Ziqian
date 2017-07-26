package io.github.wang_jingyi.ZiQian.example;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.TruePredicate;
import io.github.wang_jingyi.ZiQian.data.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.data.PrismPathData;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.exceptions.SimulationException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedLearningTypeException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedTestingTypeException;
import io.github.wang_jingyi.ZiQian.main.AlgoProfile;
import io.github.wang_jingyi.ZiQian.main.Config;
import io.github.wang_jingyi.ZiQian.main.GlobalConfigs;
import io.github.wang_jingyi.ZiQian.main.LAR;
import io.github.wang_jingyi.ZiQian.main.TimeProfile;
import io.github.wang_jingyi.ZiQian.refine.PrismSampler;
import io.github.wang_jingyi.ZiQian.refine.TestEnvironment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CrowdMain {
	
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, 
	SimulationException, UnsupportedLearningTypeException, UnsupportedTestingTypeException {
			
			Config.writePropertyLearnFile();
			
			List<String> varsSet 
			= PrismPathData.extractPathVars(Config.DATA_PATH, Config.DELIMITER);
			
			ExtractPrismData epd = new ExtractPrismData(Config.DATA_PATH, Config.DATA_SIZE, Config.STEP_SIZE, Config.DELIMITER);
			VariablesValueInfo vvi = epd.getVariablesValueInfo(varsSet);
			
			AlgoProfile.vars = vvi.getVars();	
			AlgoProfile.varsLength = vvi.getVarsLength();
			
			List<Predicate> pres = new ArrayList<>();
			pres.add(new TruePredicate());
			pres.add(new CrowdPositive());
			
			AlgoProfile.predicates = pres;
			
			TestEnvironment te = TestEnvironment.te;
			
			LAR lar = new LAR();
			lar.setDATA_PATH(Config.DATA_PATH); // path of training data
			lar.setLEARNING_TYPE(Config.LEARN_METHOD); // learning algorithm
			lar.setMODEL_NAME(Config.MODEL_NAME); // model name
			lar.setOUTPUT_MODEL_PATH(Config.OUTPUT_MODEL_PATH); // path of output models
			GlobalConfigs.OUTPUT_MODEL_PATH = lar.getOUTPUT_MODEL_PATH(); 
			lar.setProperty(pres); // property to verify, initial predicate set
			lar.setSampler(new PrismSampler(Config.ORIG_MODEL_FILE,Config.TESTING_PATH,Config.MODEL_SETTING)); // set sampler for new traces
			lar.setTe(te); // set testing environment
			lar.setVvi(vvi); // set variable values information
			lar.setBoundedSteps(-1); // unbounded property
			lar.setPROPERTY_LEARN_FILE(Config.PROPERTY_LEARN_FILE); // path of property file
			lar.setPROPERTY_INDEX(Config.PROPERTY_INDEX); // index of property in the property file
			lar.setSafetyBound(Config.SAFETY_THRESHOLD); // safety threshold of the property
//			lar.setTESTING_TYPE("sprt"); // hypothesis testing type (sprt in this case)
//			lar.setError_alpha(0.05); // type_1 error for sprt
//			lar.setError_beta(0.05); // type_2 error for sprt
//			lar.setConfidence_inteval(0.01); // confidence inteval for sprt
			lar.setTESTING_TYPE("sst"); // hypothesis testing type (sst in this case)
			lar.setSstSampleSize(1); // sample size for sst
			lar.setData_step_size(Config.STEP_SIZE); // sampling step size of data
			lar.setData_delimiter(Config.DELIMITER); // delimiter of data file 
			
			TimeProfile.main_start_time = System.nanoTime();
			lar.execute(); // execute LAR
			TimeProfile.main_end_time = System.nanoTime();
			TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
			TimeProfile.outputTimeProfile(); // output time profile
			TimeProfile.outputTimeProfile(GlobalConfigs.PROJECT_ROOT+"/time_profile.txt"); // write time profile to file
			System.out.println("End of the program.");
			
		}
		

}
