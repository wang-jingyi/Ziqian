package io.github.wang_jingyi.ZiQian.singtel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.data.VariablesValue;

public class LabelPredicate implements Predicate, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6597157343152997108L;
	int target_number;
	List<String> vars = new ArrayList<>();
	
	public LabelPredicate(int target_number) {
		this.target_number = target_number;
		this.vars.add("target");
	}

	@Override
	public String getPredicateName() {
		return "is_target_label";
	}

	@Override
	public List<String> getVariables() {
		return vars;
	}

	@Override
	public boolean check(VariablesValue vv) {
		
		int target = vv.getVarValue(vars.get(0)).getRawIntValue(); // get the value of "target" of current state
		if(target==target_number){ // check if the valuation of the variables in the current state satisfies the predicate
			return true;
		}
		return false;
	}

}
