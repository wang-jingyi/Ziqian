package io.github.wang_jingyi.ZiQian.active;

public interface InitialDistGetter {
	
	public double[] getInitialDistribution(int[][] frequencyMatrix, double[][] origEstimation);
	
}
