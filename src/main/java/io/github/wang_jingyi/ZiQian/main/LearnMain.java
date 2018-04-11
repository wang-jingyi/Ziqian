package io.github.wang_jingyi.ZiQian.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.NonAbstraction;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.data.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.example.CrowdPositive;
import io.github.wang_jingyi.ZiQian.learn.AAlergia;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.learn.ModelSelection;
import io.github.wang_jingyi.ZiQian.learn.evolution.LearnMergeEvolutions;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

public class LearnMain {

	public static void main(String[] args) throws IOException, ClassNotFoundException{
		
		ExtractPrismData epd = new ExtractPrismData(Config.DATA_PATH, Config.DATA_SIZE, 1, Config.DELIMITER);
		VariablesValueInfo vvl = epd.getVariablesValueInfo();
		
		for(String testing_dir : FileUtil.foldersInDir(Config.TESTING_PATH)){
			ExtractPrismData epd_test = new ExtractPrismData(Config.TESTING_PATH+"/"+testing_dir, Integer.MAX_VALUE, Config.STEP_SIZE, Config.DELIMITER);
			VariablesValueInfo vvi_test = epd_test.getVariablesValueInfo();
			vvl.updateVariableVarInfo(vvi_test.getVarsValues());
		}
		
		// swat 
//		Config.vars.add("LIT101");
//		Config.vars.add("LIT301");
//		Config.vars.add("LIT401");
//		Config.vars.add("LS601");
//		Config.vars.add("LS602");
//		
		// nand
//		AlgoProfile.vars.add("s");
//		AlgoProfile.vars.add("z");

		// crowds
		AlgoProfile.vars.add("runCount");
		AlgoProfile.vars.add("new");
		AlgoProfile.vars.add("lastSeen");
		AlgoProfile.vars.add("observe0");
//		
		NonAbstraction na = new NonAbstraction(AlgoProfile.vars);
		AlgoProfile.varsLength = na.updateVarsLength(vvl); // must be executed before extract data
		Input data = na.extractVariableInfo(vvl);
		
		System.out.println("size of the alphabet: " + data.getAlphabet().size());
		System.out.println("size of learning data: " + data.getDataSize());
		
		Config.initConfig();
		
		List<Predicate> pl = new ArrayList<>();
//		pl.add(new NandReliable(60)); // nand property
		pl.add(new CrowdPositive());  // crowds property
		
//		pl.add(new Overflow());
//		pl.add(new Underflow());
		
		TimeProfile.learning_start_time = System.nanoTime();
		
		if(Config.LEARN_METHOD.equals("AA")){
			System.out.println("- learn by aalergia...");
			ModelSelection gs = new AAlergia(1, Math.pow(2, 6)); //
			LearningDTMC bestDTMC = gs.selectCriterion(data);
			bestDTMC.PrismModelTranslation(data, pl, Config.MODEL_NAME+Config.DATA_SIZE); //
			// format to .pm file
			System.out.println("--- formatting the model to .pm file for model checking...");
			FormatPrismModel fpm = new FormatPrismModel("dtmc", Config.AA_OUTPUT_PATH, Config.MODEL_NAME+Config.DATA_SIZE);
			fpm.translateToFormat(bestDTMC.getPrismModel(), data);
			TimeProfile.learning_end_time = System.nanoTime();
		}
		
		else if(Config.LEARN_METHOD.equals("GA")){
			System.out.println("- learn by evolution...");
			LearnMergeEvolutions bestDTMC = new LearnMergeEvolutions();
			bestDTMC.learn(data);
			bestDTMC.PrismModelTranslation(data, pl, Config.MODEL_NAME+Config.DATA_SIZE);
			// format to .pm file
			System.out.println("--- formatting the model to .pm file for model checking...");
			FormatPrismModel fpm = new FormatPrismModel("dtmc", Config.GA_OUTPUT_PATH, Config.MODEL_NAME+Config.DATA_SIZE);
			fpm.translateToFormat(bestDTMC.getPrismModel(), data);
			TimeProfile.learning_end_time = System.nanoTime();
		}
		System.out.println("--- total learning time: " + TimeProfile.nanoToSeconds(TimeProfile.learning_end_time-TimeProfile.learning_start_time));
		System.out.println("=== end of the program ===");

	}



}
