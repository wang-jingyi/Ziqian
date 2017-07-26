package io.github.wang_jingyi.ZiQian.data;

import java.util.List;

import io.github.wang_jingyi.ZiQian.Input;

public interface VariableInfoExtraction {
	
	public Input extractVariableInfo(VariablesValueInfo vvi);
	public List<Integer> updateVarsLength(VariablesValueInfo vvi);
}
