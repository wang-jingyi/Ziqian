package io.github.wang_jingyi.ZiQian.run;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.NonAbstraction;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateSet;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.evolution.LearnMergeEvolutions;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.learn.ModelSelection;
import io.github.wang_jingyi.ZiQian.learn.PrefixMergeGoldenSearch;
import io.github.wang_jingyi.ZiQian.prism.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.profile.TimeProfile;
import io.github.wang_jingyi.ZiQian.swat.Underflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LearnMain {

	public static void main(String[] args) throws IOException, ClassNotFoundException{
		
		TimeProfile.mainStartTime = System.nanoTime();
		
		ExtractPrismData epd = new ExtractPrismData(Config.DATA_PATH, Config.DATA_SIZE);
		VariablesValueInfo vvl = epd.getVariablesValueInfo();
		
		// swat 
		Config.vars.add("LIT101");
		Config.vars.add("LIT301");
		Config.vars.add("LIT401");
		Config.vars.add("LS601");
		Config.vars.add("LS602");
		
		// nand
//		Config.vars.add("s");
//		Config.vars.add("z");

		// crowds
//		Config.vars.add("runCount");
//		Config.vars.add("new");
//		Config.vars.add("lastSeen");
//		Config.vars.add("observe0");
		
		NonAbstraction na = new NonAbstraction(Config.vars);
		Config.varsLength = na.updateVarsLength(vvl); // must be executed before extract data
		Input data = na.extractVariableInfo(vvl);
		
		System.out.println("size of the alphabet: " + data.getAlphabet().size());
		System.out.println("size of learning data: " + data.getDataSize());
		
		Config.initConfig();
		
		List<Predicate> pl = new ArrayList<>();
//		pl.add(new NandReliable(60)); // nand property
//		pl.add(new CrowdPositive());  // crowds property
		
//		pl.add(new Overflow());
		pl.add(new Underflow());
		PredicateSet ps = new PredicateSet(pl);
		
		if(Config.LEARN_METHOD.equals("AA")){
			System.out.println("learn by aalergia...");
			ModelSelection gs = new PrefixMergeGoldenSearch(Math.pow(2, -6), Math.pow(2, 6)); //
			LearningDTMC bestDTMC = gs.selectCriterion(data);
			bestDTMC.PrismModelTranslation(data, ps, Config.MODEL_NAME+Config.DATA_SIZE); //
			// format to .pm file
			System.out.println("formatting the model to .pm file for model checking...");
			FormatPrismModel fpm = new FormatPrismModel("dtmc", Config.AA_OUTPUT_PATH, Config.MODEL_NAME+Config.DATA_SIZE);
			fpm.translateToFormat(bestDTMC.getPrismModel(), data);
			TimeProfile.mainEndTime = System.nanoTime();
			TimeProfile.outputTimeProfile(Config.AA_OUTPUT_PATH+"/time_profile_"+Config.DATA_SIZE+".txt");
		}
		
		else if(Config.LEARN_METHOD.equals("GA")){
			System.out.println("learn by evolution...");
			LearnMergeEvolutions bestDTMC = new LearnMergeEvolutions();
			bestDTMC.learn(data);
			bestDTMC.PrismModelTranslation(data, ps, Config.MODEL_NAME+Config.DATA_SIZE);
			// format to .pm file
			System.out.println("formatting the model to .pm file for model checking...");
			FormatPrismModel fpm = new FormatPrismModel("dtmc", Config.GA_OUTPUT_PATH, Config.MODEL_NAME+Config.DATA_SIZE);
			fpm.translateToFormat(bestDTMC.getPrismModel(), data);
			TimeProfile.mainEndTime = System.nanoTime();
			TimeProfile.outputTimeProfile(Config.GA_OUTPUT_PATH+"/time_profile_"+Config.DATA_SIZE+".txt");
		}
		
		System.out.println("end of the program");

	}



}
