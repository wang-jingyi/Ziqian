package io.github.wang_jingyi.ZiQian;

import java.util.List;

public interface VariableInfoExtraction {
	
	public Input extractVariableInfo(VariablesValueInfo vvi);
	public List<Integer> updateVarsLength(VariablesValueInfo vvi);
}
