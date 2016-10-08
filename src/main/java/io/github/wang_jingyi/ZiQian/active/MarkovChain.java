package io.github.wang_jingyi.ZiQian.active;

import java.util.Random;

public class MarkovChain {

	private int nodeNumber;
	private double[][] transitionMatrix;

	public int getNodeNumber() {
		return nodeNumber;
	}

	public double[][] getTransitionMatrix() {
		return transitionMatrix;
	}

	public MarkovChain(int nn) {
		this.nodeNumber = nn;
		this.transitionMatrix = new double[nn][nn];
	}

	public MarkovChain(double[][] matrix) {
		this.nodeNumber = matrix.length;
		this.transitionMatrix = matrix;
	}
	
	public static int nextState(double[] dis){
		double[] accdis = new double[dis.length];
		double acc = 0;
		for(int i=0; i<dis.length; i++){
			acc += dis[i];
			accdis[i] = acc;
		}
		double rn = new Random().nextDouble();
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
		double[] dis = transitionMatrix[currentState];
		return nextState(dis);
	}



}
