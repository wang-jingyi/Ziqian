package io.github.wang_jingyi.ZiQian.main;

import io.github.wang_jingyi.ZiQian.CheckLearned;
import io.github.wang_jingyi.ZiQian.DTMCLearner;
import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.TruePredicate;
import io.github.wang_jingyi.ZiQian.data.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.data.PrismPathData;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.example.CrowdPositive;
import io.github.wang_jingyi.ZiQian.example.EglFormulaA;
import io.github.wang_jingyi.ZiQian.example.EglFormulaB;
import io.github.wang_jingyi.ZiQian.example.NandReliable;
import io.github.wang_jingyi.ZiQian.exceptions.PrismNoResultException;
import io.github.wang_jingyi.ZiQian.learn.AAlergia;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.refine.Counterexample;
import io.github.wang_jingyi.ZiQian.refine.CounterexampleGenerator;
import io.github.wang_jingyi.ZiQian.refine.CounterexamplePath;
import io.github.wang_jingyi.ZiQian.refine.ModelTesting;
import io.github.wang_jingyi.ZiQian.refine.PrismSampler;
import io.github.wang_jingyi.ZiQian.refine.Refiner;
import io.github.wang_jingyi.ZiQian.refine.Sampler;
import io.github.wang_jingyi.ZiQian.refine.SprtTest;
import io.github.wang_jingyi.ZiQian.refine.SwatSampler;
import io.github.wang_jingyi.ZiQian.refine.TestEnvironment;
import io.github.wang_jingyi.ZiQian.swat.property.UnderLow;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;




import io.github.wang_jingyi.ZiQian.utils.LearnUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LAREnginee {
	
	String model_name;
	String trace_path;
	String property_path;
	String result_path;
	String model;
	String model_setting;
	double alpha;
	double beta;
	double sigma;
	double min_svm_accuracy;
	int max_iteration;
	boolean selective_collect;
	boolean loop_first;
	String delimiter;
	int data_step;
	int data_length;
	int max_epsilon;
	String sampler;
	boolean random_length;

	public LAREnginee(String model_name, String trace_path, String property_path, String result_path, String model_and_config, String model_setting, double alpha, double beta, double sigma, 
			double min_svm_accuracy, int max_iteration, boolean selective_collect, boolean loop_first, String delimiter, int data_step, int data_length, int max_epsilon, String sampler, boolean random_length) {
		
		this.model_name = model_name;
		this.trace_path = trace_path;
		this.property_path = property_path;
		this.result_path = result_path;
		this.model = model_and_config;
		this.model_setting = model_setting;
		this.alpha = alpha;
		this.beta = beta;
		this.sigma = sigma;
		this.min_svm_accuracy = min_svm_accuracy;
		this.max_iteration = max_iteration;
		this.selective_collect = selective_collect;
		this.loop_first = loop_first;
		this.delimiter = delimiter;
		this.data_step = data_step;
		this.data_length = data_length;
		this.max_epsilon = max_epsilon;
		this.sampler = sampler;
		this.random_length = random_length;
	}

	public void execute() throws ClassNotFoundException, IOException {
		
		int iteration = 0;
		int lar_current_model_size = 0;
		
		double safety_thres = FileUtil.extractSafetyThreshold(property_path);
		assert safety_thres!=-1;
		
		String result_root_path = result_path + "/" + model_setting + "/thres="+ safety_thres + "/exp-" + LearnUtil.formatTime(new Date(), "hh:mm:ss");
		String testing_path = result_root_path + "/lar_paths";
		String result_model_path = result_root_path + "/lar_models";
		
		AlgoProfile.result_output_path = result_model_path;
		
		Sampler path_sampler = null;
		if(sampler.equals("prism")){
			path_sampler = new PrismSampler(model, testing_path, model_setting);
		}
		else if(sampler.equals("swat")){
			path_sampler = new SwatSampler(false, result_model_path, 
					AlgoProfile.SWAT_SAMPLE_STEP, AlgoProfile.SWAT_RECORD_STEP, AlgoProfile.SWAT_RUNNING_TIME);
		}
		else{
			System.out.println("====== Sampler not supported =======");
			System.exit(0);
		}
		
		TimeProfile.main_start_time = System.nanoTime();
		
		List<String> varsSet 
		= PrismPathData.extractPathVars(trace_path, delimiter);

		ExtractPrismData epd = new ExtractPrismData(trace_path, data_length, data_step, delimiter, random_length);
		VariablesValueInfo vvi = epd.getVariablesValueInfo(varsSet);
		
		
		List<Predicate> property = new ArrayList<>();
		property.add(new TruePredicate());
		if(model_name.equals("nand")){
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
		
		
		AlgoProfile.vars = varsSet;
		AlgoProfile.predicates = property;
		AlgoProfile.model_name = model_name;
		
		while(iteration<max_iteration){
			
			AlgoProfile.iterationCount = iteration;
			iteration++;
			
			// store the new sampled paths in separate directories
			String iter_testing_path = testing_path + "/iter_"+iteration;
			FileUtil.createDir(iter_testing_path);
			path_sampler.setOutputFilePath(iter_testing_path);
			
			TimeProfile.iteration_start_time = System.nanoTime();;
			System.out.println("\n****** Iteration : " + iteration + " ******\n");
			
			TimeProfile.learning_start_time = System.nanoTime();
			PredicateAbstraction pa = new PredicateAbstraction(property);
			Input data = pa.abstractInput(vvi.getVarsValues());
			
			
			TestEnvironment te = TestEnvironment.te;
			te.init(property, path_sampler, data, delimiter, data_step);
			
			System.out.println("------ Data information ------");
			System.out.println("- Data total length: " + vvi.getTotalLength());
			
			String iter_model_name = model_name + "_" + iteration;
			DTMCLearner learner = new DTMCLearner();
			
			// use AA for now
			learner.setLearner(new AAlergia(1,max_epsilon).selectCriterion(data));
			LearningDTMC bestDTMC = learner.getLearner();
			bestDTMC.learn(data);
			bestDTMC.PrismModelTranslation(data, pa.getPredicates(), iter_model_name);
			System.out.println("------ Writing learned model into PRISM format ------");
			
			FormatPrismModel fpm = new FormatPrismModel("dtmc", result_model_path, iter_model_name, true);
			fpm.translateToFormat(bestDTMC.getPrismModel(), data);
			System.out.println("- Learned model wrote to : " + result_model_path + "/" + iter_model_name + ".pm");
			System.out.println("- Number of states in the learned model: " + bestDTMC.getPrismModel().getNumOfPrismStates());
			TimeProfile.learning_end_time = System.nanoTime();
			TimeProfile.learning_times.add(TimeProfile.nanoToSeconds(TimeProfile.learning_end_time
					-TimeProfile.learning_start_time));
			
			/*
			 * test for AA and GA with basic abstraction
			 * */ 
//			System.exit(0);
			
			// update LAR model size
			if(bestDTMC.getPrismModel().getNumOfPrismStates()==lar_current_model_size){
				System.out.println("====== Cannot obtain a new linear predicate, verification fails ======");
				TimeProfile.main_end_time = System.nanoTime();
				TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
				TimeProfile.outputTimeProfile();
				TimeProfile.outputTimeProfile(result_model_path+"/time_profile.txt");
				FileUtil.writeObject(result_model_path + "/predicates", AlgoProfile.predicates);
				System.exit(0);
			}
			else{
				lar_current_model_size = bestDTMC.getPrismModel().getNumOfPrismStates();
			}
			
			TimeProfile.pmc_start_time = System.nanoTime();
			CheckLearned cl = new CheckLearned(result_model_path + "/" + iter_model_name + ".pm" , 
					property_path, 1); // the first property by default
			try {
				cl.check();
			} catch (PrismNoResultException e) {
				e.printStackTrace();
			}
			
			System.out.println("------ Generating counterexample ------");
			TimeProfile.ce_generation_start_time = System.nanoTime();
			CounterexampleGenerator counterg = new CounterexampleGenerator(bestDTMC.getPrismModel(),  // generate counterexamples
					-1, safety_thres); // currently only support unbounded properties
			List<CounterexamplePath> counterPaths = counterg.generateCounterexamples();
			TimeProfile.ce_generation_end_time = System.nanoTime();;
			TimeProfile.ce_generation_times.add(TimeProfile.nanoToSeconds(TimeProfile.ce_generation_end_time
					-TimeProfile.ce_generation_start_time));
			
			System.out.println("------ Hypothesis testing of counterexample ------");
			te.init(property, path_sampler, data, delimiter, data_step);
			ModelTesting mt = new ModelTesting();
			mt.setHypothesisTesting(new SprtTest(safety_thres, alpha, beta, sigma));
			
			
			Counterexample ce = new Counterexample(bestDTMC.getPrismModel(), counterPaths, mt.getHypothesisTesting(), loop_first);
			ce.analyze(te);
			
			System.out.println("------ Refine the predicate set ------");
			
			TimeProfile.refine_start_time = System.nanoTime();
			Refiner refiner = new Refiner(ce.getSortedSplittingPoints(), vvi, property, bestDTMC.getPrismModel(), random_length,
					selective_collect, min_svm_accuracy);
			Predicate newPredicate = refiner.refine();
			TimeProfile.refine_end_time = System.nanoTime();
			TimeProfile.refine_times.add(TimeProfile.nanoToSeconds(TimeProfile.refine_end_time
					-TimeProfile.refine_start_time));
			
			if(newPredicate==null){
				TimeProfile.iteration_end_time = System.nanoTime();
				TimeProfile.iteration_times.add(TimeProfile.nanoToSeconds
						(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time));
				System.out.println("======= Fail to learn a new predicate, verification fails ======");
				FileUtil.writeObject(result_model_path + "/predicates", AlgoProfile.predicates);
				TimeProfile.main_end_time = System.nanoTime();
				TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
				TimeProfile.outputTimeProfile();
				TimeProfile.outputTimeProfile(result_model_path + "/time_profile.txt");
				System.exit(0);
			}
			
			// add the new predicate to the predicate set
			property.add(newPredicate);
			
			// update the training data
			ExtractPrismData new_epd = new ExtractPrismData(iter_testing_path, data_length, data_step, delimiter, random_length);
			VariablesValueInfo new_testing_vvi = new_epd.getVariablesValueInfo(vvi.getVars());
			vvi.updateVariableVarInfo(new_testing_vvi.getVarsValues());
			
			TimeProfile.iteration_end_time = System.nanoTime();
			TimeProfile.iteration_times.add(TimeProfile.nanoToSeconds(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time));
			AlgoProfile.predicates = property;
			AlgoProfile.newIteration = true;
			AlgoProfile.iterationCount++;
		}
	}

}
