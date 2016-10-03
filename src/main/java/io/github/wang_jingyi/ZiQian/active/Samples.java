package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.utils.NumberUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class Samples {

	//	public static final Samples sample = new Samples();
	private MarkovChain mc;
	private int pathLength;
	private int nodeNumber;
	private RealMatrix estimatedTransitionMatrix;
	private RealMatrix frequencyMatrix;
	private double mse;
	private Double minFreq;
	private String estimation; // which estimator to use


	public Samples(MarkovChain mc, int pathLength, String est){
		this.mc = mc;
		this.nodeNumber = mc.getNodeNumber();
		this.pathLength = pathLength;
		this.estimation = est;
		this.estimatedTransitionMatrix = MatrixUtils.createRealMatrix(nodeNumber, nodeNumber);
		this.frequencyMatrix = MatrixUtils.createRealMatrix(nodeNumber, nodeNumber);
	}

	public int calculateSampleStartPoint(double[] optimizedInitDistribution){
		return MarkovChain.nextState(optimizedInitDistribution);
	}

	public void newSample(int startIndex){
		List<Integer> asample = sample(startIndex);
		for(int i=0; i<asample.size()-1; i++){
			int start = asample.get(i);
			int end = asample.get(i+1);
			double currentEntry = frequencyMatrix.getEntry(start, end);
			currentEntry ++;
			frequencyMatrix.setEntry(start, end, currentEntry);
		}
		bayesianEstimation();
		calculateMSE();
		calMinFreq();
	}


	public Double getMinFreq() {
		return minFreq;
	}

	public void setFrequencyMatrix(RealMatrix frequencyMatrix) {
		this.frequencyMatrix = frequencyMatrix;
	}

	public double getMSE() {
		return mse;
	}


	private int[] extractLeastTrans(double[][] matrix){
		double min = Double.MAX_VALUE;
		int[] ind = new int[2]; 
		for(int i=0; i<matrix.length; i++){
			for(int j=0; j<matrix[0].length; j++){
				if(matrix[i][j]<min && matrix[i][j]!=0){
					ind[0] = i;
					ind[1] = j;
				}
			}
		}
		return ind;
	}

	private void calMinFreq(){
		double[] rowsums = NumberUtil.getRowSums(frequencyMatrix.getData()); // 
		minFreq = Double.MAX_VALUE;
		for(int i=0; i<rowsums.length; i++){
			if(rowsums[i]<minFreq){
				minFreq = rowsums[i];
			}
		}
	}

	private void calculateMSE() {

		RealMatrix diffMatrix = estimatedTransitionMatrix.subtract(mc.getTransitionMatrix());
		int[] ind = extractLeastTrans(mc.getTransitionMatrix().getData());
		System.out.println("minimum transition: " + mc.getTransitionMatrix().getEntry(ind[0], ind[1]));
		double[][] diff= diffMatrix.getData();
		for(int i=0; i<nodeNumber; i++){
			for(int j=0; j<nodeNumber; j++){
				if(i==ind[0] && j==ind[1]){
					mse = Math.abs(diff[i][j]);
					break;
				}
			}
		}
		mse = mse
				/nodeNumber/nodeNumber;



	}

	private void bayesianEstimation(){
		double[] rowsums = new double[nodeNumber];
		for(int i=0; i<nodeNumber; i++){
			double rowsum = 0;
			double[] row = frequencyMatrix.getRow(i);
			for(int j=0; j<nodeNumber; j++){
				rowsum += row[j];
			}
			rowsums[i] = rowsum;
		}

		if(estimation.equalsIgnoreCase("gt")){
			for(int i=0; i<nodeNumber; i++){
				double[] goodTuringEstimation = goodTuringEstimate(frequencyMatrix.getRow(i));
				for(int j=0; j<nodeNumber; j++){
					double p = goodTuringEstimation[j];
					estimatedTransitionMatrix.setEntry(i, j, p);
				}
			}
		}

		else{
			for(int i=0; i<nodeNumber; i++){
				for(int j=0; j<nodeNumber; j++){
					double fre = frequencyMatrix.getEntry(i, j);
					double p = 1;
					// estimate p using different estimators
					if(estimation.equalsIgnoreCase("lp")){ 
						p = (1+fre)/(nodeNumber+rowsums[i]);
					}

					else{ // default estimator is empirical frequency
						p = fre / rowsums[i];
					}
					estimatedTransitionMatrix.setEntry(i, j, p);
				}
			}
		}

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

	private List<Integer> sample(int startIndex) {
		return mc.samplePath(pathLength, startIndex);
	}

	public int getPathLength() {
		return pathLength;
	}

	public int getMinStartVertex(){
		double[] rowsums = new double[nodeNumber];
		for(int i=0; i<nodeNumber; i++){
			double rowsum = 0;
			double[] row = frequencyMatrix.getRow(i);
			for(int j=0; j<nodeNumber; j++){
				rowsum += row[j];
			}
			rowsums[i] = rowsum;
		}

		int minstart = 0;
		double minsum = rowsums[0];
		for(int i=1; i<nodeNumber; i++){
			if(rowsums[i]<minsum){
				minstart = i;
				minsum = rowsums[i];
			}
		}
		return minstart;
	}

	public RealMatrix getEstimatedTransitionMatrix() {
		return estimatedTransitionMatrix;
	}

	public RealMatrix getFrequencyMatrix() {
		return frequencyMatrix;
	}
}
