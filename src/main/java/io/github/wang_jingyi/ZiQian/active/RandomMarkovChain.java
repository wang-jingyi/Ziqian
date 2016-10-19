package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.utils.FileUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class RandomMarkovChain {

	private int numOfState; // number of states in the dtmc
	private double density; // percentage of edges
	private RealMatrix transitionMatrix; // transition matrix of the dtmc
	private boolean[][] edges; // edges of the dtmc 
	private List<List<Integer>> edgeIndexes; // indexes of next state
	private List<Integer> initStates;
	

	private List<Integer> validStateIndex;

	private boolean rareTrans = false; // whether we set rare transitions
	private boolean rareState = false; // whether exists a rare state
	private double rareTransLevel = 0.0; // the level of rareness

	private int rareTransNum = 0; // number of rare transitions under a rare level
	private Map<TransitionPoint, Double> rts = new HashMap<TransitionPoint, Double>();
	
	private Random rnd;
	private String rmcName;

	public RandomMarkovChain(int n, double density,String rmcName) {
		this.numOfState = n;
		this.density = density;
		this.rmcName = rmcName;
		this.transitionMatrix = MatrixUtils.createRealMatrix(n, n);
		this.edges = new boolean[numOfState][numOfState];
		this.edgeIndexes = new ArrayList<>();
		this.validStateIndex = new ArrayList<Integer>();
		this.initStates = new ArrayList<Integer>();
		this.rnd = new Random();

	}
	
	public RandomMarkovChain(int n, double density,String rmcName, boolean rareState, 
			double rareTransLevel) {
		this.numOfState = n;
		this.density = density;
		this.rmcName = rmcName;
		this.rareState = rareState;
		this.rareTransLevel = rareTransLevel;
		this.transitionMatrix = MatrixUtils.createRealMatrix(n, n);
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
		this.transitionMatrix = MatrixUtils.createRealMatrix(n, n);
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
		else if(rareState){
			generateRMCWithRareStates();
//			System.out.println("created");
		}
		else{
			generateRMCFree();
		}
	}
	
	private void generateRMCWithRareStates(){
		
		int stateToRareState = rnd.nextInt(numOfState-1); // the only state that has the rare transition to the rare state
		
		for (int outerIndex = 0; outerIndex < edges.length-1; outerIndex++) { // rows except last row
			List<Integer> ind = new ArrayList<>();
			if(outerIndex!=stateToRareState){
				for (int innerIndex = 0; innerIndex < edges[outerIndex].length-1; innerIndex++) {
					if (rnd.nextDouble() < density) {
						edges[outerIndex][innerIndex] = true;
						ind.add(innerIndex);
					} else {
						edges[outerIndex][innerIndex] = false;
					}
				}
				edges[outerIndex][edges[outerIndex].length-1] = false;
			}
			else{
				for (int innerIndex = 0; innerIndex < edges[outerIndex].length-1; innerIndex++) {
					if (rnd.nextDouble() < density) {
						edges[outerIndex][innerIndex] = true;
						ind.add(innerIndex);
					} else {
						edges[outerIndex][innerIndex] = false;
					}
				}
				ind.add(edges[outerIndex].length-1);
				edges[outerIndex][edges[outerIndex].length-1] = true;
			}
			edgeIndexes.add(ind);
		}
		
		// last row
		int lastRowIndex = edges.length-1;
		edges[lastRowIndex][edges.length-1] = true;
		List<Integer> lastRow = new ArrayList<Integer>();
		lastRow.add(edges.length-1);
		edgeIndexes.add(lastRow);
		
		for(int i=0; i<numOfState; i++){
			double tmp = 1;
			double sumcr = 0;
			int edgeNum = edgeIndexes.get(i).size();
			List<Integer> edges = edgeIndexes.get(i);
			
			if(i==stateToRareState){
				for(int j=edgeNum-1; j>=0; j--){
					if(j==edgeNum-1){
						double cr = (1 + rnd.nextInt(9)) * rareTransLevel;
						transitionMatrix.setEntry(i, edges.get(j), cr);
						sumcr += cr;
						tmp -= cr;
					}
					else if(j==0){
						transitionMatrix.setEntry(i, edges.get(j), 1-sumcr);
						sumcr = 1;
					}
					else{
						double cr = rnd.nextDouble();
						cr = tmp * cr;
						sumcr += cr;
						tmp -= cr;
						transitionMatrix.setEntry(i, edges.get(j), cr);
					}
				}
			}
			
			else if(i==this.edges.length-1){
				transitionMatrix.setEntry(i, edges.get(0), 1);
			}
			
			else{
				transitionMatrix.setRow(i, generateRandomDistribution(i));
			}
		}

	}
	
	
	private void generateRMCWithRareTransitions(){
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
					transitionMatrix.setEntry(i, edges.get(edgeNum-1), 1 - sumcr);
					sumcr = 1;
				}
				else if(rts.containsKey(currenttp)){
					int mul = 1 + rnd.nextInt(9);
					double rt = mul * rareTransLevel;
					transitionMatrix.setEntry(i, edges.get(j), rt);
					rts.put(currenttp,rt);
					sumcr += rt;
					tmp -= rt;
				}
				else{
					double cr = rnd.nextDouble();
					cr = tmp * cr;
					sumcr += cr;
					tmp -= cr;
					transitionMatrix.setEntry(i, edges.get(j), cr);
				}
			}
		}

	}


	private void generateRMCFree(){
		generateEdgesWithDensity();
		for(int i=0; i<numOfState; i++){
			transitionMatrix.setRow(i, generateRandomDistribution(i));
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

	public RealMatrix getTransitionMatrix() {
		return transitionMatrix;
	}

	public int getNumOfState() {
		return numOfState;
	}
	
	public String getRmcName() {
		return rmcName;
	}

	private int nextState(int currentState){
		double[] dis = transitionMatrix.getRow(currentState);
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



	@Override
	public String toString() {
		return "RandomMarkovChain [numOfState=" + numOfState + ", density="
				+ density + ", transitionMatrix=" + transitionMatrix
				+ ", rmcName=" + rmcName + "]";
	}
	
	
	
}
