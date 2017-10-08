package io.github.wang_jingyi.ZiQian.learn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.utils.MarkovChainUtil;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

public class LearnPST implements LearningDTMC{

	private PSTNode root = new PSTNode();
	private int numOfStates;
	private double epsilon = 0.00000001; // default threshold
	private int maxMemorySize = 1000;
	private double selectionCriterion; // BIC score in this learning algorithm
	private PrismModel PSA = new PrismModel(); // this will be used for generating .pm file later
	

	public LearnPST() {
	}

	public LearnPST(double epsilon){
		this.epsilon = epsilon;
	}

	public LearnPST(double epsilon, int mms){
		this.epsilon = epsilon;
		this.maxMemorySize = mms;
	}

	public void learn(Input data) {
		buildPST(data);
		numOfStates = getLeafsSize(root);
		//		calBIC(data);
	}


	private int getLeafsSize(PSTNode root){ // find leafs of a tree iteratively
		return findLeafInLengthOrder(root).size();
	}

	// translate a learned PST to PrismModel format, only do the translation after the golden search
	public void PrismModelTranslation(Input data, List<Predicate> pset, String modelName) {

		assert data.getObservations().size()==1 : "=== not a single observation ===";
		List<String> observation = data.getObservations().get(0);

//		System.out.println("- translating learned model to a PrismModel...");
		List<PrismState> prismStates = new ArrayList<PrismState>(); // list of prism states
		List<PrismState> initialStates = new ArrayList<PrismState>(); // list of initial states
		numOfStates = 1; //reset the number of state, since we may need to extend the tree

		// extend original tree, leaf by leaf check until the desired property of PST holds
		// this step should start from deepest node and add missing sons if necessary

//		System.out.println("- extend original tree to satisfy pst property");
		boolean pst_property_holds = false;
		HashSet<PSTNode> testedLeaf = new HashSet<>();
		while(!pst_property_holds){
			List<PSTNode> leafs_in_length_order = findLeafInLengthOrder(root);
			assert leafs_in_length_order.size()>0 : "=== no valid model learned, adjust the learning parameter \\epsilon ===";
			int current_leaf_num = leafs_in_length_order.size();
			boolean traverse_over = false;
			for(int j=current_leaf_num-1; j>=0; j--){

				PSTNode current_leaf = leafs_in_length_order.get(j);
				if(testedLeaf.contains(current_leaf)){
					continue;
				}
				testedLeaf.add(current_leaf);
				List<String> leafLabel = current_leaf.getLabel();
				List<String> leafLabelLongestPrefix = StringUtil.getLongestPrefix(leafLabel);
				PSTNode deepestSuffix = findDeepestSuffix(leafLabelLongestPrefix);
				if(!StringUtil.equals(leafLabelLongestPrefix, deepestSuffix.getLabel())){ // no leaf or internal node generate this leaf
					deepestSuffix.setExtending(true); // extend from the deepest suffix node, set extending true
					addCurrentCand(deepestSuffix, leafLabelLongestPrefix, data);
					addMissingSons(root, data);
					break;
				}
				if(j==0){
					traverse_over = true;
				}
			}
			if(traverse_over){
				pst_property_holds = true;
			}
		}

		// build transitions and PrismState
//		System.out.println("- from PST to PFA");
		LinkedList<PSTNode> stack = new LinkedList<PSTNode>();
		stack.add(root);
		while(!stack.isEmpty()){
			PSTNode f = stack.remove();
			if(f.isLeaf()==true){ // original leaf node
				double transSum = 0;
				List<String> sigmas = new ArrayList<String>();
				List<Double> transP = new ArrayList<Double>();
				List<String> transPinString = new ArrayList<>();
				for(String s : data.getAlphabet()){
					double trans = StringUtil.calNextSymbolTransProb(f.getLabel(), s, observation);
					transSum += trans;
					if(trans>0){
						sigmas.add(s);
						transP.add(trans);
						transPinString.add(String.valueOf(trans));
					}
				}
				PrismState ps = new PrismState(numOfStates, f.getLabel());
				ps.setTransitionProb(transP);
				ps.setTranProbInString(transPinString);
				ps.setSigmas(sigmas);
				numOfStates++;
				prismStates.add(ps);
//				System.out.println("- add PrismState :: " + ps);
				if(transSum>0){
					assert transSum==1 : "=== out transition probability not equal to 1 ===";
				}

			}
			if(f.isLeaf()==false){ 
				for(PSTEdge child : f.getPSTEdges()){
					stack.add(child.getDestPSTNode());
				}
			}
		}
		
		RealMatrix transition_matrix = MatrixUtils.createRealMatrix(numOfStates-1,numOfStates-1);
		// set nextStates
		for(PrismState ps : prismStates){
			if(ps.getSigmas().size()==0){ // not observed suffix, regard as a sink state
				continue;
			}
			for(int i=0; i<ps.getSigmas().size(); i++){
				List<String> nextPSLabel = StringUtil.cloneList(ps.getLabel());
				nextPSLabel.add(ps.getSigmas().get(i));
				boolean found = false;
				for(PrismState innps : prismStates){
					if(StringUtil.equals(nextPSLabel, innps.getLabel()) || StringUtil.isSuffix(innps.getLabel(), nextPSLabel)){
						ps.getNextStates().add(innps);
						transition_matrix.setEntry(ps.getId()-1, innps.getId()-1,Double.parseDouble(ps.getSigmas().get(i)));
						found = true;
						break;
					}
				}
				if(!found){
//					System.out.println("undefined transitions: from " + ps + ", next symbol: " + nextPSLabel);
				}
			}
			assert ps.getSigmas().size()==ps.getNextStates().size() : "=== not every state has a emitting state ===";
		}
		
		List<Double> init_distribution = new ArrayList<>();
		double[] steady_state_distribution = MarkovChainUtil.computeSteadyStateDistribution(transition_matrix.getData());
		for(int i=0; i<prismStates.size(); i++){
//			if(steady_state_distribution[i]!=0){
				initialStates.add(prismStates.get(i));
				init_distribution.add(steady_state_distribution[i]);
//			}
		}
		PSA.setPrismStates(prismStates);
		PSA.setInitialStates(initialStates);
		PSA.setInitialDistribution(init_distribution);
		PSA.setPredicates(pset);
		PSA.setNumOfPrismStates(numOfStates-1);
		PSA.setTransitionMatrix(transition_matrix);

	}

	private void buildPST(Input data) {

		List<String> observation = data.getObservations().get(0);
//		System.out.println("- build probabilistic suffix tree");
		
		List<List<String>> candidates = new ArrayList<List<String>>();
		List<Double> candsProb = new ArrayList<Double>();// for each candidate, there is a corresponding occurrence probability 
		for(String sigma : data.getAlphabet()){ // initialize inclusion candidates
			List<String> tmpCand = new ArrayList<String>();
			tmpCand.add(sigma);
			double tmpCandProb = StringUtil.calOccProb(tmpCand,observation);
			if(tmpCandProb>=epsilon){
				candidates.add(tmpCand);
				candsProb.add(tmpCandProb);
			}
		}

		while(candidates.size()!=0){ // the loop body to select candidate to include
			List<String> currentCand = candidates.get(0);
			double currentCandProb = candsProb.get(0);
			PSTNode parentNode = findDeepestSuffix(currentCand); // find the longest suffix node of current candidate
			double temp = 0;
			List<Double> currentCandGeneratingTransProb = StringUtil.calGeneratingTransProb(currentCand, data.getAlphabet(), observation);
			List<Double> parentNodeGeneratingTransProb = StringUtil.calGeneratingTransProb(parentNode.getLabel(), data.getAlphabet(), observation);
			for(int i=0; i<data.getAlphabet().size(); i++){
				double currentCandNextSymbolTransProb = currentCandGeneratingTransProb.get(i);
				double parentNodeNextSymbolTransProb = parentNodeGeneratingTransProb.get(i);
				if(currentCandNextSymbolTransProb==0){
					continue;
				}
				temp += currentCandNextSymbolTransProb * 
						Math.log(currentCandNextSymbolTransProb/parentNodeNextSymbolTransProb);
			}
			double criteria = candsProb.get(0) * temp;

			if(criteria>=epsilon){ // the K-L distance is larger than threshold
				addCurrentCand(parentNode,currentCand, data);
			}

			if(currentCandProb>=epsilon && currentCand.size()<maxMemorySize){ // update candidates (only for limited memory size)
				for(String sigma : data.getAlphabet()){
					List<String> currentCandNext = new ArrayList<String>();
					currentCandNext.add(sigma);// add sigma-s to candidates
					currentCandNext.addAll(currentCand);
					double currentCandNextProb = StringUtil.calOccProb(currentCandNext, observation);
					if(currentCandNextProb>0){
						candidates.add(currentCandNext);
						candsProb.add(currentCandNextProb);
					}
				}
			}
			candidates.remove(0); // remove checked candidates
			candsProb.remove(0);

		}
		addMissingSons(root, data); // traverse and add missing nodes
	}

//	private void calBIC(Input data) { // calculate BIC score
//		List<String> observation = data.getObservations().get(0);
//		selectionCriterion = 0;
//		//		assert numOfStates!=0 : "=== no model learned ===";
//		double logEventLikelihood = calLogEventsLikelihood(observation, data);
//		selectionCriterion = logEventLikelihood - (double)0.5 * numOfStates * (data.getAlphabet().size()-1) 
//				* Math.log(observation.size());
//	}

//	// calculate the event likelihood given a PST, this will be used in calculating BIC
//	private double calLogEventsLikelihood(List<String> events, Input data) {
//		double logEventLikelihood = 0;
//		for(int i=0; i<events.size(); i++){
//			PSTNode deepestSuffix = findDeepestSuffix(events,i); // find the deepest suffix of the first i elements
//			double nextSymProb = findNextSymProb(deepestSuffix,events.get(i), data);
//			assert nextSymProb!=0 : "event probability equals 0.";
//			logEventLikelihood = logEventLikelihood + Math.log(nextSymProb);
//		}
//		return logEventLikelihood;
//	}

//	private double findNextSymProb(PSTNode currentNode, String t, Input data) {
//		List<String> observation = data.getObservations().get(0);
//		double nextSymProb = 1;
//		if(currentNode.getPSTEdges().size()==0){ // if current node is a leaf node, have to calculate transition probability with no edges available
//			nextSymProb = StringUtil.calNextSymbolTransProb(currentNode.getLabel(), t, observation);
//		}
//		else{  // not leaf
//			for(PSTEdge edge : currentNode.getPSTEdges()){
//				if(edge.getLabel().equals(t)){
//					nextSymProb = edge.getTransProb();
//					break;
//				}
//			}
//		}
//		if(nextSymProb==1){
//			System.out.println("bp");
//		}
//		return nextSymProb;
//	}

	// find the deepest suffix node in the tree of the candidate node to include
	private PSTNode findDeepestSuffix(List<String> currentCand) {
		PSTNode deepestSuffix = root;
		int index = currentCand.size()-1; // start from the last element
		while(deepestSuffix.getPSTEdges().size()!=0 && index>=0){
			boolean found = false;
			for(PSTEdge edge : deepestSuffix.getPSTEdges()){
				if(edge.getLabel().equals(currentCand.get(index))){ // find the corresponding edge
					deepestSuffix = edge.getDestPSTNode();
					found = true;
					break;
				}
			}
			if(found==false){
				return deepestSuffix;
			}
			else{
				index--;
			}
		}
		return deepestSuffix;
	}

	// find the deepest suffix node in the tree of the candidate node to include
	//	private PSTNode findDeepestSuffix(List<String> currentCand, PSTNode root) {
	//		PSTNode deepestSuffix = root;
	//		int index = currentCand.size()-1; // start from the last element
	//		while(deepestSuffix.getPSTEdges().size()!=0 && index>=0){
	//			boolean found = false;
	//			for(PSTEdge edge : deepestSuffix.getPSTEdges()){
	//				if(edge.getLabel().equals(currentCand.get(index))){ // find the corresponding edge
	//					deepestSuffix = edge.getDestPSTNode();
	//					found = true;
	//					break;
	//				}
	//			}
	//			if(found==false){
	//				return deepestSuffix;
	//			}
	//			else{
	//				index--;
	//			}
	//		}
	//		return deepestSuffix;
	//	}

//	private PSTNode findDeepestSuffix(List<String> events, int i) {
//		PSTNode currentNode = root;
//		if(i==0){
//			return currentNode;
//		}
//		else{
//			for(int j=i-1; j>=0; j--){ // start from the last element, reversely search until a leaf is reached 
//				for(PSTEdge edge : currentNode.getPSTEdges()){
//					if(edge.getLabel()==events.get(j)){
//						currentNode = edge.getDestPSTNode();
//						break;
//					}
//				}
//			}
//		}
//		return currentNode;
//	}

	private List<PSTNode> findLeafInLengthOrder(PSTNode root){
		List<PSTNode> leafs = new ArrayList<PSTNode>();
		LinkedList<PSTNode> stack = new LinkedList<PSTNode>();
		stack.add(root);
		while(!stack.isEmpty()){
			PSTNode f = stack.remove();
			if(f.isLeaf()==true){
				leafs.add(f);
			}
			for(PSTEdge child : f.getPSTEdges()){
				stack.add(child.getDestPSTNode());
			}
		}
		return leafs;
	}

	// add qualified string s and all its suffixes which are not in tree 
	private void addCurrentCand(PSTNode deepestNode, List<String> currentCand, Input data) {
		List<String> observation = data.getObservations().get(0);
		int ind = currentCand.size() - deepestNode.getLabel().size() - 1;
		if(ind>=0){
			deepestNode.setLeaf(false); // deepest node will no longer be a leaf
		}
		while(ind>=0){
			// add an edge for current node
			double transP = StringUtil.calNextSymbolTransProb(deepestNode.getLabel(), currentCand.get(ind), observation);
			PSTEdge tmpEdge = new PSTEdge(currentCand.get(ind), new PSTNode(), transP); 
			List<String> newNodeLabel = new ArrayList<String>();
			for(String t : deepestNode.getLabel()){
				newNodeLabel.add(t); // clone label from parent node
			}
			newNodeLabel.add(0,currentCand.get(ind)); // add new symbol in label to the start point
			tmpEdge.getDestPSTNode().setLabel(newNodeLabel); // set label for the new node
			deepestNode.getPSTEdges().add(tmpEdge);
			deepestNode = tmpEdge.getDestPSTNode(); // update deepest node
			ind--;
		}
		deepestNode.setLeaf(true); // update leaf label
	}

	private void addMissingSons(PSTNode root, Input data) { // add missing sons of internal nodes recursively
		List<String> observation = data.getObservations().get(0);
		if(root.getPSTEdges().size()==0){ // tree has only a root node or is leaf node already
			return;
		}
		if(root.getPSTEdges().size()!=data.getAlphabet().size()){ // some transitions are not in the tree
			for(int i=0; i<data.getAlphabet().size(); i++){
				if(!isInEdges(data.getAlphabet().get(i),root.getPSTEdges())){
					List<String> newLabel = StringUtil.cloneList(root.getLabel());
					newLabel.add(0, data.getAlphabet().get(i));
					double transP = StringUtil.calNextSymbolTransProb(root.getLabel(), data.getAlphabet().get(i), observation);
					if(transP>0){
						PSTEdge tmpEdge = new PSTEdge(data.getAlphabet().get(i), new PSTNode(), transP);
						root.getPSTEdges().add(tmpEdge);
						tmpEdge.getDestPSTNode().setLabel(newLabel);
						tmpEdge.getDestPSTNode().setLeaf(true); // newly added sons must be leafs
					}
				};
			}
		}
		for(PSTEdge edge : root.getPSTEdges()){
			addMissingSons(edge.getDestPSTNode(), data);
		}
	}

	private boolean isInEdges(String t, List<PSTEdge> pstEdges) {
		for(PSTEdge edge : pstEdges){
			if(edge.getLabel().equals(t)){
				return true;
			}
		}
		return false;
	}

	public PrismModel getPrismModel() {
		return PSA;
	}
	
	public PSTNode getRoot() {
		return root;
	}

	public int getNumOfStates() {
		return numOfStates;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public double getSelectionCriterion() {
		return selectionCriterion;
	}

	public void setSelectionCriterion(double selectionCriterion) {
		this.selectionCriterion = selectionCriterion;
	}
}
