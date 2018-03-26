package io.github.wang_jingyi.ZiQian.data;

import java.util.List;

public class VariablesValueInfo {
	
	List<String> vars;
	List<Integer> varsLength;
	List<List<VariablesValue>> varsValues;
	int totalLength;
	
	public VariablesValueInfo(List<String> vars, List<Integer> varslength, List<List<VariablesValue>> vvs) {
		this.vars = vars;
		this.varsLength = varslength;
		this.varsValues = vvs;
		updateTotalLength();
	}
	
	private void updateTotalLength(){
		totalLength = 0;
		for(List<VariablesValue> vvl : varsValues){
			totalLength += vvl.size();
		}
	}

	public List<List<VariablesValue>> getVarsValues() {
		return varsValues;
	}

	public List<String> getVars() {
		return vars;
	}

	public List<Integer> getVarsLength() {
		return varsLength;
	}

	public int getTotalLength() {
		updateTotalLength();
		return totalLength;
	}

}
