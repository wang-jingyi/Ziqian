package io.github.wang_jingyi.ZiQian.swat;

import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.VariablesValue;

public class Underflow implements Predicate{
	
	List<String> vars = new ArrayList<>();

	public Underflow() {
		vars.add("LIT101");
	}
	
	@Override
	public String getPredicateName() {
		return "underflow";
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
		if(sensorValue<250){
			return true;
		}
		return false;
	}

}
