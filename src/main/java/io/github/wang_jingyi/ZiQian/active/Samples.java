package io.github.wang_jingyi.ZiQian.active;

import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class Samples {

	private RealMatrix estimatedTransitionMatrix;
	private RealMatrix frequencyMatrix;
	private Estimator estimator;
	private Sampler sampler;
	private InitialDistGetter idg;
	private int pathLength;

	public Samples(int pathLength, Estimator estimator, Sampler sampler, InitialDistGetter idg){
		this.pathLength = pathLength;
		this.estimator = estimator;
		this.sampler = sampler;
		this.idg = idg;
		int stateNumber = ALConfig.stateNumber;
		if(ALConfig.sparse){
			this.frequencyMatrix = new OpenMapRealMatrix(stateNumber, stateNumber);
			this.estimatedTransitionMatrix = new OpenMapRealMatrix(stateNumber, stateNumber);
		}
		else{
			this.frequencyMatrix = MatrixUtils.createRealMatrix(stateNumber, stateNumber);
			this.estimatedTransitionMatrix = MatrixUtils.createRealMatrix(stateNumber, stateNumber);
		}
	}

	public Samples(int pathLength, RealMatrix currentFrequencyMatrix, Estimator estimator, Sampler sampler,
			InitialDistGetter idg){
		this.pathLength = pathLength;
		this.frequencyMatrix = currentFrequencyMatrix.copy(); // make a copy instead of using it directly
		this.estimator = estimator;
		this.sampler = sampler;
		this.idg = idg;
		this.estimatedTransitionMatrix = estimator.estimate(frequencyMatrix);
	}


	public void newSample(){
		List<Integer> asample = sampler.newSample(
				idg.getInitialDistribution(frequencyMatrix, estimatedTransitionMatrix), ALConfig.pathLength);
		
		System.out.println("sampled new trace: " + asample);

		for(int i=0; i<asample.size()-1; i++){
			int start = asample.get(i);
			int end = asample.get(i+1);
			double cr = frequencyMatrix.getEntry(start, end);
			cr++;
			frequencyMatrix.setEntry(start, end, cr);
		}
		estimatedTransitionMatrix = estimator.estimate(frequencyMatrix);
	}

	public static RealMatrix getFrequencyMatrix(List<List<Integer>> traces, int stateNumber){

		RealMatrix freqMatrix = ALConfig.sparse ? 
				new OpenMapRealMatrix(stateNumber, stateNumber) : MatrixUtils.createRealMatrix(stateNumber, stateNumber);

				for(List<Integer> trace : traces){
					for(int i=0; i<trace.size()-1; i++){
						int start = trace.get(i);
						int end = trace.get(i+1);
						double cr = freqMatrix.getEntry(start, end);
						cr++;
						freqMatrix.setEntry(start, end, cr);
					}
				}
				return freqMatrix;
	}

	public RealMatrix getEstimatedTransitionMatrix() {
		return estimatedTransitionMatrix;
	}

	public RealMatrix getFrequencyMatrix() {
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
