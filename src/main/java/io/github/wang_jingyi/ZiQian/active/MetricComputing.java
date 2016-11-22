package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;
import org.uncommons.maths.random.MersenneTwisterRNG;

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
	
	public static double calculateVariance(RealMatrix freqMatrix){
		int nodeNumber = freqMatrix.getRowDimension();
		double[] rowsums = new double[nodeNumber];
		for(int i=0; i<nodeNumber; i++){
			int rowsum = 0;
			double[] row = freqMatrix.getRow(i);
			for(int j=0; j<nodeNumber; j++){
				rowsum += row[j];
			}
			rowsums[i] = rowsum;
		} 
		return getVariance(rowsums);
	}
	
	public static double getMean(double[] data)
    {
        double sum = 0.0;
        int size = data.length;
        for(double a : data)
            sum += a;
        return sum/size;
    }

    public static double getVariance(double[] data)
    {
        double mean = getMean(data);
        double temp = 0;
        int size = data.length;
        for(double a :data)
            temp += (a-mean)*(a-mean);
        return temp/size;
    }

    public static double getStdDev(double[] data)
    {
        return Math.sqrt(getVariance(data));
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
	
	public static HashSet<Integer> oneStepToTargetStates(RealMatrix fm, List<Integer> targetStates){
		HashSet<Integer> oneStepTSs = new HashSet<Integer>();
		for(int ts : targetStates){
			double[] tots = fm.getColumn(ts);
			for(int i=0; i<tots.length; i++){
				if(tots[i]!=0){
					if(!oneStepTSs.contains(i)){
						oneStepTSs.add(i);
					}
				}
			}
		}
		return oneStepTSs;
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
//				continue;
			}
//			if(rowsums[j]==minFre){
//				min = new MersenneTwisterRNG().nextDouble()>0.5? min : j;
//			}
		}
		
		List<Integer> mins = new ArrayList<Integer>();
		for(int i=0; i<targetStates.size(); i++){
			int j = targetStates.get(i);
			if(rowsums[j]==rowsums[min]){
				mins.add(j);
			}
		}
		
		int minsSize = mins.size();
		return mins.get(new MersenneTwisterRNG().nextInt(minsSize));
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
			if(rowsums[i]<minNonZeroRowSum 
					&& rowsums[i]!=0
					){ // only those states 
				minFreqState = i;
				minNonZeroRowSum = rowsums[i];
//				continue;
			}
//			if(rowsums[i]==minNonZeroRowSum){
//				minFreqState = new MersenneTwisterRNG().nextDouble()>0.5? minFreqState : i;
//			}
		}
		
		List<Integer> mins = new ArrayList<Integer>();
		for(int i=0; i<nodeNumber; i++){
			if(rowsums[i]==rowsums[minFreqState]){
				mins.add(i);
			}
		}
		
		int minsSize = mins.size();
		minFreqState = mins.get(new MersenneTwisterRNG().nextInt(minsSize));
		System.out.println("minimum row sum: " + minFreqState);
		return minFreqState;
	}
}
