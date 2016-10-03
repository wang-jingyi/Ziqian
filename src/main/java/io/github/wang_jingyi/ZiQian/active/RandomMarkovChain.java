package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.NumberUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomMarkovChain {

	private int numOfState; // number of states in the dtmc
	private double density; // percentage of edges
	private double[][] transitionMatrix; // transition matrix of the dtmc
	private boolean[][] edges; // edges of the dtmc 
	private List<List<Integer>> edgeIndexes; // indexes of next state
	private List<Integer> initStates;
	public String getRmcName() {
		return rmcName;
	}

	private List<Integer> validStateIndex;

	private boolean rareTrans = false; // whether we set rare transitions
	private double rareTransLevel = 0.0; // the level of rareness

	private int rareTransNum = 0; // number of rare transitions under a rare level
	private Map<TransitionPoint, Double> rts = new HashMap<TransitionPoint, Double>();
	
	private Random rnd;
	private String rmcName;

	public RandomMarkovChain(int n, double density,String rmcName) {
		this.numOfState = n;
		this.density = density;
		this.rmcName = rmcName;
		this.transitionMatrix = new double[numOfState][numOfState];
		this.edges = new boolean[numOfState][numOfState];
		this.edgeIndexes = new ArrayList<>();
		this.validStateIndex = new ArrayList<Integer>();
		this.initStates = new ArrayList<Integer>();
		this.rnd = new Random();

	}



	public RandomMarkovChain(int n, double density,String rmcName, boolean rareTrans, 
			double rareTransLevel, int rareTransNum) {
		this.numOfState = n;
		this.density = density;
		this.rmcName = rmcName;
		this.rareTrans = rareTrans;
		this.rareTransLevel = rareTransLevel;
		this.rareTransNum = rareTransNum;
		this.transitionMatrix = new double[numOfState][numOfState];
		this.edges = new boolean[numOfState][numOfState];
		this.edgeIndexes = new ArrayList<>();
		this.validStateIndex = new ArrayList<Integer>();
		this.initStates = new ArrayList<Integer>();
		this.rnd = new Random();

	}

	public List<Integer> getInitStates() {
		return initStates;
	}

	public void setInitStates(List<Integer> initStates) {
		this.initStates = initStates;
	}
	
	public void generateRMC(){
		if(rareTrans){
			generateRMCWithRareTransitions();
		}
		else{
			generateRMCFree();
		}
	}
	
	
	public void generateRMCWithRareTransitions(){
		generateEdgesWithDensity();
		while(rts.size()<rareTransNum){
			int row = rnd.nextInt(numOfState); // random row
			int s = edgeIndexes.get(row).size();
			int ti = rnd.nextInt(s-1);
			int column = edgeIndexes.get(row).get(ti);
			TransitionPoint rt = new TransitionPoint(row, column);
			if(!rts.containsKey(rt)){
				rts.put(rt,0.0);
			}
		}

		for(int i=0; i<numOfState; i++){
			double tmp = 1;
			double sumcr = 0;
			int edgeNum = edgeIndexes.get(i).size();
			List<Integer> edges = edgeIndexes.get(i);
			for(int j=0; j<edgeNum; j++){
				TransitionPoint currenttp = new TransitionPoint(i, edges.get(j));
				if(j==edgeNum-1){
					transitionMatrix[i][edges.get(edgeNum-1)] = 1 - sumcr;
					sumcr = 1;
				}
				else if(rts.containsKey(currenttp)){
					int mul = 1 + rnd.nextInt(9);
					double rt = mul * rareTransLevel;
					transitionMatrix[i][edges.get(j)]= rt;
					rts.put(currenttp,rt);
					sumcr += rt;
					tmp -= rt;
				}
				else{
					double cr = rnd.nextDouble();
					cr = tmp * cr;
					sumcr += cr;
					tmp -= cr;
					transitionMatrix[i][edges.get(j)] = cr;
				}
			}
		}

	}


	public void generateRMCFree(){
		generateEdgesWithDensity();
		for(int i=0; i<numOfState; i++){
			transitionMatrix[i] = generateRandomDistribution(i);
		}
	}

	public void getValidRMC(){
		List<Integer> queue = new ArrayList<Integer>();

		//		queue.add(0);
		queue.addAll(initStates);

		validStateIndex.add(0);
		while(queue.size()!=0){
			int st = queue.get(0);
			queue.remove(0);
			int counter = -1;
			for(int k=0; k<edges.length; k++){
				boolean hasNext = edges[st][k];
				if(hasNext){
					counter++;
					int next = edgeIndexes.get(st).get(counter);
					if(!validStateIndex.contains(next)){
						validStateIndex.add(next);
						queue.add(next);
					}

				}
			}
		}
		Collections.sort(validStateIndex);

		if(numOfState!=validStateIndex.size()){
			numOfState = validStateIndex.size();
		}

	}

	private void generateEdgesWithDensity(){ // add edges according to density
		for (int outerIndex = 0; outerIndex < edges.length; outerIndex++) {
			List<Integer> ind = new ArrayList<>();
			for (int innerIndex = 0; innerIndex < edges[outerIndex].length; innerIndex++) {
				if (rnd.nextDouble() < density) {
					edges[outerIndex][innerIndex] = true;
					ind.add(innerIndex);
				} else {
					edges[outerIndex][innerIndex] = false;
				}
			}
			edgeIndexes.add(ind);
		}
	}

	private double[] generateRandomDistribution(int curState){
		double[] dis = new double[numOfState];
		List<Integer> edgeInd = edgeIndexes.get(curState);
		int edgeNum = edgeInd.size();

		double tmp = 1;
		double sumcr = 0;
		for(int i=0; i<edgeNum; i++){
			if(i==edgeNum-1){
				dis[edgeInd.get(edgeNum-1)] = 1 - sumcr;
				sumcr = 1;
			}
			else{
				double cr = rnd.nextDouble();
				cr = tmp * cr;
				sumcr += cr;
				tmp -= cr;
				dis[edgeInd.get(i)] = cr;

			}
		}
		return dis;
	}

	// write property list to check
	public void WriteRMCPropertyList(String filePath, int boundedStep) throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
		for(int i=1; i<=numOfState; i++){
			sb.append("P=? [ true U<="+ boundedStep +" s=" + i + " ];\n");
		}
		FileUtil.writeStringToFile(filePath+"/"+rmcName+".pctl", sb.toString());
	}

	public void writeLearnPropertyList(String filePath, int boundedStep) throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
		for(int i=1; i<=numOfState; i++){
			sb.append("P=? [ true U<=" + boundedStep + " \"rmc" + i + "\" ]\n");
		}
		FileUtil.writeStringToFile(filePath+"/"+rmcName+"_learn.pctl", sb.toString());
	}

	//	// write simulation into path
	public void writeSimulationPath(String filePath,int steps) throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
		sb.append("state\n");
		List<Integer> sim = simulateRMC(steps);
		for(int i=0; i<sim.size(); i++){
			sb.append(sim.get(i)+"\n");
		}
		FileUtil.writeStringToFile(filePath+"/"+rmcName+".txt", sb.toString());
	}
	//	

	public List<Integer> simulateRMC(int steps){
		List<Integer> sim = new ArrayList<Integer>();
		int crstate = 0;
		sim.add(0);
		for(int i=0; i<steps; i++){
			crstate = nextState(crstate);
			sim.add(crstate);
		}
		return sim;
	}

	public double[][] getTransitionMatrix() {
		return transitionMatrix;
	}

	public int getNumOfState() {
		return numOfState;
	}

	private int nextState(int currentState){
		double[] dis = transitionMatrix[currentState];
		List<Integer> edgeInd = edgeIndexes.get(currentState);
		double rn = rnd.nextDouble();
		double lb = 0;
		double ub = dis[edgeInd.get(0)];
		for(int i=0; i<edgeInd.size(); i++){
			if(rn>lb && rn<=ub){
				return edgeInd.get(i);
			}
			lb = ub;
			ub += dis[edgeInd.get(i)];
		}
		return -1;
	}

	public String toString(){
		List<String> str = new ArrayList<String>();
		for(int i=0; i<transitionMatrix.length; i++){
			str.add(NumberUtil.doubleArrayToString(transitionMatrix[i]));
		}
		return str.toString();
	}
	
}
