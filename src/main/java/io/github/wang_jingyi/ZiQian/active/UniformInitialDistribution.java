package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.List;

public class UniformInitialDistribution implements InitialDistGetter {

	private List<Integer> validInitialStates;
	
	public UniformInitialDistribution(List<Integer> validInitialStates) {
		this.validInitialStates = validInitialStates;
	}
	
	@Override
	public List<Double> getInitialDistribution(List<List<Integer>> frequencyMatrix, List<List<Double>> origEstimation) {
		double p = (double) 1 / validInitialStates.size();
		List<Double> id = new ArrayList<Double>();
		for(@SuppressWarnings("unused") int i : validInitialStates){
			id.add(p);
		}
		return id;
	}

	@Override
	public void setValidInitialStates(List<Integer> validInitialStates) {
		this.validInitialStates = validInitialStates;
	}

}
