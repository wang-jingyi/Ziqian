package io.github.wang_jingyi.ZiQian.main;

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


@Command(description = "Verify a safety property from system traces through learning, abstraction and refinement",
name = "LAR", mixinStandardHelpOptions = true, version = "LAR 1.0")
public class LearnToVerify implements Callable<Void>{
	
	@Parameters(index = "0", description = "The name of the system to verify.")
	private String model_name;
	
	@Parameters(index = "1", description = "The directory path containing all the system traces.")
	private String trace_path;

	@Parameters(index = "2", description = "The path to the property to verify.")
	private String property_path;

	@Parameters(index = "3", description = "The directory path to store the results.")
	private String result_path;
	
	@Parameters(index = "4", description = "The model to sample new traces for hypothesis testing.")
	private String model;
	
	@Parameters(index = "5", description = "The model setting to sample new traces for hypothesis testing.")
	private String model_setting;
	
	@Option(names = {"--alpha"}, description = "The Type-1 error bound of hypothesis testing.")
	private double alpha = 0.05;
	
	@Option(names = {"--beta"}, description = "The Type-2 error bound of hypothesis testing.")
	private double beta = 0.05;
	
	@Option(names = {"--sigma"}, description = "The indifference region of hypothesis testing.")
	private double sigma = 0.05;

	@Option(names = {"--min_acc"}, description = "The minimum classification accuracy for SVM.")
	private double min_svm_accuracy = 0.8;
	
	@Option(names = {"--max_iter"}, description = "The maximum number of iterations.")
	private int max_iteration = 20;
	
	@Option(names = {"--collect"}, description = "Whether to collect all or part of the concrete states.")
	private boolean selective_collect = false;

	@Option(names = {"--loop"}, description = "Whether to refine loops first.")
	private boolean loop_first = false;

	@Option(names = {"--delimiter"}, description = "The delimiter of the trace files.")
	private String delimiter = " ";

	@Option(names = {"--step"}, description = "The sampling frequency.")
	private int data_step = 1;
	
	@Option(names = {"--length"}, description = "The total data length.")
	private int data_length = 20000;
	
	@Option(names = {"--epsilon"}, description = "The maximum epsilon to choose from.")
	private int max_epsilon = 64;
	
	@Option(names = {"--sampler"}, description = "The sampler to sample a new path.")
	private String sampler = "prism";
	
	@Option(names = {"--random_length"}, description = "Whether the sample length is randmized.")
	private boolean random_length = false;
	
	public static void main(String[] args) throws Exception {
		CommandLine.call(new LearnToVerify(), args);
	}

	@Override
	public Void call() throws Exception {
		LAREnginee enginee = new LAREnginee(model_name, trace_path, property_path, result_path, model, model_setting, alpha, beta, sigma, min_svm_accuracy,
				max_iteration, selective_collect, loop_first, delimiter, data_step, data_length, max_epsilon, sampler, random_length);
		enginee.execute();
		return null;
	}

}
