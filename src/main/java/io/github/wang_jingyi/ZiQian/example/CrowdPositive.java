package io.github.wang_jingyi.ZiQian.example;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.VariablesValue;

import java.util.ArrayList;
import java.util.List;

public class CrowdPositive implements Predicate{
	
	List<String> vars = new ArrayList<>();
	
	public CrowdPositive() {
		vars.add("observe0");
	}

	@Override
	public List<String> getVariables() {
		return vars;
	}

	@Override
	public boolean check(VariablesValue vv) {
		int observe_0 = vv.getVarValue(vars.get(0)).getRawIntValue();
		if(observe_0>1){
			return true;
		}
		return false;
	}

	@Override
	public String getPredicateName() {
		return "positive";
	}


}
