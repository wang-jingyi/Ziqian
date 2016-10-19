package io.github.wang_jingyi.ZiQian.active;

import org.apache.commons.math3.linear.RealMatrix;

public class MetricComputing {

	public static double calculateMSE(RealMatrix transitionMatrix, RealMatrix estTransitionMatrix){
		int nodeNumber = transitionMatrix.getRowDimension();
		RealMatrix diffMatrix = estTransitionMatrix.subtract(transitionMatrix);
		double[][] diff= diffMatrix.getData();
		double mse = 0.0;
		for(int i=0; i<nodeNumber; i++){
			for(int j=0; j<nodeNumber; j++){
				mse += Math.abs(diff[i][j]);
			}
		}
		mse = mse/nodeNumber/nodeNumber;
		return mse;
	}

	public static double calculateMinFreq(RealMatrix frequencyMatrix){
		
		int nodeNumber = frequencyMatrix.getRowDimension();
		double[] rowsums = new double[nodeNumber];
		for(int i=0; i<nodeNumber; i++){
			int rowsum = 0;
			double[] row = frequencyMatrix.getRow(i);
			for(int j=0; j<nodeNumber; j++){
				rowsum += row[j];
			}
			rowsums[i] = rowsum;
		} 
		
		double minFreq = Integer.MAX_VALUE;
		for(int i=0; i<rowsums.length; i++){
			if(rowsums[i]<minFreq){
				minFreq = rowsums[i];
			}
		}
		return minFreq;
	}

	public static int calculateMinFreqState(RealMatrix frequencyMatrix){
		
		int nodeNumber = frequencyMatrix.getRowDimension();
		double[] rowsums = new double[nodeNumber];
		for(int i=0; i<nodeNumber; i++){
			int rowsum = 0;
			double[] row = frequencyMatrix.getRow(i);
			for(int j=0; j<nodeNumber; j++){
				rowsum += row[j];
			}
			rowsums[i] = rowsum;
		} 
		int minFreqState = 0;
		for(int i=1; i<rowsums.length; i++){
			if(rowsums[i]<rowsums[minFreqState]){ // only those states 
				minFreqState = i;
			}
		}
		return minFreqState;
	}
}
