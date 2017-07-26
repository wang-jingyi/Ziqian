package io.github.wang_jingyi.ZiQian.learn.evolution;

import io.github.wang_jingyi.ZiQian.learn.DataSuffix;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

public class SuffixMutation implements EvolutionaryOperator<int[]>{
	
	DataSuffix ds;
	int mutationCount;
	
	public SuffixMutation(DataSuffix ds, int mutationCount) {
		this.ds = ds;
		this.mutationCount = mutationCount;
	}

	public List<int[]> apply(List<int[]> selectedCandidates, Random rng) {
		
		for(int[] candidate : selectedCandidates){
			int lastMutation = -1;
			for(int i=0; i<mutationCount; i++){
				int mutationLoc = 0;
				while(true){
					mutationLoc = rng.nextInt(candidate.length);
					if(mutationLoc!=lastMutation){
						if(candidate[mutationLoc]==1){
							candidate[mutationLoc] = -1;
							candidate = selectNewSuffix(ds,candidate,mutationLoc);
						}
						else{
							candidate[mutationLoc] = 1;
							candidate = flipOriginalSuffix(ds,candidate,mutationLoc);
						}
						break;
					}
				}
				lastMutation = mutationLoc;
			}
		}
		return selectedCandidates;
	}
	
	// flip the original suffix included excluded since a new suffix is selected
	private int[] flipOriginalSuffix(DataSuffix ds, int[] candidate,
			int mutationLoc) {
		return SuffixModelEncode.setSuffixFalse(mutationLoc, ds, candidate);
	}

	// select a new suffix to include since original suffix is excluded
	private int[] selectNewSuffix(DataSuffix ds, int[] candidate,
			int mutationLoc) {
		List<Integer> inds = new ArrayList<Integer>();
		for(int j=0; j<candidate.length; j++){
			if(j==mutationLoc){
				continue;
			}
			if(StringUtil.isSuffix(ds.getSuffixes().get(j), ds.getSuffixes().get(mutationLoc)) || 
					StringUtil.isSuffix(ds.getSuffixes().get(mutationLoc), ds.getSuffixes().get(j))){
					inds.add(j);
			}
		}
		if(inds.isEmpty()){
			return candidate;
		}
		int i = new MersenneTwisterRNG().nextInt(inds.size());
		int newloc = inds.get(i);
		candidate[newloc] = 1;
		return candidate;
	}

}
