package io.github.wang_jingyi.ZiQian.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.TruePredicate;
import io.github.wang_jingyi.ZiQian.data.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.data.PrismPathData;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedLearningTypeException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedTestingTypeException;
import io.github.wang_jingyi.ZiQian.swat.property.OverHigh;

public class SwatSingleMain {

	public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLearningTypeException, UnsupportedTestingTypeException {
		
		SwatConfig.writePropertyLearnFile();
		SwatConfig.processDirs();

		List<String> varsSet 
		= PrismPathData.extractPathVars(SwatConfig.DATA_PATH, SwatConfig.DELIMITER);

		ExtractPrismData epd = new ExtractPrismData(SwatConfig.DATA_PATH, SwatConfig.DATA_SIZE, SwatConfig.STEP_SIZE, SwatConfig.DELIMITER);
		VariablesValueInfo vvi = epd.getVariablesValueInfo(varsSet);

		AlgoProfile.vars = vvi.getVars();	
		AlgoProfile.varsLength = vvi.getVarsLength();

		List<Predicate> pres = new ArrayList<>();
		pres.add(new TruePredicate());
		pres.add(new OverHigh(SwatConfig.SENSOR,SwatConfig.SENSOR_THRES)); // specify safety property SENSOR>SENSOR_THRES
//		pres.add(new UnderLow(SwatConfig.SENSOR, SwatConfig.SENSOR_THRES));
		
		AlgoProfile.predicates = pres;

		SingleTraceLAR lar = new SingleTraceLAR();
		lar.setProperty(pres);
		lar.setPredicate_set(pres);
		lar.setTRAINING_LOG_PATH(SwatConfig.TRAINING_LOG_PATH);// path of training data
		lar.setTESTING_LOG_PATH(SwatConfig.TESTING_LOG);
		lar.setMODEL_NAME("swat"); // model name
		lar.setOUTPUT_MODEL_PATH(SwatConfig.OUTPUT_MODEL_PATH); // path of output models
		GlobalConfigs.OUTPUT_MODEL_PATH = lar.getOUTPUT_MODEL_PATH(); 
		lar.setData_size(SwatConfig.DATA_SIZE);
		lar.setData_step_size(SwatConfig.STEP_SIZE); // sampling step size of data
		lar.setData_delimiter(SwatConfig.DELIMITER); // delimiter of data file
		lar.setSafety_thres(SwatConfig.SAFETY_THRESHOLD); // safety threshold
		lar.setEpsilon(SwatConfig.epsilon); // learning parameter
		
		TimeProfile.main_start_time = System.nanoTime();
		lar.execute(); // execute LAR
		TimeProfile.main_end_time = System.nanoTime();
		TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
		TimeProfile.outputTimeProfile(); // output time profile
		TimeProfile.outputTimeProfile(GlobalConfigs.OUTPUT_MODEL_PATH+"/time_profile.txt"); // write time profile to file
		System.out.println("End of the program.");


	}	

}
