package io.github.wang_jingyi.ZiQian.swat.property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.VariablesValue;

public class OverHigh implements Predicate, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 262212472467613540L;
	private List<String> vars = new ArrayList<>();
	private double threshold;
	
	public OverHigh(String tankSensor, double threshold) {
		this.vars.add(tankSensor);
		this.threshold = threshold;
	}
	
	@Override
	public String getPredicateName() {
		return "swat_error";
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
		if(sensorValue>threshold){
			return true;
		}
		return false;
	}

}
