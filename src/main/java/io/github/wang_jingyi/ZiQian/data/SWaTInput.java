package io.github.wang_jingyi.ZiQian.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.main.AlgoProfile;

public class SWaTInput {
	
	String training_log;
	String testing_log;
	List<Predicate> predicates;
	VariablesValueInfo training_vvi;
	VariablesValueInfo testing_vvi;
	List<String> varsSet;
	Input training_input;
	List<String> testing_input;
	List<String> previous_observation;
	String delimiter;
	int data_size;
	int step_size;
	
	public SWaTInput(String training_log, String testing_log, List<Predicate> predicates, int previous_count, int data_size,
			int step_size, String delimiter) {
		this.training_log = training_log;
		this.testing_log = testing_log;
		this.predicates = predicates;
		this.previous_observation = new ArrayList<>();
		this.data_size = data_size;
		this.step_size = step_size;
		this.delimiter = delimiter;
	}
	
	
	public void execute(){
		try {
			varsSet = PrismPathData.extractPathVars(training_log, delimiter);
			ExtractPrismData epd = new ExtractPrismData(training_log, data_size, step_size, delimiter, true);
			training_vvi = epd.getVariablesValueInfo(varsSet);
			PredicateAbstraction pa = new PredicateAbstraction(predicates);
			training_input = pa.abstractInput(training_vvi.getVarsValues());
			AlgoProfile.collect_training_data = false;
			
			ExtractPrismData epd1 = new ExtractPrismData(testing_log, Integer.MAX_VALUE, step_size, delimiter, true);
			testing_vvi = epd1.getVariablesValueInfo(varsSet);
			Input testing_data = pa.abstractInput(testing_vvi.getVarsValues());
			testing_input = testing_data.getObservations().get(0);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public VariablesValueInfo getTraining_vvi() {
		return training_vvi;
	}

	public VariablesValueInfo getTesting_vvi() {
		return testing_vvi;
	}

	public Input getAbstractTrainingInput() throws IOException{
		return training_input;
	}
	
	public List<String> getAbstractTestingInput() throws IOException{
		return testing_input;
	}
	
	public List<String> getPreviousObservation(){
		return previous_observation;
	}
	
	public List<Predicate> getPredicates(){
		return predicates;
	}
}
