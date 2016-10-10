package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GoodTuringEstimator implements Estimator {
	

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
			double[] goodTuringEstimation = goodTuringEstimate(frequencyMatrix.get(i));
			List<Double> esttmi = new ArrayList<Double>(); 
			for(int j=0; j<nodeNumber; j++){
				double p = goodTuringEstimation[j];
				esttmi.add(p);
			}
			esttm.add(esttmi);
		}
		return new MarkovChain(esttm);
	}
	
	private double[] goodTuringEstimate(List<Integer> fre){
		double[] probs = new double[fre.size()];
		Map<Integer,Integer> st = new HashMap<>();
		double rowsum = 0;
		for(int i=0; i<fre.size(); i++){
			rowsum = rowsum + fre.get(i);
			if(!st.containsKey(fre.get(i))){
				st.put(fre.get(i), 1);
			}
			else{
				int cn = st.get(fre.get(i));
				cn++;
				st.put(fre.get(i), cn);
			}
		}
		for(int i=0; i<probs.length; i++){
			int t = st.get(fre.get(i));
			int tplus = 0;
			if(st.containsKey(fre.get(i)+1)){
				tplus = st.get(fre.get(i)+1);
			}
			if(rowsum==0 || t==0){
				probs[i] = 0;
			}
			else{
				probs[i] = (double)(fre.get(i)+1) * tplus /rowsum / t;
			}

		}
		return probs;

	}

}
