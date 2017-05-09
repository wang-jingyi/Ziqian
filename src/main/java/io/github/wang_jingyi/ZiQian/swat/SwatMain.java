package io.github.wang_jingyi.ZiQian.swat;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.TruePredicate;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.exceptions.SimulationException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedLearningTypeException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedTestingTypeException;
import io.github.wang_jingyi.ZiQian.prism.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.prism.PrismPathData;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.profile.TimeProfile;
import io.github.wang_jingyi.ZiQian.run.GlobalConfigs;
import io.github.wang_jingyi.ZiQian.run.LAR;
import io.github.wang_jingyi.ZiQian.sample.TestEnvironment;
import io.github.wang_jingyi.ZiQian.swat.property.OverHigh;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SwatMain {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, 
	SimulationException, UnsupportedLearningTypeException, UnsupportedTestingTypeException {

		SwatConfig.writePropertyLearnFile();

		List<String> varsSet 
		= PrismPathData.extractPathVars(SwatConfig.DATA_PATH, SwatConfig.DELIMITER);

		ExtractPrismData epd = new ExtractPrismData(SwatConfig.DATA_PATH, SwatConfig.DATA_SIZE, SwatConfig.STEP_SIZE, SwatConfig.DELIMITER);
		VariablesValueInfo vvi = epd.getVariablesValueInfo(varsSet);

		AlgoProfile.vars = vvi.getVars();	
		AlgoProfile.varsLength = vvi.getVarsLength();

		List<Predicate> pres = new ArrayList<>();
		pres.add(new TruePredicate());
		pres.add(new OverHigh(SwatConfig.SENSOR,SwatConfig.SENSOR_THRES));

		AlgoProfile.predicates = pres;

		TestEnvironment te = TestEnvironment.te;

		LAR lar = new LAR();
		lar.setDATA_PATH(SwatConfig.DATA_PATH); // path of training data
		lar.setLEARNING_TYPE("AA"); // learning algorithm
		lar.setMODEL_NAME("swat"); // model name
		lar.setOUTPUT_MODEL_PATH(SwatConfig.OUTPUT_MODEL_PATH); // path of output models
		GlobalConfigs.OUTPUT_MODEL_PATH = lar.getOUTPUT_MODEL_PATH(); 
		lar.setProperty(pres); // property to verify, initial predicate set
		lar.setSampler(new SwatSampler()); // set sampler for new traces
		lar.setTe(te); // set testing environment
		lar.setVvi(vvi); // set variable values information
		lar.setBoundedSteps(-1); // unbounded property
		lar.setPROPERTY_LEARN_FILE(SwatConfig.PROPERTY_LEARN_FILE); // path of property file
		lar.setPROPERTY_INDEX(SwatConfig.PROPERTY_INDEX); // index of property in the property file
		lar.setSafetyBound(SwatConfig.SAFETY_THRESHOLD); // safety threshold of the property
		lar.setTESTING_TYPE("sprt"); // hypothesis testing type (sprt in this case)
		lar.setError_alpha(0.05); // type_1 error for sprt
		lar.setError_beta(0.05); // type_2 error for sprt
		lar.setConfidence_inteval(0.01); // confidence inteval for sprt
		//		lar.setTESTING_TYPE("sst"); // hypothesis testing type (sst in this case)
		//		lar.setSstSampleSize(100); // sample size for sst
		lar.setData_step_size(SwatConfig.STEP_SIZE); // sampling step size of data
		lar.setData_delimiter(SwatConfig.DELIMITER); // delimiter of data file
		lar.setTerminateSample(false);
		lar.setSelectiveDataCollection(true);
		
		TimeProfile.main_start_time = System.nanoTime();
		lar.execute(); // execute LAR
		TimeProfile.main_end_time = System.nanoTime();
		TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
		TimeProfile.outputTimeProfile(); // output time profile
		TimeProfile.outputTimeProfile(GlobalConfigs.OUTPUT_MODEL_PATH+"/time_profile.txt"); // write time profile to file
		System.out.println("End of the program.");

	}


}
