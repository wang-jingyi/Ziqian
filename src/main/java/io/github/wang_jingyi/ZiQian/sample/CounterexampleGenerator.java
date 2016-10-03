package io.github.wang_jingyi.ZiQian.sample;

import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.utils.IntegerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;


public class CounterexampleGenerator {

	private PrismModel prismModel;
	private int boundedStep; // bounded step should be -1 if unbounded
	private double probabilityBound; // probability threshold of path set
	private boolean containLoops; // if the counterexample paths contain loops


	public CounterexampleGenerator(PrismModel pm, int bs, double pb) {
		this.prismModel = pm;
		this.boundedStep = bs;
		this.probabilityBound = pb;
	}

	public List<CounterexamplePath> generateCounterexamples(){
		DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph = PRISMModelToGraph();
		int endingVertex = findAbsorbingState(graph);

		//		List<Integer> stateWithLoops = new ArrayList<>();
		//		List<Double> loopWeights = new ArrayList<>();
		//		for(int vertex: graph.vertexSet()){ 			// find loops with weights in ascending order 
		//			for(DefaultWeightedEdge edge : graph.outgoingEdgesOf(vertex)){
		//				double weight = graph.getEdgeWeight(edge);
		//				if(graph.getEdgeTarget(edge)==vertex && weight!=0){ // self loop with probability 1 is not considered
		//					if(stateWithLoops.size()==0){
		//						loopWeights.add(weight);
		//						stateWithLoops.add(vertex);
		//						continue;
		//					}
		//					
		//					for(int i=0;i<loopWeights.size();i++){
		//						if(i==stateWithLoops.size()-1){
		//							loopWeights.add(weight);
		//							stateWithLoops.add(vertex);
		//						}
		//						else if(weight<loopWeights.get(i)){
		//							loopWeights.set(i, weight);
		//							stateWithLoops.set(i, vertex);
		//						}
		//					}
		//				}
		//			}
		//		}
		//		System.out.println("state with loops: " + stateWithLoops);
		//		System.out.println("loop weights: " + loopWeights);


		List<CounterexamplePath> cps = new ArrayList<>();
		List<GraphPath<Integer, DefaultWeightedEdge>> gcps = new ArrayList<>();

		if(boundedStep==-1){ // unbounded property
			int k = 1; 
			double lastAccProb = 0;
			while(true){ // iteratively find minimum k paths which forms a counterexample
				KShortestPaths<Integer, DefaultWeightedEdge> ksps = new KShortestPaths<Integer, DefaultWeightedEdge>(graph, 1, k);
				gcps = ksps.getPaths(endingVertex);

				double accProb = 0;
				for(GraphPath<Integer, DefaultWeightedEdge> gp : gcps){
					double weight = gp.getWeight();
					double pathProb = weightToProbability(weight);
					accProb += pathProb;
				}
				if(accProb>=probabilityBound){
					cps = graphPathToCounterexample(gcps);
					break;
				}
				if(accProb==lastAccProb){ // probability measure dont increase anymore, have to add loops
					//					cps = addCounterexamplePathsWithLoops(gcps, stateWithLoops, loopWeights, accProb);
					containLoops = true;
					cps = graphPathToCounterexample(gcps);
					cps.addAll(addLoopGraphPathToCounterexample(gcps, graph));
					break;
				}
				else{
					lastAccProb = accProb;
					k = (int) Math.pow(2, k);
				}

			}
		}
		else if(boundedStep>0){ // bounded property
			while(true){
				int k = 1;
				double lastAccProb = 0;
				KShortestPaths<Integer, DefaultWeightedEdge> ksps = new KShortestPaths<Integer, DefaultWeightedEdge>(graph, 1, k, boundedStep);
				gcps = ksps.getPaths(endingVertex);
				double accProb = 0;
				for(GraphPath<Integer, DefaultWeightedEdge> gp : gcps){
					double weight = gp.getWeight();
					double pathProb = (double)1/Math.pow(10, weight);
					accProb += pathProb;
				}
				if(accProb>=probabilityBound){
					break;
				}
				if(accProb==lastAccProb){ // probability measure dont increase anymore, have to add loops
					//					cps = addCounterexamplePathsWithLoops(gcps, stateWithLoops, loopWeights, accProb);
					containLoops = true;
					cps = graphPathToCounterexample(gcps);
					cps.addAll(addLoopGraphPathToCounterexample(gcps, graph));
					break;
				}
				else{
					lastAccProb = accProb;
					k++;
				}
			}
		}
		return cps;
	}

	//	private List<CounterexamplePath> addCounterexamplePathsWithLoops(List<GraphPath<Integer, DefaultWeightedEdge>> gcps,List<Integer> stateWithLoops, List<Double> loopWeights, double accProb){
	//		
	//		List<CounterexamplePath> cps = graphPathToCounterexample(gcps); // first add all paths without loops;
	//		List<LoopPoint> loopPoints = new ArrayList<>(); // maintain a list of loop points in ascending order
	//		for(GraphPath<Integer, DefaultWeightedEdge> gp : gcps){
	//			double weight = gp.getWeight();
	//			List<Integer> path = Graphs.getPathVertexList(gp);
	//			for(int v : path){
	//				int loopIndex = IntegerUtil.indexInList(v, stateWithLoops); 
	//				if(loopIndex!=-1){
	//					double addedWeight = weight + loopWeights.get(loopIndex);
	//					LoopPoint lp = new LoopPoint(path, v, addedWeight);
	//					loopPoints = insertLoopPoint(loopPoints, lp);
	//				}
	//			}
	//		}
	//		while(accProb<probabilityBound){ // add loops to accumulate probability mass to exceed the probability bound
	//			
	//			LoopPoint currentLP = loopPoints.get(0);
	//			
	//			accProb += weightToProbability(currentLP.addedWeight); // add a loop with highest probability to accumulate probability
	//			
	//			List<Integer> loopPath = currentLP.path;
	//			List<Integer> clonedLoopPath = IntegerUtil.cloneList(loopPath);
	//			
	//			double weight = loopWeights.get(IntegerUtil.indexInList(currentLP.vertex, stateWithLoops));
	//			double newAddedWeight = currentLP.addedWeight + weight; // udpate added weight
	//			
	//			for(int j=0; j<loopPath.size(); j++){
	//				int tmp = loopPath.get(j);
	//				if(tmp==currentLP.vertex){
	//					clonedLoopPath.add(j, tmp);
	//					break;
	//				}
	//			}
	//			
	//			// update counterexample paths
	//			List<Integer> tmpPath = IntegerUtil.cloneList(clonedLoopPath);
	//			tmpPath = IntegerUtil.removeLastElement(tmpPath);
	////			System.out.println("add counterexample path: " + tmpPath);
	//			cps.add(new CounterexamplePath(tmpPath));
	//			
	//			// update loop points
	//			LoopPoint newLoopPoint = new LoopPoint(clonedLoopPath, currentLP.vertex, newAddedWeight);
	//			loopPoints.remove(0);
	//			loopPoints = insertLoopPoint(loopPoints, newLoopPoint);
	//			break;
	//		}
	//		return cps;
	//	}


	public boolean isContainLoops() {
		return containLoops;
	}

	private List<CounterexamplePath> addLoopGraphPathToCounterexample(List<GraphPath<Integer, DefaultWeightedEdge>> gcps,
			DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph){
		List<CounterexamplePath> cps = new ArrayList<>();
		boolean loopAdded = false;
		
		for(GraphPath<Integer, DefaultWeightedEdge> gp : gcps){

			Map<Integer, Integer> addPoints = new HashMap<Integer, Integer>();
			
			List<Integer> path = Graphs.getPathVertexList(gp);
			IntegerUtil.removeLastElement(path);
			for(int i=0; i<path.size(); i++){
				int vertex = path.get(i);
				for(DefaultWeightedEdge edge : graph.outgoingEdgesOf(vertex)){
					double weight = graph.getEdgeWeight(edge);
					if(graph.getEdgeTarget(edge)==vertex && weight!=0){ // self loop with probability 1 is not considered
						loopAdded = true;
						addPoints.put(i, vertex);
					}
				}
			}
			
			if(loopAdded){
				int count = 0;
				for(int j : addPoints.keySet()){
					path.add(j+count,addPoints.get(j));
					count ++;
				}
				cps.add(new CounterexamplePath(path));
			}
		}
		return cps;
	}

	private double weightToProbability(double weight){
		return (double)1/Math.pow(10, weight);
	}


	private List<CounterexamplePath> graphPathToCounterexample(List<GraphPath<Integer, DefaultWeightedEdge>> gcps){
		List<CounterexamplePath> cps = new ArrayList<>();
		for(GraphPath<Integer, DefaultWeightedEdge> gp : gcps){
			List<Integer> path = Graphs.getPathVertexList(gp);
			IntegerUtil.removeLastElement(path);
			//			System.out.println("add counterexample path: " + path);
			CounterexamplePath cp = new CounterexamplePath(path);
			cps.add(cp);
		}
		return cps;
	}

	class LoopPoint{
		List<Integer> path;
		int vertex;
		double addedWeight;
		public LoopPoint(List<Integer> path, int vertex, double addedWeight) {
			this.path = path;
			this.vertex = vertex;
			this.addedWeight = addedWeight;
		}
	}

//	private List<LoopPoint> insertLoopPoint(List<LoopPoint> origLoopPoints, LoopPoint lp){
//		if(origLoopPoints.size()==0){
//			origLoopPoints.add(lp);
//			return origLoopPoints;
//		}
//
//		for(int i=0; i<origLoopPoints.size(); i++){
//			if(origLoopPoints.size()==0 || i==origLoopPoints.size()-1){
//				origLoopPoints.add(lp);
//			}
//			else if(origLoopPoints.get(i).addedWeight>lp.addedWeight){
//				origLoopPoints.set(i, lp);
//			}
//		}
//		return origLoopPoints;
//	}

	// absorbing state has the largest index
	private int findAbsorbingState(DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph){
		Set<Integer> vs = graph.vertexSet();
		int endingVertex = 0;
		for(int v : vs){
			if(v>endingVertex){
				endingVertex = v;
			}
		}
		return endingVertex;
	}

	private DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> PRISMModelToGraph(){
		DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph = 
				new DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge>
		(DefaultWeightedEdge.class);

		int origStateNumber = prismModel.getNumOfPrismStates();

		Set<Integer> collapsedStates = new HashSet<>(); // states neither satisfy phi or psai
		Set<Integer> psaiStates = new HashSet<>(); // states satisfy psai
		int collopsedStatesId = 0;
		for(int i=0; i<origStateNumber; i++){ // the prism model needed to be adapted, first two bits represents phi and psai respectively
			String state = prismModel.getPrismStates().get(i).getCurrentState();
			if(state.startsWith("0")){ // states that phi is not satisfied
				collapsedStates.add(prismModel.getPrismStates().get(i).getId());
				if(collapsedStates.size()==1){
					collopsedStatesId = prismModel.getPrismStates().get(i).getId();
				}
				continue;
			}
			if(state.startsWith("01") || state.startsWith("11")){ // states that psai is satisfied
				psaiStates.add(prismModel.getPrismStates().get(i).getId());
			}
		}

		int absorbingStateId = origStateNumber + 1;

		for(int i=0; i<origStateNumber; i++){ // add vertex
			PrismState ps = prismModel.getPrismStates().get(i);
			if(!collapsedStates.contains(ps.getId())){ // normal state
				graph.addVertex(ps.getId());
			}
		}

		if(collopsedStatesId!=0){
			graph.addVertex(collopsedStatesId);
		}
		graph.addVertex(absorbingStateId);

		System.out.println("vertex set: "  + graph.vertexSet());

		for(int vertex : graph.vertexSet()){
			if(vertex==collopsedStatesId || vertex==absorbingStateId){
				DefaultWeightedEdge edge = graph.addEdge(vertex, vertex);
				graph.setEdgeWeight(edge, 0);


			}
			else if(psaiStates.contains(vertex)){
				DefaultWeightedEdge edge = graph.addEdge(vertex, absorbingStateId);
				graph.setEdgeWeight(edge, 0);
			}
			else{
				PrismState ps = prismModel.getPrismStates().get(vertex-1);
				List<PrismState> nextStates = ps.getNextStates();
				List<Double> transProb = ps.getTransitionProb();
				for(int j=0; j<nextStates.size(); j++){
					if(collapsedStates.contains(nextStates.get(j).getId())){
						DefaultWeightedEdge edge = graph.addEdge(ps.getId(), collopsedStatesId);
						graph.setEdgeWeight(edge, Math.log10(1/transProb.get(j)));
					}
					else{
						DefaultWeightedEdge edge = graph.addEdge(ps.getId(), nextStates.get(j).getId());
						graph.setEdgeWeight(edge, Math.log10(1/transProb.get(j)));
					}
				}
			}
		}
		return graph;
	}

}
