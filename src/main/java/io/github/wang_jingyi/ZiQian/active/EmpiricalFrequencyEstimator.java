package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.List;



public class EmpiricalFrequencyEstimator implements Estimator {

	@Override
	public MarkovChain estimate(List<List<Integer>> frequencyMatrix) {
		int nodeNumber = frequencyMatrix.size();
		List<List<Double>> esttm = new ArrayList<List<Double>>();
		

		int[] rowsums = new int[nodeNumber];
		for(int i=0; i<nodeNumber; i++){
			int rowsum = 0;
			List<Integer> row = frequencyMatrix.get(i);
			for(int j=0; j<nodeNumber; j++){
				rowsum += row.get(j);
			}
			rowsums[i] = rowsum;
		}

		for(int i=0; i<nodeNumber; i++){
			List<Double> esttmi = new ArrayList<Double>();
			for(int j=0; j<nodeNumber; j++){
				double fre = frequencyMatrix.get(i).get(j);
				double p = fre / rowsums[i];
				esttmi.add(p);
			}
			esttm.add(esttmi);
		}

		return new MarkovChain(esttm);
	}

}
