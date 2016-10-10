package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.run.GlobalVars;

import java.util.ArrayList;
import java.util.List;

public class Samples {

	private int pathLength;
	private List<List<Double>> estimatedTransitionMatrix;
	private List<List<Integer>> frequencyMatrix;
	private Estimator estimator;
	private Sampler sampler;
	private InitialDistGetter idg;
	

	public Samples(int nodeNumber, int pathLength, Estimator estimator, Sampler sampler, InitialDistGetter idg){
		this.pathLength = pathLength;
		this.estimator = estimator;
		this.sampler = sampler;
		this.idg = idg;
		for(int i=0; i<nodeNumber; i++){ // if no estimation is given, treat as uniform
			List<Integer> fmi = new ArrayList<Integer>();
			List<Double> eti = new ArrayList<Double>();
			for(int j=0; j<nodeNumber; j++){
				fmi.add(0);
				eti.add((double) 1 / nodeNumber);
			}
			frequencyMatrix.add(fmi);
			estimatedTransitionMatrix.add(eti);
		}
	}
	
	public Samples(int pathLength, List<List<Integer>> currentFrequencyMatrix, Estimator estimator, Sampler sampler,
			InitialDistGetter idg){
		this.frequencyMatrix = currentFrequencyMatrix;
		this.pathLength = pathLength;
		this.estimator = estimator;
		this.sampler = sampler;
		this.idg = idg;
		this.frequencyMatrix = currentFrequencyMatrix;
		this.estimatedTransitionMatrix = estimator.estimate(frequencyMatrix).getTransitionMatrix();
	}
	

	public void newSample(){
		List<Integer> asample = sampler.newSample(
				idg.getInitialDistribution(frequencyMatrix, estimatedTransitionMatrix), pathLength);
		
		if(GlobalVars.newStateNumber!=0){ // update matrixes when there are new states
			
			System.out.println("adding " + GlobalVars.newStateNumber + " new states and updating matrix...");
			
			int stateNumber = frequencyMatrix.size() + GlobalVars.newStateNumber;
			
			for(List<Integer> fmi : frequencyMatrix){ // update each list
				for(int i=0; i<GlobalVars.newStateNumber; i++){
					fmi.add(0); 
				}
			}
			for(int i=0; i<GlobalVars.newStateNumber; i++){ // update row for new state
				List<Integer> newfmi = new ArrayList<Integer>();
				for(int j=0; j<stateNumber; j++){
					newfmi.add(0); 
				}
				frequencyMatrix.add(newfmi);
			}
			
			
			for(List<Double> estmi : estimatedTransitionMatrix){
				for(int i=0; i<GlobalVars.newStateNumber; i++){
					estmi.add(0.0);
				}
			}
			for(int i=0; i<GlobalVars.newStateNumber; i++){ // update row for new state
				List<Double> newtmi = new ArrayList<Double>();
				for(int j=0; j<stateNumber; j++){
					newtmi.add(0.0); 
				}
				estimatedTransitionMatrix.add(newtmi);
			}
			
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
			frequencyMatrix.get(start).get(end);
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

	
	public List<List<Double>> getEstimatedTransitionMatrix() {
		return estimatedTransitionMatrix;
	}

	public List<List<Integer>> getFrequencyMatrix() {
		return frequencyMatrix;
	}

	@Override
	public String toString() {
		return "Samples [pathLength=" + pathLength
				+ ", estimatedTransitionMatrix="
				+ estimatedTransitionMatrix
				+ ", frequencyMatrix=" + frequencyMatrix + "]";
	}
}
