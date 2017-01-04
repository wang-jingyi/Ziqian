package io.github.wang_jingyi.ZiQian;

import java.util.List;
/*
 * an interface for predicate
 * 1) name
 * 2) relevant variables
 * 3) variable values
 * 
 * */

public interface Predicate {
	
	public String getPredicateName();
	
	public List<String> getVariables();
	
	public boolean check(VariablesValue vv); // given valuation of a set of variables, check if a predicate holds
	
}
