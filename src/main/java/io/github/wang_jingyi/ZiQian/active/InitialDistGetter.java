package io.github.wang_jingyi.ZiQian.active;

import java.util.List;

public interface InitialDistGetter {
	
	public double[] getInitialDistribution(int[][] frequencyMatrix, double[][] origEstimation);
	public void setValidInitialStates(List<Integer> validInitialStates);
	
}
