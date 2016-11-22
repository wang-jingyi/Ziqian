package io.github.wang_jingyi.ZiQian.active;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

public class OriginalInitialDistribution implements InitialDistGetter {

	private List<Integer> validInitialStates;
	private List<Double> initDist;
	
	public OriginalInitialDistribution(List<Integer> validInitialStates, List<Double> initDist) {
		this.validInitialStates = validInitialStates;
		this.initDist = initDist;
	}
	
	@Override
	public double[] getInitialDistribution(RealMatrix frequencyMatrix, RealMatrix origEstimation) {
//		double p = (double) 1 / validInitialStates.size();
		double[] id = new double[validInitialStates.size()];
		for(int i=0; i<validInitialStates.size(); i++){
			int ind = validInitialStates.get(i);
			id[ind] = initDist.get(i);
		}
		return id;
	}

	@Override
	public void setValidInitialStates(List<Integer> validInitialStates) {
		this.validInitialStates = validInitialStates;
	}

}
