package io.github.wang_jingyi.ZiQian.singtel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.TruePredicate;
import io.github.wang_jingyi.ZiQian.data.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.data.PrismPathData;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.exceptions.SimulationException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedLearningTypeException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedTestingTypeException;
import io.github.wang_jingyi.ZiQian.main.AlgoProfile;
import io.github.wang_jingyi.ZiQian.main.GlobalConfigs;
import io.github.wang_jingyi.ZiQian.main.LAR;
import io.github.wang_jingyi.ZiQian.main.TimeProfile;
import io.github.wang_jingyi.ZiQian.refine.TestEnvironment;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

public class SingtelMain {
	
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, 
	SimulationException, UnsupportedLearningTypeException, UnsupportedTestingTypeException {
		
		
		// initial configs
		SingtelConfig.initConfig();
		FileUtil.cleanDirectory(SingtelConfig.OUTPUT_MODEL_PATH);
		SingtelConfig.writePropertyLearnFile();
		
		// read and format data from file
		List<String> varsSet 
		= PrismPathData.extractPathVars(SingtelConfig.DATA_PATH, SingtelConfig.DELIMITER);
		
		ExtractPrismData epd = new ExtractPrismData(SingtelConfig.DATA_PATH, SingtelConfig.DATA_SIZE, 
				SingtelConfig.STEP_SIZE, SingtelConfig.DELIMITER, !SingtelConfig.TERMINATE_SAMPLE);
		VariablesValueInfo vvi = epd.getVariablesValueInfo(varsSet);

		AlgoProfile.vars = vvi.getVars();	
		AlgoProfile.varsLength = vvi.getVarsLength();
		
		List<Predicate> pres = new ArrayList<>();
		pres.add(new TruePredicate());
		pres.add(new LabelPredicate(11));
		
		AlgoProfile.predicates = pres;

		TestEnvironment te = TestEnvironment.te;
		LAR lar = new LAR();
		lar.setDATA_PATH(SingtelConfig.DATA_PATH); // path of training data
		lar.setLEARNING_TYPE(SingtelConfig.LEARN_METHOD); // learning algorithm
		lar.setMODEL_NAME(SingtelConfig.MODEL_NAME); // model name
		lar.setOUTPUT_MODEL_PATH(SingtelConfig.OUTPUT_MODEL_PATH); // path of output models
		GlobalConfigs.OUTPUT_MODEL_PATH = lar.getOUTPUT_MODEL_PATH(); 
		lar.setProperty(pres); // property to verify, initial predicate set
		lar.setSampler(new SingtelSampler());
		lar.setTe(te); // set testing environment
		lar.setVvi(vvi); // set variable values information
		lar.setBoundedSteps(-1); // unbounded property
		lar.setPROPERTY_LEARN_FILE(SingtelConfig.PROPERTY_LEARN_FILE); // path of property file
		lar.setPROPERTY_INDEX(SingtelConfig.PROPERTY_INDEX); // index of property in the property file
		lar.setSafetyBound(SingtelConfig.SAFETY_THRESHOLD); // safety threshold of the property
		lar.setTESTING_TYPE("sprt"); // hypothesis testing type (sprt in this case)
		lar.setError_alpha(0.05); // type_1 error for sprt
		lar.setError_beta(0.05); // type_2 error for sprt
		lar.setConfidence_inteval(0.05); // confidence inteval for sprt
		//		lar.setTESTING_TYPE("sst"); // hypothesis testing type (sst in this case)
		//		lar.setSstSampleSize(100); // sample size for sst
		lar.setData_step_size(SingtelConfig.STEP_SIZE); // sampling step size of data
		lar.setData_delimiter(SingtelConfig.DELIMITER); // delimiter of data file
		lar.setTerminateSample(SingtelConfig.TERMINATE_SAMPLE);
		lar.setSelectiveDataCollection(SingtelConfig.SELECTIVE_DATA_COLLECTION);
		
		TimeProfile.main_start_time = System.nanoTime();
		lar.execute(); // execute LAR
		TimeProfile.main_end_time = System.nanoTime();
		TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
		TimeProfile.outputTimeProfile(); // output time profile
		TimeProfile.outputTimeProfile(GlobalConfigs.OUTPUT_MODEL_PATH+"/time_profile.txt"); // write time profile to file
		System.out.println("End of the program.");
		

	}

}
