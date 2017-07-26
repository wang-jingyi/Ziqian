package io.github.wang_jingyi.ZiQian.refine;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;

import java.io.IOException;
import java.util.List;

public class TestEnvironment {
	
	public static final TestEnvironment te = new TestEnvironment();
	private List<Predicate> predicates;
	private Input training_data;
	private Sampler sampler; // to get new samples
	private String data_delimiter; // to extract samples
	private int data_step_size;
	
	private TestEnvironment(){
		super();
	}
	
	public void init(List<Predicate> predicates, Sampler sampler, Input training_data, 
			String delimiter, int step_size){
		this.predicates = predicates;
		this.sampler = sampler;
		this.training_data = training_data;
		this.data_delimiter = delimiter;
		this.data_step_size = step_size;
	}
	
	public String getData_delimiter() {
		return data_delimiter;
	}

	public int getData_step_size() {
		return data_step_size;
	}

	public boolean test(List<String> trace, Counterexample ce) throws IOException, ClassNotFoundException{
		return ce.checkMembership(trace);
	}
	
	public List<Predicate> getPredicates() {
		return predicates;
	}

	public Sampler getSampler() {
		return sampler;
	}

	public Input getTraining_data() {
		return training_data;
	}
	
}
