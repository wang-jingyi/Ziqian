package io.github.wang_jingyi.ZiQian.main;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.NonAbstraction;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.data.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.example.CrowdPositive;
import io.github.wang_jingyi.ZiQian.learn.AAlergia;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.learn.ModelSelection;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.LearnUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AAEnginee {


	String model_name;
	String trace_path;
	String result_path;
	String vars_path;
	String delimiter;
	int data_step;
	int data_length;
	double max_epsilon;

	public AAEnginee(String model_name, String trace_path, String result_path, String vars_path, String delimiter, int data_step, int data_length, double max_epsilon) {
		this.model_name = model_name;
		this.trace_path = trace_path;
		this.result_path = result_path;
		this.vars_path = vars_path;
		this.delimiter = delimiter;
		this.data_step = data_step;
		this.data_length = data_length;
		this.max_epsilon = max_epsilon;
	}

	public void execute() throws FileNotFoundException, ClassNotFoundException, IOException {
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

		System.out.println("- learn by aalergia...");
		ModelSelection gs = new AAlergia(1, max_epsilon); //
		LearningDTMC bestDTMC = gs.selectCriterion(data);
		bestDTMC.PrismModelTranslation(data, pl, model_name+data_length); //
		
		// format to .pm file
		System.out.println("--- formatting the model to .pm file for model checking...");
		FormatPrismModel fpm = new FormatPrismModel("dtmc", result_path, model_name+data_length);
		fpm.translateToFormat(bestDTMC.getPrismModel(), data);
		System.out.println("--- total learning time: " + TimeProfile.nanoToSeconds(TimeProfile.learning_end_time-TimeProfile.learning_start_time));
		System.out.println("=== end of the program ===");
	}
}