package io.github.wang_jingyi.ZiQian.active;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

public interface InitialDistGetter {
	
	public double[] getInitialDistribution(RealMatrix frequencyMatrix, RealMatrix origEstimation);
	public void setValidInitialStates(List<Integer> validInitialStates);
	
}
