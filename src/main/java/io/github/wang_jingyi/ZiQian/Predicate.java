package io.github.wang_jingyi.ZiQian;

import java.util.List;

public interface Predicate{
	
	public String getPredicateName();
	
	public List<String> getVariables();
	
	public boolean check(VariablesValue vv); // given valuation of a set of variables, check if a predicate holds
	
}
