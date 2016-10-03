package io.github.wang_jingyi.ZiQian.example;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.VariablesValue;

import java.util.List;

public class EglFormulaA extends EglUnfair implements Predicate{
	
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
