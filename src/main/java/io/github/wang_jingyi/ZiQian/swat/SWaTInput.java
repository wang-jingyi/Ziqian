package io.github.wang_jingyi.ZiQian.swat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.prism.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.prism.PrismPathData;

public class SWaTInput {
	
	String training_log;
	String testing_log;
	List<Predicate> predicates;
	int previous_count = 100;
	List<String> previous_observation;
	
	public SWaTInput(String training_log, String testing_log, List<Predicate> predicates, int previous_count) {
		this.training_log = training_log;
		this.testing_log = testing_log;
		this.predicates = predicates;
		this.previous_count = previous_count;
		this.previous_observation = new ArrayList<>();
	}
	
	public Input getAbstractInput() throws IOException{
		List<String> varsSet 
		= PrismPathData.extractPathVars(training_log, SwatConfig.DELIMITER);
		ExtractPrismData epd = new ExtractPrismData(training_log, SwatConfig.DATA_SIZE, SwatConfig.STEP_SIZE, SwatConfig.DELIMITER);
		VariablesValueInfo vvi = epd.getVariablesValueInfo(varsSet);
		
		PredicateAbstraction pa = new PredicateAbstraction(predicates);
		Input data = pa.abstractInput(vvi.getVarsValues());
		return data;
	}
	
	public List<String> getAbstractTestingLog() throws IOException{
		List<String> varsSet 
		= PrismPathData.extractPathVars(testing_log, SwatConfig.DELIMITER);
		ExtractPrismData epd = new ExtractPrismData(training_log, SwatConfig.DATA_SIZE, SwatConfig.STEP_SIZE, SwatConfig.DELIMITER);
		VariablesValueInfo vvi = epd.getVariablesValueInfo(varsSet);
		
		PredicateAbstraction pa = new PredicateAbstraction(predicates);
		Input data = pa.abstractInput(vvi.getVarsValues());
		previous_observation = data.getObservations().get(0).subList(0, previous_count);
		return data.getObservations().get(0);
	}
	
	public List<String> getPreviousObservation(){
		return previous_observation;
	}
	
	public List<Predicate> getPredicates(){
		return predicates;
	}
}
