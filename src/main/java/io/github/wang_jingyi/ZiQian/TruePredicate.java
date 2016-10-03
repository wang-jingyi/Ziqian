package io.github.wang_jingyi.ZiQian;

import java.util.List;

public class TruePredicate implements Predicate{
	

	@Override
	public String getPredicateName() {
		return "hold";
	}

	@Override
	public List<String> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean check(VariablesValue vv) {
		return true;
	}


}
