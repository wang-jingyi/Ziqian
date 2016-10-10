package io.github.wang_jingyi.ZiQian.active;

import java.util.List;

public interface InitialDistGetter {
	
	public List<Double> getInitialDistribution(List<List<Integer>> frequencyMatrix, List<List<Double>> origEstimation);
	public void setValidInitialStates(List<Integer> validInitialStates);
	
}
