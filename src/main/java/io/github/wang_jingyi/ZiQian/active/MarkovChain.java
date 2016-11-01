package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.uncommons.maths.random.MersenneTwisterRNG;

public class MarkovChain {

	private int nodeNumber;
	private double[] initDistribution; 
	private RealMatrix transitionMatrix;

	public MarkovChain(int nn) {
		this.nodeNumber = nn;
		this.transitionMatrix = MatrixUtils.createRealMatrix(nn, nn); 
	}

	public RealMatrix getTransitionMatrix() {
		return transitionMatrix;
	}
	
	public int getNodeNumber() {
		return nodeNumber;
	}

	public MarkovChain(RealMatrix matrix, double[] initDist) {
		this.nodeNumber = matrix.getRowDimension();
		this.initDistribution = initDist;
		this.transitionMatrix = matrix;
	}
	
	public MarkovChain(RealMatrix matrix, List<Integer> validInitStates,
			List<Double> validInitDist) {
		this.nodeNumber = matrix.getRowDimension();
		this.transitionMatrix = matrix;
		this.initDistribution = new double[nodeNumber];
		for(int i=0; i<validInitStates.size(); i++){ // generate initial distribution
			int s = validInitStates.get(i);
			double d = validInitDist.get(i);
			this.initDistribution[s] = d;
		}
	}

	public List<Integer> simulate(int pathLength){
		List<Integer> path = new ArrayList<Integer>();
		int initState = nextState(initDistribution);
		path.add(initState);
		int currentState = initState;
		for(int i=0; i<pathLength-1; i++){
			int ns = nextState(currentState);
			currentState = ns;
		}
		return path;
	}
	
	public static int nextState(double[] dis){
		double[] accdis = new double[dis.length];
		double acc = 0;
		for(int i=0; i<dis.length; i++){
			acc += dis[i];
			accdis[i] = acc;
		}
		double rn = new MersenneTwisterRNG().nextDouble();
		for(int i=0; i<dis.length; i++){
			if(i==0){
				if(rn<=accdis[i]){
					return i;
				}
			}
			if(i==dis.length-1){
				return i;
			}
			if(rn>accdis[i] && rn<=accdis[i+1]){
				return i+1;
			}
		}
		return -1;
	}

	public int nextState(int currentState){
		double[] dis = transitionMatrix.getRow(currentState);
		return nextState(dis);
	}



}
