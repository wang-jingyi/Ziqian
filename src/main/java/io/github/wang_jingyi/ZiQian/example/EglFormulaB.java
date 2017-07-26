package io.github.wang_jingyi.ZiQian.example;

import java.io.Serializable;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.data.VariablesValue;

public class EglFormulaB extends EglUnfair implements Predicate, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4642328675256518665L;

	public EglFormulaB() {
		super();
	}
	
	@Override
	public String getPredicateName() {
		return "unfairB";
	}

	@Override
	public List<String> getVariables() {
		return vars;
	}

	@Override
	public boolean check(VariablesValue vv) {
		if(EglUnfair.checkB(vv, vars)){
			return true;
		}
		return false;
	}


}
