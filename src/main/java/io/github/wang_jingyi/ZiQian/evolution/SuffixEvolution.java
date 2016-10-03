package io.github.wang_jingyi.ZiQian.evolution;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.PredicateSet;
import io.github.wang_jingyi.ZiQian.learn.DataSuffix;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.uncommons.watchmaker.framework.termination.Stagnation;

public class SuffixEvolution{

	private int[] resultCandidate;
	private int maxMaxHistory = 2; // longest history to watch
	private int initialPopulationSize = 100; // initial number of candidates
	private int populationSize = 10; // number of candidates in each population
	private int eliteCount = 3; // number of candidate to preserve for each generation
	private int mutationNum = 2; // number of mutation for each candidate
	private int generationLimit = 3; // terminate without improvement in this limit

	protected PrismModel singleObsEvolution(Input data, PredicateSet pset, DataSuffix ds){
		int maxHistory = ds.getMaxHistoryLength();
		double bestFitness = Double.MAX_VALUE;
		EvolutionResult er = new EvolutionResult();

		while(maxHistory<=maxMaxHistory){
			System.out.println("current maximum history length: " + maxHistory);
			er = executeEvolution(data,ds);
			if(er.bestCandidatefitness>bestFitness){ // fitness dont get better when state size increases
				System.out.println("the fitness doesnt increase anymore with maximum history length: " + (maxHistory-1));
				break;
			}
			else{
				bestFitness = er.bestCandidatefitness;
				resultCandidate = er.bestCandidate;
				maxHistory++;
				System.out.println("best candidate data likelihood: " + SuffixModelEvaluator.getLogDataProbability(data, ds, resultCandidate));
				if(maxHistory<=maxMaxHistory){
					System.out.println("the max length of history needs to be increased.");
					ds = new DataSuffix(data, maxHistory);
					ds.execute();
				}
				else{
					System.out.println("reach preset maximum history, algorithm terminates.");
					break;
				}

			}
		}
		System.out.println("best candidate fitness: " + bestFitness);
		PrismModel pm = PrismModelTranslation(data, pset, ds);
		return pm;
	}

	private EvolutionResult executeEvolution(Input data, DataSuffix ds) {

		Random rng = new MersenneTwisterRNG();
		final EvolutionResult er = new EvolutionResult();
		er.bestCandidatefitness = Double.MAX_VALUE;

		// create candidate grouping
		System.out.println("Creating initial candidate grouping...");
		SuffixModelEncode sse = new SuffixModelEncode(ds);
		sse.generateInitialPopulation(initialPopulationSize, rng);


		// create a pipeline that applies cross-over then mutation.
		System.out.println("Creating pipelines that applies cross-over and mutation...");
		List<EvolutionaryOperator<int[]>> operators
		= new LinkedList<EvolutionaryOperator<int[]>>();
		operators.add(new SuffixMutation(ds, mutationNum));
		operators.add(new IntArrayCrossover()); // add crossover
		EvolutionaryOperator<int[]> pipeline
		= new EvolutionPipeline<int[]>(operators);

		// create evaluator of candidates
		System.out.println("Creating candidate evaluator...");
		SuffixModelEvaluator sme = new SuffixModelEvaluator(data, ds);

		// create selection strategy of candidates
		System.out.println("Creating selection strategy of candidates...");
		SelectionStrategy<Object> selection = new TournamentSelection(new Probability(0.9));

		// create evolution engine
		EvolutionEngine<int[]> engine
		= new GenerationalEvolutionEngine<int[]>(sse,pipeline,sme,selection,rng);

		engine.addEvolutionObserver(new EvolutionObserver<int[]>()
				{
			public void populationUpdate(PopulationData<? extends int[]> data)
			{
				if(data.getBestCandidateFitness()<er.bestCandidatefitness){
					er.bestCandidate = data.getBestCandidate();
					er.bestCandidatefitness = data.getBestCandidateFitness();
				}

				System.out.printf("Generation %d: %s\n",
						data.getGenerationNumber(),
						//						IntegerOps.intArrayToString(data.getBestCandidate()),
						data.getBestCandidateFitness());
			}
				});

		System.out.println("Evolving...");
		engine.evolve(populationSize, eliteCount, new Stagnation(generationLimit, false));
		return er;
	}

	public PrismModel PrismModelTranslation(Input data, PredicateSet pset, DataSuffix ds) {

		PrismModel pm = new PrismModel();
		List<PrismState> prismStates = new ArrayList<PrismState>(); // list of prism states
		List<PrismState> initialStates = new ArrayList<PrismState>(); // list of initial states
		Map<List<String>,Integer> stateLabelMap = new HashMap<List<String>, Integer>(); // map the state label and id

		List<String> emptySuffix = new ArrayList<String>();
		boolean emptySuffixAdded = false;

		int psID = 1; // prism model id starts from 1


		for(int i=0; i<resultCandidate.length; i++){ // first round, add suffixes needs extending first
			if(resultCandidate[i]==1){
				List<String> curSuffix = ds.getSuffixes().get(i);
				List<String> longestPrefix = StringUtil.getLongestPrefix(curSuffix);

				if(StringUtil.equals(emptySuffix, longestPrefix) && emptySuffixAdded==false){ // only add empty suffix once
					PrismState ps = new PrismState(psID, emptySuffix);
					for(String s : ds.getGeneratingDistribution().keySet()){
						ps.getSigmas().add(s);
						ps.getTransitionProb().add(ds.getGeneratingDistribution().get(s));
					}
					prismStates.add(ps);
					stateLabelMap.put(ps.getLabel(), psID);
					psID++;
					emptySuffixAdded = true;
				}

				if(!ds.getSuffixIndexMap().containsKey(longestPrefix) && !StringUtil.equals(longestPrefix, emptySuffix)){ // longest prefix not in the suffix set, needs extending
					List<List<String>> extendingSuffixes = StringUtil.getSuffixes(longestPrefix);
					for(List<String> ls : extendingSuffixes){
						if(ds.getSuffixIndexMap().containsKey(ls)){
							int extendingSuffixInd = ds.getSuffixIndexMap().get(ls);
							resultCandidate[extendingSuffixInd] = 0; // set extending suffix to 0

							PrismState ps = new PrismState(psID, longestPrefix); // add suffix needs extending first
							stateLabelMap.put(longestPrefix, psID);
							for(String s : ds.getSuffixGeneratingDistribution().get(extendingSuffixInd).keySet()){ // set next state probability
								ps.getSigmas().add(s);
								ps.getTransitionProb().add(ds.getSuffixGeneratingDistribution().get(extendingSuffixInd).get(s));
							}
							prismStates.add(ps);
							psID++;
							break;
						}
					}
				}
			}
		}

		for(int i=0; i<resultCandidate.length; i++){ // add remaining suffixes
			if(resultCandidate[i]==1){
				PrismState ps = new PrismState(psID, ds.getSuffixes().get(i));
				prismStates.add(ps);
				stateLabelMap.put(ps.getLabel(), psID);
				for(String s : ds.getSuffixGeneratingDistribution().get(i).keySet()){
					ps.getSigmas().add(s);
					ps.getTransitionProb().add(ds.getSuffixGeneratingDistribution().get(i).get(s));
				}
				psID++;
			}
		}

		// check if empty suffix needs to be added
		if(emptySuffixAdded==false){
			boolean flag = false;
			for(PrismState ps : prismStates){
				for(int i=0; i<ps.getSigmas().size(); i++){
					List<String> labelCopy = StringUtil.cloneList(ps.getLabel());
					labelCopy.add(ps.getSigmas().get(i));
					boolean suffixIn = false;
					List<List<String>> sff = StringUtil.getSuffixes(labelCopy);
					for(List<String> sf : sff){
						if(stateLabelMap.containsKey(sf)){
							suffixIn = true;
							break;
						}
					}
					if(suffixIn==false){
						PrismState eps = new PrismState(psID, emptySuffix);
						for(String s : ds.getGeneratingDistribution().keySet()){
							eps.getSigmas().add(s);
							eps.getTransitionProb().add(ds.getGeneratingDistribution().get(s));
						}
						prismStates.add(eps);
						stateLabelMap.put(eps.getLabel(), psID);
						psID++;
						emptySuffixAdded = true;
						flag = true;
						break;
					}
				}
				if(flag == true){
					break;
				}
			}
		}

		// set up next states for each suffix state
		for(PrismState ps : prismStates){
			for(int i=0; i<ps.getSigmas().size(); i++){
				List<String> labelCopy = StringUtil.cloneList(ps.getLabel());
				labelCopy.add(ps.getSigmas().get(i));
				boolean suffixIn = false;
				List<List<String>> sff = StringUtil.getSuffixes(labelCopy);
				for(List<String> sf : sff){
					if(stateLabelMap.containsKey(sf)){
						ps.getNextStates().add(prismStates.get(stateLabelMap.get(sf)-1));
						suffixIn = true;
						break;
					}
				}
				if(suffixIn==false){
					ps.getNextStates().add(prismStates.get(stateLabelMap.get(emptySuffix)-1));
				}
			}


		}

		pm.setPrismStates(prismStates);
		pm.setInitialStates(initialStates);
		pm.setNumOfPrismStates(psID-1); // after add last state, psID is still added by one
		pm.setPredicates(pset.getPredicates());
		return pm;
	}

}
