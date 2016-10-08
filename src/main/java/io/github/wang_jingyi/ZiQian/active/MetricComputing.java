package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.utils.NumberUtil;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class MetricComputing {

	public static double calculateMSE(double[][] transitionMatrix, double[][] estTransitionMatrix){
		int nodeNumber = transitionMatrix.length;
		RealMatrix diffMatrix = MatrixUtils.createRealMatrix(estTransitionMatrix).subtract(
				MatrixUtils.createRealMatrix(transitionMatrix));
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

	public static int calculateMinFreq(int[][] frequencyMatrix){
		int[] rowsums = NumberUtil.getRowSums(frequencyMatrix);// 
		int minFreq = Integer.MAX_VALUE;
		for(int i=0; i<rowsums.length; i++){
			if(rowsums[i]<minFreq){
				minFreq = rowsums[i];
			}
		}
		return minFreq;
	}
	
	public static int calculateMinFreqState(int[][] frequencyMatrix){
		int[] rowsums = NumberUtil.getRowSums(frequencyMatrix);// 
		int minFreqState = 0;
		for(int i=1; i<rowsums.length; i++){
			if(rowsums[i]<rowsums[minFreqState]){
				minFreqState = i;
			}
		}
		return minFreqState;
	}
}
