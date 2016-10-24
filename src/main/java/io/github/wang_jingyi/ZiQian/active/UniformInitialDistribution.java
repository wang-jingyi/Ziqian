package io.github.wang_jingyi.ZiQian.active;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

public class UniformInitialDistribution implements InitialDistGetter {

	private List<Integer> validInitialStates;
	
	public UniformInitialDistribution(List<Integer> validInitialStates) {
		this.validInitialStates = validInitialStates;
	}
	
	@Override
	public double[] getInitialDistribution(RealMatrix frequencyMatrix, RealMatrix origEstimation) {
		double p = (double) 1 / validInitialStates.size();
		double[] id = new double[validInitialStates.size()];
		for(int i=0; i<validInitialStates.size(); i++){
			id[i] = p;
		}
		return id;
	}

	@Override
	public void setValidInitialStates(List<Integer> validInitialStates) {
		this.validInitialStates = validInitialStates;
	}

}
