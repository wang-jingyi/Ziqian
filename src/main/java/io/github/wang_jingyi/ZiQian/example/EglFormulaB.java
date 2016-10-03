package io.github.wang_jingyi.ZiQian.example;

import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.VariablesValue;

public class EglFormulaB extends EglUnfair implements Predicate{
	
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
