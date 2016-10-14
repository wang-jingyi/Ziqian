package io.github.wang_jingyi.ZiQian.active;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;


public class GoodTuringEstimator implements Estimator {


	@Override
	public RealMatrix estimate(RealMatrix frequencyMatrix) {

		int nodeNumber = frequencyMatrix.getRowDimension();

		RealMatrix estrm = ALConfig.sparse? new OpenMapRealMatrix(nodeNumber, nodeNumber) : 
			MatrixUtils.createRealMatrix(nodeNumber, nodeNumber);

		double[] rowsums = new double[nodeNumber];
		for(int i=0; i<nodeNumber; i++){
			int rowsum = 0;
			double[] row = frequencyMatrix.getRow(i);
			for(int j=0; j<nodeNumber; j++){
				rowsum += row[j];
			}
			rowsums[i] = rowsum;
		}

		for(int i=0; i<nodeNumber; i++){
			double[] goodTuringEstimation = goodTuringEstimate(frequencyMatrix.getRow(i));
			for(int j=0; j<nodeNumber; j++){
				double p = goodTuringEstimation[j];
//				if(p==0){continue;}
				estrm.setEntry(i, j, p);
			}
		}
		return estrm;
	}

	private double[] goodTuringEstimate(double[] fre){
		double[] probs = new double[fre.length];
		Map<Double,Integer> st = new HashMap<>();
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
