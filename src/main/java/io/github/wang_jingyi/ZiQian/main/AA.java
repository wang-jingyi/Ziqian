package io.github.wang_jingyi.ZiQian.main;

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(description = "Learn a probabilistic model from system traces using AAlergia algorithm.",
name = "AA", mixinStandardHelpOptions = true, version = "AA 1.0")
public class AA implements Callable<Void>{
	
	@Parameters(index = "0", description = "The name of the system to learn.")
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
	
	@Option(names = {"--epsilon"}, description = "The maximum epsilon to choose from.")
	private double max_epsilon = 64;
	
	public static void main(String[] args) throws Exception {
		CommandLine.call(new AA(), args);
	}

	@Override
	public Void call() throws Exception {
		AAEnginee enginee = new AAEnginee(model_name, trace_path, result_path, vars_path, delimiter, data_step, data_length, max_epsilon);
		enginee.execute();
		return null;
	}

}