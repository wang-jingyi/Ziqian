package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	
	public static List<Double> calculateTargetStateFreq(RealMatrix frequencyMatrix, List<Integer> targetStates){

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

		List<Double> freqs = new ArrayList<Double>();
		for(int i=0; i<targetStates.size(); i++){
			int j = targetStates.get(i);
			freqs.add(rowsums[j]);
		}
		return freqs;
	}
	
	public static int calculateTargetStateMinFreq(RealMatrix frequencyMatrix, List<Integer> targetStates){

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

		double minFre = Double.MAX_VALUE;
		int min = -1;
		for(int i=0; i<targetStates.size(); i++){
			int j = targetStates.get(i);
			if(rowsums[j]<minFre 
//					&& rowsums[j]!=0
					){
				minFre = rowsums[j];
				min = j;
				continue;
			}
			if(rowsums[j]==minFre){
				min = new Random().nextDouble()>0.5? min : j;
			}
		}
		return min;
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
				continue;
			}
		}
		return minFreq;
	}

	public static double calculateNonZeroMinFreq(RealMatrix frequencyMatrix){

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
			if(rowsums[i]<minFreq && rowsums[i]!=0){
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
		double minNonZeroRowSum = Double.MAX_VALUE;
		for(int i=0; i<rowsums.length; i++){
			if(rowsums[i]<minNonZeroRowSum && rowsums[i]!=0){ // only those states 
				minFreqState = i;
				minNonZeroRowSum = rowsums[i];
				continue;
			}
			if(rowsums[i]==minNonZeroRowSum){
				minFreqState = new Random().nextDouble()>0.5? minFreqState : i;
			}
		}
		System.out.println("minimum row sum: " + minNonZeroRowSum);
		return minFreqState;
	}
}
