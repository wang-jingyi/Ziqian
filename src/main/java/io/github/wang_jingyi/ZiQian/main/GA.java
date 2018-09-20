package io.github.wang_jingyi.ZiQian.main;

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(description = "Learn a probabilistic model from system traces using evolutionary algorithm.",
name = "GA", mixinStandardHelpOptions = true, version = "GA 1.0")
public class GA implements Callable<Void>{
	
	@Parameters(index = "0", description = "The name of the system to verify.")
	private String model_name;
	
	@Parameters(index = "1", description = "The directory path containing all the system traces.")
	private String trace_path;

	@Parameters(index = "2", description = "The directory path to store the results.")
	private String result_path;
	
	@Parameters(index = "3", description = "The path to the variables to learn from.")
	private String vars_path;
	
	@Option(names = {"--delimiter"}, description = "The delimiter of the trace files.")
	private String delimiter = " ";

	@Option(names = {"--step"}, description = "The sampling frequency.")
	private int data_step = 1;
	
	@Option(names = {"--length"}, description = "The total data length.")
	private int data_length = 20000;
	
	@Option(names = {"--mutation"}, description = "The mutation rate.")
	private double mutation_rate = 0.1;
	
	@Option(names = {"--generation"}, description = "The number of generations.")
	private int gen_num = 10;
	
	@Option(names = {"--size"}, description = "The number of chromosomes in each generation.")
	private int gen_size = 50;
	
	@Option(names = {"--prob"}, description = "The probability to select the winner.")
	private double select_prob = 0.9;
	
	public static void main(String[] args) throws Exception {
		CommandLine.call(new AA(), args);
	}

	@Override
	public Void call() throws Exception {
		GAEnginee enginee = new GAEnginee(model_name, trace_path, result_path, vars_path, delimiter, data_step, data_length, 
				mutation_rate, gen_num, gen_size, select_prob);
		enginee.execute();
		return null;
	}

}