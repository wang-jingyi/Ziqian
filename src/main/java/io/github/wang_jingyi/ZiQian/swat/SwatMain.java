package io.github.wang_jingyi.ZiQian.swat;

import io.github.wang_jingyi.ZiQian.LAR;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.TruePredicate;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.exceptions.SimulationException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedLearningTypeException;
import io.github.wang_jingyi.ZiQian.prism.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.prism.PrismPathData;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.profile.TimeProfile;
import io.github.wang_jingyi.ZiQian.sample.TestEnvironment;
import io.github.wang_jingyi.ZiQian.swat.property.OverHigh;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SwatMain {
	
public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, SimulationException, UnsupportedLearningTypeException {
		
		SwatConfig.writePropertyLearnFile();

		List<String> varsSet 
		= PrismPathData.extractPathVars(SwatConfig.DATA_PATH, SwatConfig.DELIMITER);
		
		TimeProfile.mainStartTime = System.nanoTime();
		ExtractPrismData epd = new ExtractPrismData(SwatConfig.DATA_PATH, SwatConfig.DATA_SIZE, SwatConfig.STEP_SIZE, SwatConfig.DELIMITER);
		VariablesValueInfo vvi = epd.getVariablesValueInfo(varsSet);
		
		AlgoProfile.vars = vvi.getVars();	
		AlgoProfile.varLength = vvi.getVarsLength();
		
		List<Predicate> pres = new ArrayList<>();
		pres.add(new TruePredicate());
		pres.add(new OverHigh("LIT401",999));
		
		AlgoProfile.predicates = pres;
		
		TestEnvironment te = TestEnvironment.te;
		
		LAR lar = new LAR();
		lar.setDATA_PATH(SwatConfig.DATA_PATH);
		lar.setLEARNING_TYPE("AA");
		lar.setMODEL_NAME("swat");
		lar.setOUTPUT_MODEL_PATH(SwatConfig.OUTPUT_MODEL_PATH);
		lar.setProperty(pres);
		lar.setPROPERTY_INDEX(SwatConfig.PROPERTY_INDEX);
		lar.setSampler(new SwatSampler());
		lar.setTe(te);
		lar.setVvi(vvi);
		lar.setBoundedSteps(-1);
		lar.setPROPERTY_LEARN_FILE(SwatConfig.PROPERTY_LEARN_FILE);
		lar.setSafetyBound(SwatConfig.SAFETY_THRESHOLD);
		
		lar.execute();
		System.out.println("End of the program.");
		
	}
	

}
