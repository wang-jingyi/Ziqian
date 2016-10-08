package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.run.GlobalVars;
import io.github.wang_jingyi.ZiQian.utils.NumberUtil;

import java.util.ArrayList;
import java.util.List;

public class Samples {

	private int pathLength;
	private double[][] estimatedTransitionMatrix;
	private int[][] frequencyMatrix;
	private Estimator estimator;
	private Sampler sampler;
	private InitialDistGetter idg;
	

	public Samples(int nodeNumber, int pathLength, Estimator estimator, Sampler sampler, InitialDistGetter idg){
		this.pathLength = pathLength;
		this.estimator = estimator;
		this.sampler = sampler;
		this.idg = idg;
		this.frequencyMatrix = new int[nodeNumber][nodeNumber];
		this.estimatedTransitionMatrix = new double[nodeNumber][nodeNumber];
		for(int i=0; i<nodeNumber; i++){ // if no estimation is given, treat as uniform
			for(int j=0; j<nodeNumber; j++){
				this.frequencyMatrix[i][j] = 1;
				this.estimatedTransitionMatrix[i][j] = (double) 1 / nodeNumber; 
			}
		}
	}
	
	public Samples(int pathLength, int[][] currentFrequencyMatrix, Estimator estimator, Sampler sampler,
			InitialDistGetter idg){
		this.frequencyMatrix = currentFrequencyMatrix;
		this.pathLength = pathLength;
		this.estimator = estimator;
		this.sampler = sampler;
		this.idg = idg;
		this.frequencyMatrix = currentFrequencyMatrix;
		this.estimatedTransitionMatrix = estimator.estimate(currentFrequencyMatrix).getTransitionMatrix();
	}
	

	public void newSample(){
		List<Integer> asample = sampler.newSample(
				idg.getInitialDistribution(frequencyMatrix, estimatedTransitionMatrix), pathLength);
		
		if(GlobalVars.newStateNumber!=0){ // update matrixes when there are new states
			System.out.println("adding " + GlobalVars.newStateNumber + " new states and updating matrix...");
			int stateNumber = frequencyMatrix.length + GlobalVars.newStateNumber;
			int[][] newfrequencyMatrix = new int[stateNumber][stateNumber];
			System.arraycopy(frequencyMatrix, 0, newfrequencyMatrix, 0, frequencyMatrix.length);
			frequencyMatrix = newfrequencyMatrix;
			estimatedTransitionMatrix = new double[stateNumber][stateNumber];
			
			// update valid initial states
			List<Integer> newValidInitialStates = new ArrayList<Integer>();
			for(int i=0; i<stateNumber; i++){
				newValidInitialStates.add(i);
			}
			idg.setValidInitialStates(newValidInitialStates);
			GlobalVars.newStateNumber = 0 ;
		}
		for(int i=0; i<asample.size()-1; i++){
			int start = asample.get(i);
			int end = asample.get(i+1);
			frequencyMatrix[start][end] ++;
		}
		bayesianEstimation();
	}



	private void bayesianEstimation(){
		MarkovChain mc = estimator.estimate(frequencyMatrix);
		estimatedTransitionMatrix = mc.getTransitionMatrix();
	}

	public int getPathLength() {
		return pathLength;
	}

	public double[][] getEstimatedTransitionMatrix() {
		return estimatedTransitionMatrix;
	}

	public int[][] getFrequencyMatrix() {
		return frequencyMatrix;
	}
	
	@Override
	public String toString() {
		return "Samples [pathLength=" + pathLength
				+ ", estimatedTransitionMatrix="
				+ NumberUtil.twoDArrayToString(estimatedTransitionMatrix)
				+ ", frequencyMatrix=" + NumberUtil.twoDArrayToString(frequencyMatrix) + "]";
	}
}
