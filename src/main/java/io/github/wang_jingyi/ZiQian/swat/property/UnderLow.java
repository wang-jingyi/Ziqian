package io.github.wang_jingyi.ZiQian.swat.property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.VariablesValue;

public class UnderLow implements Predicate, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4971393425170521837L;
	private List<String> vars = new ArrayList<>();
	private int threshold;

	public UnderLow(String tankSensor, int threshold) {
		this.vars.add(tankSensor);
		this.threshold = threshold;
	}
	
	@Override
	public String getPredicateName() {
		return "under";
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
		int sensorValue = vv.getVarValue(vars.get(0)).getRawIntValue();
		if(sensorValue<threshold){
			return true;
		}
		return false;
	}

}
