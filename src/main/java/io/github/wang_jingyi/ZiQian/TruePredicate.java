package io.github.wang_jingyi.ZiQian;

import java.io.Serializable;
import java.util.List;

public class TruePredicate implements Predicate, Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 9130418500059477410L;

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
