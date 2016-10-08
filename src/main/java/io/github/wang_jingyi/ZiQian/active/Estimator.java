package io.github.wang_jingyi.ZiQian.active;



public interface Estimator {
	
	public MarkovChain estimate(int[][] frequencyMatrix);

}
