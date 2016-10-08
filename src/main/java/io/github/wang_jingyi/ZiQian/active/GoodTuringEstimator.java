package io.github.wang_jingyi.ZiQian.active;

import java.util.HashMap;
import java.util.Map;


public class GoodTuringEstimator implements Estimator {
	

	@Override
	public MarkovChain estimate(int[][] frequencyMatrix) {
		int nodeNumber = frequencyMatrix.length;
		double[][] estimatedTransitionMatrix = new double[nodeNumber][nodeNumber];
		
		int[] rowsums = new int[nodeNumber];
		for(int i=0; i<nodeNumber; i++){
			int rowsum = 0;
			int[] row = frequencyMatrix[i];
			for(int j=0; j<nodeNumber; j++){
				rowsum += row[j];
			}
			rowsums[i] = rowsum;
		}
		
		for(int i=0; i<nodeNumber; i++){
			double[] goodTuringEstimation = goodTuringEstimate(frequencyMatrix[i]);
			for(int j=0; j<nodeNumber; j++){
				double p = goodTuringEstimation[j];
				estimatedTransitionMatrix[i][j] = p;
			}
		}
		return new MarkovChain(estimatedTransitionMatrix);
	}
	
	private double[] goodTuringEstimate(int[] fre){
		double[] probs = new double[fre.length];
		Map<Integer,Integer> st = new HashMap<>();
		double rowsum = 0;
		for(int i=0; i<fre.length; i++){
			rowsum = rowsum + fre[i];
			if(!st.containsKey(fre[i])){
				st.put(fre[i], 1);
			}
			else{
				int cn = st.get(fre[i]);
				cn++;
				st.put(fre[i], cn);
			}
		}
		for(int i=0; i<probs.length; i++){
			int t = st.get(fre[i]);
			int tplus = 0;
			if(st.containsKey(fre[i]+1)){
				tplus = st.get(fre[i]+1);
			}
			if(rowsum==0 || t==0){
				probs[i] = 0;
			}
			else{
				probs[i] = (double)(fre[i]+1) * tplus /rowsum / t;
			}

		}
		return probs;

	}

}
