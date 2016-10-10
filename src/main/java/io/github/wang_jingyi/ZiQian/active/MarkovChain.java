package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.utils.ListUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MarkovChain {

	private int nodeNumber;
	private List<List<Double>> transitionMatrix;

	public MarkovChain(int nn) {
		this.nodeNumber = nn;
		for(int i=0; i<nodeNumber; i++){
			List<Double> tmi = new ArrayList<Double>();
			for(int j=0; j<nodeNumber; j++){
				tmi.add(0.0);
			}
			transitionMatrix.add(tmi);
		}
	}

	public List<List<Double>> getTransitionMatrix() {
		return transitionMatrix;
	}
	
	public int getNodeNumber() {
		return nodeNumber;
	}

	public MarkovChain(List<List<Double>> matrix) {
		this.nodeNumber = matrix.size();
		this.transitionMatrix = matrix;
	}
	
	public static int nextState(List<Double> disl){
		double[] dis = ListUtil.listToArray(disl);
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
		List<Double> dis = transitionMatrix.get(currentState);
		return nextState(dis);
	}



}
