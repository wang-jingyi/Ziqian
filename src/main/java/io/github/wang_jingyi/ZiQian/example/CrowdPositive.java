package io.github.wang_jingyi.ZiQian.example;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.data.VariablesValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * property to verify for Crowds of PRISM benchmark: http://www.prismmodelchecker.org/casestudies/crowds.php
 * a property should implement Predicate and Serializable (for saving objects) interface
 * */

public class CrowdPositive implements Predicate, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8374509487552795407L;
	List<String> vars = new ArrayList<>();
	
	public CrowdPositive() {
		vars.add("observe0"); // add the relevant variables
	}

	@Override
	public List<String> getVariables() { // return the relevant variables of the predicate
		return vars;
	}

	@Override
	public boolean check(VariablesValue vv) { // check if the variable valuation of a state satisfies the predicate (property)
		int observe_0 = vv.getVarValue(vars.get(0)).getRawIntValue(); // get the value of observe_0 of current state
		if(observe_0>1){ // check if the valuation of the variables in the current state satisfies the predicate
			return true;
		}
		return false;
	}

	@Override
	public String getPredicateName() { // return the name of the predicate
		return "positive";
	}


}
