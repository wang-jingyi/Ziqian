package io.github.wang_jingyi.ZiQian.example;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.data.VariablesValue;

import java.io.Serializable;
import java.util.List;

public class EglFormulaA extends EglUnfair implements Predicate, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1495459239778127253L;

	public EglFormulaA() {
		super();
	}

	@Override
	public String getPredicateName() {
		return "unfairA";
	}

	@Override
	public List<String> getVariables() {
		return vars;
	}

	@Override
	public boolean check(VariablesValue vv) {
		if(EglUnfair.checkA(vv, vars)){
			return true;
		}
		return false;
	}


}
