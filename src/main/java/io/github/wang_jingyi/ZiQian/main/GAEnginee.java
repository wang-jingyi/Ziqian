package io.github.wang_jingyi.ZiQian.main;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.NonAbstraction;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.data.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.example.CrowdPositive;
import io.github.wang_jingyi.ZiQian.learn.evolution.LearnMergeEvolutions;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.LearnUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GAEnginee {
	
	String model_name;
	String trace_path;
	String result_path;
	String vars_path;
	String delimiter;
	int data_step;
	int data_length;
	double mutation_rate;
	int gen_num;
	int gen_size;
	double select_prob;

	public GAEnginee(String model_name, String trace_path, String result_path,
			String vars_path, String delimiter, int data_step, int data_length,
			double mutation_rate, int gen_num, int gen_size,
			double select_prob) {
		this.model_name = model_name;
		this.trace_path = trace_path;
		this.result_path = result_path;
		this.vars_path = vars_path;
		this.delimiter = delimiter;
		this.data_step = data_step;
		this.data_length = data_length;
		this.mutation_rate = mutation_rate;
		this.gen_num = gen_num;
		this.gen_size = gen_size;
		this.select_prob = select_prob;
	}

	public void execute() throws IOException {

		ExtractPrismData epd = new ExtractPrismData(trace_path, data_length, data_step, delimiter);
		VariablesValueInfo vvl = epd.getVariablesValueInfo();

		for(String testing_dir : FileUtil.foldersInDir(Config.TESTING_PATH)){
			ExtractPrismData epd_test = new ExtractPrismData(Config.TESTING_PATH+"/"+testing_dir, Integer.MAX_VALUE, Config.STEP_SIZE, Config.DELIMITER);
			VariablesValueInfo vvi_test = epd_test.getVariablesValueInfo();
			vvl.updateVariableVarInfo(vvi_test.getVarsValues());
		}
		
		List<String> vars = LearnUtil.extractVarsFromFile(vars_path);
		AlgoProfile.vars = vars;
		
		NonAbstraction na = new NonAbstraction(vars);
		AlgoProfile.varsLength = na.updateVarsLength(vvl); // must be executed before extract data
		Input data = na.extractVariableInfo(vvl);

		System.out.println("size of the alphabet: " + data.getAlphabet().size());
		System.out.println("size of learning data: " + data.getDataSize());

		List<Predicate> pl = new ArrayList<>();
		//		pl.add(new NandReliable(60)); // nand property
		pl.add(new CrowdPositive());  // crowds property

		//		pl.add(new Overflow());
		//		pl.add(new Underflow());

		TimeProfile.learning_start_time = System.nanoTime();

		System.out.println("- learn by evolution...");
		LearnMergeEvolutions bestDTMC = new LearnMergeEvolutions();
		bestDTMC.learn(data);
		bestDTMC.PrismModelTranslation(data, pl, Config.MODEL_NAME+Config.DATA_SIZE);
		// format to .pm file
		System.out.println("--- formatting the model to .pm file for model checking...");
		FormatPrismModel fpm = new FormatPrismModel("dtmc", Config.GA_OUTPUT_PATH, Config.MODEL_NAME+Config.DATA_SIZE);
		fpm.translateToFormat(bestDTMC.getPrismModel(), data);
		TimeProfile.learning_end_time = System.nanoTime();
		
		System.out.println("--- total learning time: " + TimeProfile.nanoToSeconds(TimeProfile.learning_end_time-TimeProfile.learning_start_time));
		System.out.println("=== end of the program ===");
	}

}
