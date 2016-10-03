package io.github.wang_jingyi.ZiQian.evolution;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.PredicateSet;
import io.github.wang_jingyi.ZiQian.learn.DataPrefix;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.profile.TimeProfile;
import io.github.wang_jingyi.ZiQian.utils.IntegerUtil;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.IntArrayCrossover;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

public class LearnMergeEvolutions implements LearningDTMC{

	private DataPrefix dp;
	private int numOfStates;
	private int[] resultGrouping;
	private PrismModel pm = new PrismModel();

	@Override
	public void learn(Input data) {
		System.out.println("Retrieving data prefixes inforamtion...");
		TimeProfile.dataPrefixStartTime = System.nanoTime();
		dp = new DataPrefix(data);
		dp.execute();
		TimeProfile.dataPrefixExes++;
		TimeProfile.dataPrefixEndTime = System.nanoTime();
		System.out.println("Executing evolutionary merging process...");
		executeEvolutions();
	}

	private void executeEvolutions() {
		
		// create candidate grouping
		System.out.println("Creating initial candidate grouping...");
		numOfStates = dp.getData().getAlphabet().size(); 
		int mergeEncodeStateNum = numOfStates + 1;// add 1 for the empty state
		PrefixMergeEncode mef = new PrefixMergeEncode(dp,mergeEncodeStateNum);

		// create a pipeline that applies cross-over then mutation.
		System.out.println("Creating pipelines that applies cross-over and mutation...");
		List<EvolutionaryOperator<int[]>> operators
		= new LinkedList<EvolutionaryOperator<int[]>>();
		operators.add(new IntArrayCrossover());
		EvolutionaryOperator<int[]> pipeline
		= new EvolutionPipeline<int[]>(operators);

		// create evaluator of candidates
		System.out.println("Creating candidate evaluator...");
		MergeEvaluator me = new MergeEvaluator(dp);

		// create selection strategy of candidates
		System.out.println("Creating selection strategy of candidates...");
		SelectionStrategy<Object> selection = new TournamentSelection(new Probability(0.9));

		// create random generator for evolution
		Random rng = new MersenneTwisterRNG();

		// create evolution engine
		EvolutionEngine<int[]> engine
		= new GenerationalEvolutionEngine<int[]>(mef,pipeline,me,selection,rng);

		engine.addEvolutionObserver(new EvolutionObserver<int[]>()
				{
			public void populationUpdate(PopulationData<? extends int[]> data)
			{
//				System.out.printf("Generation %d: %s\n",
//						data.getGenerationNumber(),
//						data.getBestCandidate());
			}
				});
		
		System.out.println("Evolving...");
		resultGrouping = engine.evolve(10, 2, new GenerationCount(1));
		
	}
	
	

//	private void getAutomataSize() { // valid state indexes are updated after this
//		List<Integer> queue = new ArrayList<>();
//		queue.add(0);
//		validStateIndex.add(0);
//		while(queue.size()!=0){
//			int st = queue.get(0);
//			queue.remove(0);
//			for(int k=0; k<dp.getNextSymbolIndex().get(st).size(); k++){
//				int next = dp.getNextSymbolIndex().get(st).get(k);
//				if(next!=-1 && !validStateIndex.contains(next)){
//					validStateIndex.add(next);
//					queue.add(next);
//				}
//			}
//		}
//		validStateIndex.remove(0); // remove the empty state
//		Collections.sort(validStateIndex);
//		numOfStates = validStateIndex.size();
//	}
	
	public void PrismModelTranslation(Input data, PredicateSet presSet, String modelName) {
		List<PrismState> prismStates = new ArrayList<PrismState>(); // list of prism states
		List<PrismState> initialStates = new ArrayList<PrismState>(); // list of initial states
		
		
		List<Integer> initStatesIds = new ArrayList<>();
		
		HashSet<Integer> stateIDs = new HashSet<>(); // store indexes of grouped states
		Map<Integer,HashSet<Integer>> IDs = new HashMap<>(); // map many state indexes to one merged state id
		for(int i=0; i<resultGrouping.length; i++){
			if(!stateIDs.contains(resultGrouping[i])){
				stateIDs.add(resultGrouping[i]);
				HashSet<Integer> thisID = new HashSet<>();
				thisID.add(i);
				IDs.put(resultGrouping[i], thisID);
			}
			else{
				IDs.get(resultGrouping[i]).add(i);
			}
		}
		
		for(int id : IDs.keySet()){
			for(int it : dp.getInitStates()){
				if(IDs.get(id).contains(it)){
					initStatesIds.add(id);
					break;
				}
			}
		}
		
		Map<Integer,List<Integer>> nextIDs = new HashMap<Integer, List<Integer>>(); // next state id map for each id
		Map<Integer,List<Double>> nextTransP = new HashMap<Integer, List<Double>>(); // next transition probabilities for each id
		
		for(int i : IDs.keySet()){
			if(i==-1){ // empty state, forms initial distribution  %%% why would I skip this before? need to fix this problem
				nextIDs.put(i, initStatesIds);
				nextTransP.put(i, dp.getInitDistribution());
				continue;
			}
			int totoalOutTrans = 0;
			List<Integer> nextStateID = new ArrayList<Integer>();
			List<Integer> nextStateFrq = new ArrayList<Integer>();
			List<Double> nextStateTransP = new ArrayList<Double>();
			
			for(int ind : IDs.get(i)){ // iterate over the next state ids
				totoalOutTrans += dp.getSortedPrefixCounts().get(ind);
				if(dp.getSortedPrefixFinalCount().get(ind)!=0){
					int nk = IntegerUtil.indexInList(i, nextStateID);
					if(nk==-1){
						nextStateID.add(i); // not in the id list
						nextStateFrq.add(dp.getSortedPrefixFinalCount().get(ind));
					}
					else{
						int nf = nextStateFrq.get(nk);
						nf += dp.getSortedPrefixFinalCount().get(ind);
						nextStateFrq.set(nk, nf);
					}
					
				}
				for(int j=0; j<dp.getData().getAlphabet().size(); j++){
					List<String> nextStringList = StringUtil.cloneList(dp.getSortedPrefixes().get(ind));
					nextStringList.add(dp.getData().getAlphabet().get(j));
					int nextInd = dp.isInSortedPrefix(nextStringList);
					if(nextInd!=-1){
						int nextid = resultGrouping[nextInd];
						int nk = IntegerUtil.indexInList(nextid, nextStateID);
						if(nk==-1){ // not in the id list
							nextStateID.add(nextid);
							nextStateFrq.add(dp.getNextSymbolFrequency().get(ind).get(j));
						}
						else{
							int nf = nextStateFrq.get(nk);
							nf += dp.getNextSymbolFrequency().get(ind).get(j);
							nextStateFrq.set(nk, nf);
						}
					}
				}
			}
			
			int nextStateSumFrq = 0;
			for(int j=0; j<nextStateID.size(); j++){
				nextStateSumFrq += nextStateFrq.get(j);
				double transPJ = (double)nextStateFrq.get(j)/totoalOutTrans;
				nextStateTransP.add(transPJ);
			}
			assert nextStateSumFrq==totoalOutTrans : "out transitions not equal to the total occurrence";
			nextIDs.put(i, nextStateID);
			nextTransP.put(i, nextStateTransP);
		}
		
		Map<Integer,Integer> groupIDtoPrismID = new HashMap<>(); // map grouped state id to prism state id
		int prismStateID = 1; // empty state has id 1
		
		List<String> emptylabel = new ArrayList<>(); // add empty state first
		emptylabel.add("empty");
		PrismState emptyps = new PrismState(prismStateID, emptylabel);
		groupIDtoPrismID.put(-1, prismStateID);
		prismStates.add(emptyps);
		prismStateID ++;
		
		for(int i : IDs.keySet()){
			if(i==-1){ // add the empty state as a prism state
				continue;
			}
			List<String> label = new ArrayList<String>();
			int modr = i%dp.getData().getAlphabet().size();
			String cur = dp.getData().getAlphabet().get(modr);
			label.add(cur);
			PrismState ps = new PrismState(prismStateID, label);
			groupIDtoPrismID.put(i, prismStateID);
			prismStates.add(ps);
			prismStateID ++;
		}
		
		
		for(int i : IDs.keySet()){
			PrismState ps = prismStates.get(groupIDtoPrismID.get(i)-1); // get corresponding prism state
			ps.getTransitionProb().addAll(nextTransP.get(i));
			
			for(int j=0; j<nextIDs.get(i).size(); j++){
				int nextPrismStateID = groupIDtoPrismID.get(nextIDs.get(i).get(j));
				PrismState nextPS = prismStates.get(nextPrismStateID-1);
				ps.getNextStates().add(nextPS);
				ps.getSigmas().addAll(nextPS.getLabel());
			}
			assert ps.getNextStates().size()==ps.getTransitionProb().size();
		}
		
		
		initialStates.add(prismStates.get(0)); // empty states as initial states
		
		pm.setNumOfPrismStates(prismStates.size());
		pm.setPrismStates(prismStates);
		pm.setInitialStates(initialStates);
		pm.setPredicates(presSet.getPredicates());
	}

	@Override
	public PrismModel getPrismModel() {
		return pm;
	}

}
