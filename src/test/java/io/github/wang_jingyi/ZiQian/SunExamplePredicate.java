package io.github.wang_jingyi.ZiQian;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.data.VariablesValue;

public class SunExamplePredicate implements Predicate, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4134343789776136221L;
	List<String> vars = new ArrayList<String>();
	
	public SunExamplePredicate() {
		vars.add("examplebit");
	}

	@Override
	public String getPredicateName() {
		return "toypredicate";
	}

	@Override
	public List<String> getVariables() {
		return vars;
	}

	@Override
	public boolean check(VariablesValue vv) {
		
		return false;
	}

}
