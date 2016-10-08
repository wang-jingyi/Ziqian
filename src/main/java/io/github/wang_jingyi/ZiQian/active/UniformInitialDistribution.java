package io.github.wang_jingyi.ZiQian.active;

import java.util.List;

public class UniformInitialDistribution implements InitialDistGetter {

	private int stateNumber;
	private List<Integer> validInitialStates;
	
	public UniformInitialDistribution(int sn, List<Integer> validInitialStates) {
		this.stateNumber = sn;
		this.validInitialStates = validInitialStates;
	}
	
	@Override
	public double[] getInitialDistribution(int[][] frequencyMatrix, double[][] origEstimation) {
		double p = (double) 1 / validInitialStates.size();
		double[] id = new double[stateNumber];
		for(int i : validInitialStates){
			id[i] = p;
		}
		return id;
	}

	@Override
	public void setValidInitialStates(List<Integer> validInitialStates) {
		this.validInitialStates = validInitialStates;
	}

}
