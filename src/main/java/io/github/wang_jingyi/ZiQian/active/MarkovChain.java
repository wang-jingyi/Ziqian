package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class MarkovChain {

	private int nodeNumber;
	private RealMatrix transitionMatrix;
	private Random rnd = new Random();

	public int getNodeNumber() {
		return nodeNumber;
	}

	public RealMatrix getTransitionMatrix() {
		return transitionMatrix;
	}

	public MarkovChain(int nn) {
		this.nodeNumber = nn;
		this.transitionMatrix = MatrixUtils.createRealMatrix(nodeNumber, nodeNumber);
	}

	public MarkovChain(double[][] matrix) {
		this.nodeNumber = matrix.length;
		this.transitionMatrix = MatrixUtils.createRealMatrix(matrix);
	}

	public void generateRandomMarkovChain(int nodeNumber){
		double[][] transm = new double[nodeNumber][nodeNumber];
		for(int i=0; i<nodeNumber; i++){
			transm[i] = generateRandomDistribution();
		}
		transitionMatrix = MatrixUtils.createRealMatrix(transm);
	}

	private double[] generateRandomDistribution(){
		double[] dis = new double[nodeNumber];

		double tmp = 1;
		double sumcr = 0;
		for(int i=0; i<nodeNumber; i++){
			if(i==nodeNumber-1){
				dis[i] = 1 - sumcr;
				sumcr = 1;
			}
			else{
				double cr = rnd.nextDouble();
				cr = tmp * cr;
				sumcr += cr;
				tmp -= cr;
				dis[i] = cr;

			}
		}
		return dis;
	}


	public List<Integer> samplePath(int pathLength, int startIndex){
		List<Integer> path = new ArrayList<Integer>();
		int crstate = startIndex;
		path.add(startIndex);
		for(int i=0; i<pathLength; i++){
			crstate = nextState(crstate);
			path.add(crstate);
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

	private int nextState(int currentState){
		double[] dis = transitionMatrix.getRow(currentState);
		return nextState(dis);
	}



}
