package io.github.wang_jingyi.ZiQian.swat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.TruePredicate;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedLearningTypeException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedTestingTypeException;
import io.github.wang_jingyi.ZiQian.learn.LearnPST;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.prism.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismPathData;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.swat.property.OverHigh;

public class SwatSingleMain {

	public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLearningTypeException, UnsupportedTestingTypeException {
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
		
		PredicateAbstraction pa = new PredicateAbstraction(pres);
		Input data = pa.abstractInput(vvi.getVarsValues());
		LearningDTMC learner = new LearnPST(0.0001);
		learner.learn(data);
		learner.PrismModelTranslation(data, pres, "swat");
		FormatPrismModel fpm = new FormatPrismModel("dtmc", SwatConfig.OUTPUT_MODEL_PATH, "swat", true);
		fpm.translateToFormat(learner.getPrismModel(),data);
		
		
		
		
	}	

}
