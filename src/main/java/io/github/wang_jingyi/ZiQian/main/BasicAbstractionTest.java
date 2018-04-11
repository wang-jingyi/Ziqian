package io.github.wang_jingyi.ZiQian.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.DTMCLearner;
import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.TruePredicate;
import io.github.wang_jingyi.ZiQian.data.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.data.PrismPathData;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.example.NandReliable;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedLearningTypeException;
import io.github.wang_jingyi.ZiQian.learn.AAlergia;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.learn.evolution.LearnMergeEvolutions;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

public class BasicAbstractionTest {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, UnsupportedLearningTypeException {

		Config.initConfig();
		
		List<Predicate> pres = new ArrayList<>();
		pres.add(new TruePredicate());
//		pres.add(new OverHigh(Config.SENSOR,Config.SENSOR_THRES));
		pres.add(new NandReliable(60));
//		pres.add(new EglFormulaA());
//		pres.add(new EglFormulaB());
//		pres.add(new CrowdPositive());
//		pres.add(new UnderLow("LIT101",250));
//		pres.add(new OverHigh("LS602",580));
//		pres.add(new EglUnfairA());

		List<String> varsSet = PrismPathData.extractPathVars(Config.DATA_PATH, Config.DELIMITER);
		ExtractPrismData epd = new ExtractPrismData(Config.DATA_PATH, Config.DATA_SIZE, Config.STEP_SIZE, Config.DELIMITER);
		VariablesValueInfo vvi = epd.getVariablesValueInfo(varsSet);
		
		for(String testing_dir : FileUtil.foldersInDir(Config.TESTING_PATH)){
			ExtractPrismData epd_test = new ExtractPrismData(Config.TESTING_PATH+"/"+testing_dir, Integer.MAX_VALUE, Config.STEP_SIZE, Config.DELIMITER);
			VariablesValueInfo vvi_test = epd_test.getVariablesValueInfo(varsSet);
			vvi.updateVariableVarInfo(vvi_test.getVarsValues());
		}
		
		PredicateAbstraction pa = new PredicateAbstraction(pres);
		Input data = pa.abstractInput(vvi.getVarsValues());
		
		DTMCLearner learner = new DTMCLearner();
		
		if(Config.LEARN_METHOD.equals("AA")){
			learner.setLearner(new AAlergia(1,64).selectCriterion(data));
		}
		else if(Config.LEARN_METHOD.equals("GA")){
			learner.setLearner(new LearnMergeEvolutions());
		}
		else{
			throw new UnsupportedLearningTypeException();
		}
		
		LearningDTMC bestDTMC = learner.getLearner();
		bestDTMC.learn(data);
		bestDTMC.PrismModelTranslation(data, pa.getPredicates(), Config.MODEL_NAME);
		System.out.println("------ Writing learned model into PRISM format ------");
		FormatPrismModel fpm = new FormatPrismModel("dtmc", Config.BASIC_OUTPUT_PATH, Config.MODEL_NAME, true);
		fpm.translateToFormat(bestDTMC.getPrismModel(), data);
		System.out.println("- Learned model wrote to : " + Config.BASIC_OUTPUT_PATH + "/" + Config.MODEL_NAME + ".pm");
		System.out.println("- Number of states in the learned model: " + bestDTMC.getPrismModel().getNumOfPrismStates());
		TimeProfile.learning_end_time = System.nanoTime();
		TimeProfile.learning_times.add(TimeProfile.nanoToSeconds(TimeProfile.learning_end_time
				-TimeProfile.learning_start_time));

	}

}
