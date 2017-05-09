package io.github.wang_jingyi.ZiQian;

import java.util.ArrayList;
import java.util.List;


public class VariablesValue{
	
	private List<String> vars;
	private List<Value> values;
	
	public VariablesValue(List<String> vars){
		this.vars = vars;
		this.values = new ArrayList<Value>();
	}
	
	public VariablesValue(List<String> vars, List<Value> values){
		this.vars = vars;
		this.values = values;
	}

	public Value getVarValue(String var){
		for(int i=0; i<vars.size(); i++){
			if(var.equalsIgnoreCase(vars.get(i))){
				if(values.size()!=vars.size()){ // if not equal, desert this set of value
					return new Value("-1");
				}
				return values.get(i);
			}
		}
		assert false : "=== The given variable doesnt exist.";
		return null;
	}
	
	
	public List<String> getVars() {
		return vars;
	}
	public List<Value> getValues() {
		return values;
	}
	
	public String toString(){
		return values.toString();
	}
	
	public static VariablesValue stateToVariableValue(String state, List<String> vars, List<Integer> varsLength){
		List<Value> values = new ArrayList<>();
		int start = 0;
		for(int i=0; i<vars.size(); i++){
			Value v = new Value(state.substring(start, start+varsLength.get(i)));
			start = start + varsLength.get(i);
			values.add(v);
		}
		return new VariablesValue(vars,values);
	}
	
}
