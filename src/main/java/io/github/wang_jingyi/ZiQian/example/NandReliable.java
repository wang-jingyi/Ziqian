package io.github.wang_jingyi.ZiQian.example;

import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.VariablesValue;

public class NandReliable implements Predicate{
	
	List<String> vars = new ArrayList<>();
	int N;
	
	public NandReliable(int N) {
		this.N = N;
		vars.add("s");
		vars.add("z");
	}

	@Override
	public String getPredicateName() {
		return "reliable";
	}

	@Override
	public List<String> getVariables() {
		return vars;
	}

	@Override
	public boolean check(VariablesValue vv) {
		
		int s = vv.getVarValue(vars.get(0)).getRawIntValue();
		int z = vv.getVarValue(vars.get(1)).getRawIntValue();
		
		if(s==4 && (double)z/N<0.1){
			return true;
		}
		return false;
	}


}
