package io.github.wang_jingyi.ZiQian.main;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.NonAbstraction;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.data.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.example.CrowdPositive;
import io.github.wang_jingyi.ZiQian.example.EglFormulaA;
import io.github.wang_jingyi.ZiQian.example.EglFormulaB;
import io.github.wang_jingyi.ZiQian.example.NandReliable;
import io.github.wang_jingyi.ZiQian.learn.evolution.LearnMergeEvolutions;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.swat.property.UnderLow;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.LearnUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
	boolean random_length;
	String additional_trace_path;
	String model_setting;

	public GAEnginee(String model_name, String trace_path, String result_path,
			String vars_path, String delimiter, int data_step, int data_length,
			double mutation_rate, int gen_num, int gen_size,
			double select_prob, boolean random_length, String additional_trace_path, String model_setting) {
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
		this.random_length = random_length;
		this.additional_trace_path = additional_trace_path;
		this.model_setting = model_setting;
	}

	public void execute() throws IOException {

		ExtractPrismData epd = new ExtractPrismData(trace_path, data_length, data_step, delimiter, random_length);
		VariablesValueInfo vvl = epd.getVariablesValueInfo();
		
		// add the additional traces into GA for fair comparison (LAR testing traces) 
		if(additional_trace_path!=null){
			for(String testing_dir : FileUtil.foldersInDir(additional_trace_path)){
				ExtractPrismData epd_test = new ExtractPrismData(additional_trace_path + "/" + testing_dir, Integer.MAX_VALUE, data_step, delimiter, random_length);
				VariablesValueInfo vvi_test = epd_test.getVariablesValueInfo();
				vvl.updateVariableVarInfo(vvi_test.getVarsValues());
			}
		}
		
		List<String> vars = LearnUtil.extractVarsFromFile(vars_path);
		AlgoProfile.vars = vars;
		
		NonAbstraction na = new NonAbstraction(vars);
		AlgoProfile.varsLength = na.updateVarsLength(vvl); // must be executed before extract data
		Input data = na.extractVariableInfo(vvl);

		System.out.println("size of the alphabet: " + data.getAlphabet().size());
		System.out.println("size of learning data: " + data.getDataSize());

		List<Predicate> property = new ArrayList<>();
		if(model_name.equals("nand")){
			assert model_setting!=null : "=== Require model setting of NAND ======";
			property.add(new NandReliable(NandReliable.extractN(model_setting)));
		}
		else if(model_name.equals("crowds")){
			property.add(new CrowdPositive());
		}
		else if(model_name.equals("egl")){
			property.add(new EglFormulaA());
			property.add(new EglFormulaB());
		}
		else if(model_name.equals("swat")){
			property.add(new UnderLow("LIT101",250));
		}
		else{
			System.out.println("======= Please implement the property to verify ======");
			System.exit(0);
		}

		TimeProfile.learning_start_time = System.nanoTime();

		System.out.println("- learn by evolution...");
		LearnMergeEvolutions bestDTMC = new LearnMergeEvolutions();
		bestDTMC.learn(data);
		bestDTMC.PrismModelTranslation(data, property, model_name+data_length);
		
		
		result_path = result_path + "/exp-" + LearnUtil.formatTime(new Date(), "hh:mm:ss");
		
		// format to .pm file
		System.out.println("--- formatting the model to .pm file for model checking...");
		FormatPrismModel fpm = new FormatPrismModel("dtmc", result_path, model_name+data_length);
		fpm.translateToFormat(bestDTMC.getPrismModel(), data);
		TimeProfile.learning_end_time = System.nanoTime();
		TimeProfile.outputTimeProfile(result_path + "/time.txt");
		
		
		double learn_time = TimeProfile.nanoToSeconds(TimeProfile.learning_end_time-TimeProfile.learning_start_time);
		System.out.println("--- total learning time: " + learn_time);
		FileUtil.writeStringToFile(result_path+"/time.txt", learn_time + " s");
		System.out.println("=== end of the program ===");
	}

}
