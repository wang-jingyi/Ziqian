package io.github.wang_jingyi.ZiQian.swat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.VariablesValue;

public class Overflow implements Predicate, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 262212472467613540L;
	List<String> vars = new ArrayList<>();
	
	public Overflow() {
		vars.add("LS602");
	}
	
	@Override
	public String getPredicateName() {
		return "overflow";
	}

	@Override
	public List<String> getVariables() {
		return vars;
	}

	@Override
	public boolean check(VariablesValue vv) {
		if(vv.getValues().size()==0){
			return false;
		}
		double sensorValue = vv.getVarValue(vars.get(0)).getRawIntValue();
		if(sensorValue>580){
			return true;
		}
		return false;
	}

}
