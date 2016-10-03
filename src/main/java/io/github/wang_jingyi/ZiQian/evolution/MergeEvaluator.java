package io.github.wang_jingyi.ZiQian.evolution;

import io.github.wang_jingyi.ZiQian.learn.DataPrefix;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.util.HashSet;
import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

public class MergeEvaluator implements FitnessEvaluator<int[]> {

	private final DataPrefix dp;

	public MergeEvaluator(DataPrefix dp){
		this.dp = dp;
	}

	@Override
	public double getFitness(int[] candidate, List<? extends int[]> population) {
		double fitness = 0;
		double dataProbability = getDataProbability(candidate);
		if(tooAbstractMerge(candidate)){
			return Double.MAX_VALUE;
		}
		fitness = dataProbability;
		return fitness;
	}
	
	private boolean tooAbstractMerge(int[] candidate){
		int stateNum = getCandidateStateNum(candidate);
		if(stateNum<dp.getData().getAlphabet().size()){
			return true;
		}
		return false;
	}
	
	private int getCandidateStateNum(int[] candidate){
		int stateNum = 0;
		HashSet<Integer> stateID = new HashSet<Integer>();
		for(int i=0; i<candidate.length; i++){
			if(!stateID.contains(candidate[i])){
				stateNum++;
				stateID.add(candidate[i]);
			}
		}
		return stateNum;
	}

	private double getDataProbability(int[] candidate){
		List<List<String>> multiObservations = dp.getData().getObservations();
		double logLikelihood = 0;
//		System.out.println("Calculating fitness for current candidate... " );

		// calculate probability of generating data
		for(List<String> obs : multiObservations){
			for(int j=1; j<obs.size(); j++){
				List<String> currentPrefix = obs.subList(0, j);
				int currentPrefixInd = dp.isInSortedPrefix(currentPrefix);
//				System.out.println("current prefix index in sortedPrefix: " + currentPrefixInd);
				assert currentPrefixInd>=0 : "Not valid prefix, see the prefix generated";
				int mergedStateInd = candidate[currentPrefixInd]; // index of state id after merging
//				System.out.println("state id after merging: " + mergedStateInd);

				HashSet<Integer> curPrefixesIndMerged = new HashSet<Integer>();
				for(int c=0; c<candidate.length; c++){
					if(candidate[c]==mergedStateInd){
						curPrefixesIndMerged.add(c); // add index in sortPrefixes in same merged state
					}
				}

				int nextSymbolId = StringUtil.getStringIndex(obs.get(j), dp.getData().getAlphabet());
//				System.out.println("next symbol id in the alphabet: " + nextSymbolId);
				assert nextSymbolId!=-1 : "next symbol not found";

				int nextPrefixInd = dp.getNextSymbolIndex().get(currentPrefixInd).get(nextSymbolId);
//				System.out.println("next symbol prefix index in sortedPrefix: " + nextPrefixInd);
				int mergedNextStateInd = candidate[nextPrefixInd]; // index of next symbol state id after merging
//				System.out.println("next symbol state id after merging: " + mergedNextStateInd);

				HashSet<Integer> nextSymbolPrefixesIndMerged = new HashSet<Integer>();
				for(int c=0; c<candidate.length; c++){
					if(candidate[c]==mergedNextStateInd){
						nextSymbolPrefixesIndMerged.add(c); // add next state index of same merged state
					}
				}

				int currentSum = 0;
				int nextSum = 0;
				for(int cur : curPrefixesIndMerged){
					currentSum += dp.getSortedPrefixCounts().get(cur);
					for(int inext=0; inext<dp.getNextSymbolIndex().get(cur).size(); inext++){
						if(nextSymbolPrefixesIndMerged.contains(dp.getNextSymbolIndex().get(cur).get(inext))){
							nextSum += dp.getNextSymbolFrequency().get(cur).get(inext);
						}
					}
				}
				double nextSymbolProbability = (double)nextSum/currentSum;
//				System.out.println("next symbol probability is " + nextSymbolProbability);
				logLikelihood += Math.log(nextSymbolProbability);
//				System.out.println("data log likelihood so far is " + logLikelihood);
			}
		}
//		System.out.println("data log likelihood for this candidate: " + logLikelihood);
//		System.out.println(IntegerOps.intArrayToString(candidate));
		assert logLikelihood<0 : "log likelihood cannot be positive";
		return Math.abs(logLikelihood);// log likelihood must be negative, but fitness must be positive
	}


	// depends on my final formula of score to calculate fitness, RTC
	public boolean isNatural() {
		return false;
	}

}
