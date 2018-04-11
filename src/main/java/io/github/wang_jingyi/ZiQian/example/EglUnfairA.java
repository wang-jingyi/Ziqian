package io.github.wang_jingyi.ZiQian.example;

import java.io.Serializable;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.data.VariablesValue;

public class EglUnfairA extends EglUnfair implements Predicate, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5957084707729481231L;

	@Override
	public String getPredicateName() {
		return "Unfair_A";
	}

	@Override
	public List<String> getVariables() {
		return vars;
	}

	@Override
	public boolean check(VariablesValue vv) {
		if(!EglUnfair.checkB(vv, vars) && EglUnfair.checkB(vv, vars)){
			return true;
		}
		return false;
	}
	
	

}
