package io.github.wang_jingyi.ZiQian.refine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.utils.IntegerUtil;
import io.github.wang_jingyi.ZiQian.utils.MapUtil;

/*
 * Given a PrismModel, a time step and a probability bound,
 * calculate a minimum set of paths whose probability measure is larger than the probability bound 
 * */

public class CounterexampleGenerator {

	private PrismModel prismModel;
	private int boundedStep; // bounded step is -1 if unbounded
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

		Map<Integer, Double> loops = new HashMap<Integer, Double>();

		for(int vertex: graph.vertexSet()){ // find loops with weights in ascending order 
			for(DefaultWeightedEdge edge : graph.outgoingEdgesOf(vertex)){
				double weight = graph.getEdgeWeight(edge);
				if(graph.getEdgeTarget(edge)==vertex && weight!=0){ // self loop with probability 1 is not included
					loops.put(vertex, weightToProbability(weight));
				}
			}
		}

		LinkedHashMap<Integer, Double> sortedLoops = 
				(LinkedHashMap<Integer, Double>) MapUtil.sortByValue(loops);

		System.out.println("- State with loops and probability to itself: " + sortedLoops);

		List<CounterexamplePath> cps = new ArrayList<>();
		List<GraphPath<Integer, DefaultWeightedEdge>> gcps = new ArrayList<>();
		
		int step = 0;
		int k = 1;
		if(boundedStep==-1){ // unbounded property
			double lastAccProb = 0;
			int start_vertex = prismModel.getInitialStates().get(0).getId(); // initial state as start vertex
			Map<CounterexamplePath, Double> cpProbs = new HashMap<CounterexamplePath, Double>();
			while(true){ // iteratively find minimum k paths which forms a counterexample
				k = (int) Math.pow(2, step); // initial number of paths 
				KShortestPaths<Integer, DefaultWeightedEdge> ksps = new KShortestPaths<Integer, DefaultWeightedEdge>(graph,start_vertex,k);
				gcps = ksps.getPaths(endingVertex);

				double accProb = 0;
				for(GraphPath<Integer, DefaultWeightedEdge> gp : gcps){
					double weight = gp.getWeight();
					double pathProb = weightToProbability(weight);
					CounterexamplePath cp = graphPathToCounterexamplePath(gp);
					cps.add(cp);
					cpProbs.put(cp, pathProb);
					accProb += pathProb;
				}
				if(accProb>=probabilityBound){
					System.out.println("- Total probability of counterexample: " + accProb);
					break;
				}
				if(accProb==lastAccProb){ // probability measure dont increase anymore, have to add loops
					cps.addAll(addCounterexamplePathsWithLoops(cpProbs, sortedLoops, accProb));
					containLoops = true;
					break;
				}
				else{
					lastAccProb = accProb;
					step = step ++;
				}

			}
		}
		else if(boundedStep>0){ // bounded property
			Map<CounterexamplePath, Double> cpProbs = new HashMap<CounterexamplePath, Double>();
			while(true){
				k = (int) Math.pow(2, step);
				double lastAccProb = 0;
				KShortestPaths<Integer, DefaultWeightedEdge> ksps = new KShortestPaths<Integer, DefaultWeightedEdge>(graph, 1, k, boundedStep);
				gcps = ksps.getPaths(endingVertex);
				double accProb = 0;
				for(GraphPath<Integer, DefaultWeightedEdge> gp : gcps){
					double weight = gp.getWeight();
					double pathProb = weightToProbability(weight);
					CounterexamplePath cp = graphPathToCounterexamplePath(gp);
					cps.add(cp);
					cpProbs.put(cp, pathProb);
					accProb += pathProb;
				}
				if(accProb>=probabilityBound){
					break;
				}
				if(accProb==lastAccProb){ // probability measure don't increase anymore, have to add loops
					cps.addAll(addCounterexamplePathsWithLoops(cpProbs, sortedLoops, accProb));
					containLoops = true;
					break;
				}
				else{
					lastAccProb = accProb;
					step ++;
				}
			}
		}
		System.out.println("- Total number of paths in the counterexample: " + cps.size());
		return cps;
	}

	private List<CounterexamplePath> addCounterexamplePathsWithLoops(Map<CounterexamplePath, Double> cpProbs,
			Map<Integer, Double> sortedLoops, double accProb){
		
		List<CounterexamplePath> cps = new ArrayList<CounterexamplePath>();

		while(accProb<probabilityBound){ // add loops to accumulate probability mass to exceed the probability bound
			LoopPoint currentLP = findInsertingLoopPoint(cpProbs,sortedLoops);
			List<Integer> loopPath = currentLP.path;
			if(loopPath==null){
				System.out.println("- No loop to add");
				break;
			}
			
			accProb += weightToProbability(currentLP.addedWeight); // add a loop with highest probability to accumulate probability
			List<Integer> clonedLoopPath = IntegerUtil.cloneList(loopPath);

			for(int j=0; j<loopPath.size(); j++){
				int tmp = loopPath.get(j);
				if(tmp==currentLP.vertex){
					clonedLoopPath.add(j, tmp);
					break;
				}
			}

			// update counterexample paths
			IntegerUtil.removeLastElement(clonedLoopPath);
			cps.add(new CounterexamplePath(clonedLoopPath));
		}
		System.out.println("- Total probability of counterexample: " + accProb);
		return cps;
	}


	public boolean isContainLoops() {
		return containLoops;
	}

	private double weightToProbability(double weight){
		return (double)1/Math.pow(10, weight);
	}

	private CounterexamplePath graphPathToCounterexamplePath(GraphPath<Integer, DefaultWeightedEdge> gp){
		List<Integer> path = Graphs.getPathVertexList(gp);
		IntegerUtil.removeLastElement(path);
		CounterexamplePath cp = new CounterexamplePath(path);
		return cp;
	}
	
	class LoopPoint{
		List<Integer> path;
		int vertex;
		double addedWeight;
		LoopPoint(){}
		LoopPoint(List<Integer> path, int vertex, double addedWeight) {
			this.path = path;
			this.vertex = vertex;
			this.addedWeight = addedWeight;
		}
	}

	private LoopPoint findInsertingLoopPoint(Map<CounterexamplePath,Double> cpProbs, Map<Integer, Double> sortedLoops){
		LoopPoint lp = new LoopPoint();
		lp.addedWeight = 0;
		for(CounterexamplePath cp : cpProbs.keySet()){
			for(int v : cp.getCounterPath()){
				if(sortedLoops.containsKey(v)){
					double weight = sortedLoops.get(v);
					double newAddedWeight = cpProbs.get(cp) + weight;
					if(newAddedWeight>lp.addedWeight){
						lp.path = cp.getCounterPath();
						lp.vertex = v;
						lp.addedWeight = newAddedWeight;
					}
				}
			}
		}
		return lp;
	}

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
				assert nextStates.size()==transProb.size() : "=== Not valid state";
				for(int j=0; j<nextStates.size(); j++){
					if(collapsedStates.contains(nextStates.get(j).getId())){
						DefaultWeightedEdge edge = graph.addEdge(ps.getId(), collopsedStatesId);
						graph.setEdgeWeight(edge, Math.log10(1/transProb.get(j)));
					}
					else{
						DefaultWeightedEdge edge = graph.addEdge(ps.getId(), nextStates.get(j).getId());
						if(edge==null || transProb.get(j)==null){
							System.out.println("breakout");
						}
						graph.setEdgeWeight(edge, Math.log10(1/transProb.get(j)));
					}
				}
			}
		}
		return graph;
	}

}
