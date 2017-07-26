package io.github.wang_jingyi.ZiQian.learn.evolution;

import io.github.wang_jingyi.ZiQian.learn.DataPrefix;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/*
 * encode state merge as integer array
 * and randomly generate candidates
 * */

public class PrefixMergeEncode extends AbstractCandidateFactory<int[]> {
	
	private final DataPrefix dp;
	private int observableStatesSize; // observable states
	private int stateSpace; // total states after merging
	private Map<Integer,List<Integer>> alphabetAvailableIndexMap;
	
	public PrefixMergeEncode(DataPrefix dp, int stateSpace) {
		this.dp = dp;
		this.stateSpace = stateSpace;
		this.observableStatesSize = dp.getData().getAlphabet().size();
		this.alphabetAvailableIndexMap = new HashMap<Integer, List<Integer>>();
	}
	
	public int getObservableStatesSize() {
		return observableStatesSize;
	}

	public void setObservableStatesSize(int obervableStatesSize) {
		this.observableStatesSize = obervableStatesSize;
	}

	public int getStateSpace() {
		return stateSpace;
	}

	public void setStateSpace(int stateSpace) {
		this.stateSpace = stateSpace;
	}

	public Map<Integer, List<Integer>> getAlphabetAvailableIndexMap() {
		return alphabetAvailableIndexMap;
	}

	public void setAlphabetAvailableIndexMap(
			Map<Integer, List<Integer>> alphabetAvailableIndexMap) {
		this.alphabetAvailableIndexMap = alphabetAvailableIndexMap;
	}

	public DataPrefix getDp() {
		return dp;
	}

	public PrefixMergeEncode(DataPrefix dp) {
		this.dp = dp;
	}
	
	
	@Override
	public int[] generateRandomCandidate(Random rng) {
		getAvailableMapIndex();
		int[] cand = new int[dp.getPrefixesTotalNum()];
		Random rnd = new MersenneTwisterRNG();
		
		for(int i=0; i<dp.getPrefixesTotalNum(); i++){
			int prefixSize = dp.getSortedPrefixes().get(i).size();
			if(prefixSize==0){ // empty state
				cand[i] = -1;
				continue;
			}
			String s = dp.getSortedPrefixes().get(i).get(prefixSize-1); // last observed state 
			int alphabetIndex = StringUtil.getStringIndex(s, dp.getData().getAlphabet());
//			System.out.println("String index in alphabet: " + alphabetIndex);
			cand[i] = alphabetAvailableIndexMap.get(alphabetIndex).
					get(rnd.nextInt(alphabetAvailableIndexMap.get(alphabetIndex).size())); // randomly choose one from available ids
		}
		return cand;
	}
	
	private void getAvailableMapIndex(){
		for(int i=0; i<dp.getData().getAlphabet().size(); i++){
			List<Integer> availableIndexes = new ArrayList<Integer>();
			int j = 0;
			while(true){
				if(j * observableStatesSize + i >= stateSpace-1){ // bug fix by changing 'stateSpace' to 'stateSpace-1'
					break;
				}
				availableIndexes.add(j * observableStatesSize + i);
				j++;
			}
			alphabetAvailableIndexMap.put(i, availableIndexes);
		}
	}
	
}
