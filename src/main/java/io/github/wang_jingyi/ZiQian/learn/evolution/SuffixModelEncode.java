package io.github.wang_jingyi.ZiQian.learn.evolution;

import io.github.wang_jingyi.ZiQian.learn.DataSuffix;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

public class SuffixModelEncode extends AbstractCandidateFactory<int[]>{
	
	DataSuffix ds;
	
	public SuffixModelEncode(DataSuffix ds) {
		this.ds = ds;
	}

	public int[] generateRandomCandidate(Random rng) { // 1 means suffix included in the model, -1 means not
		int[] cand = new int[ds.getSuffixes().size()];
		while(!encodeComplete(cand)){
			int i = rng.nextInt(cand.length);
			if(encoded(cand, i)){
				continue;
			}
			cand[i] = 1;
			for(int j=0; j<cand.length; j++){
				if(j==i){
					continue;
				}
				if(StringUtil.isSuffix(ds.getSuffixes().get(j), ds.getSuffixes().get(i)) || 
						StringUtil.isSuffix(ds.getSuffixes().get(i), ds.getSuffixes().get(j))){
					cand[j] = -1;
				}
			}
		}
		
		// flip 1 to -1 as there is possibility that no history is needed to predict
		for(int i=0; i<cand.length; i++){
			if(cand[i]==1){
				double p = rng.nextDouble();
				if(p<(double)1/(ds.getMaxHistoryLength()+1)){
					cand[i] = -1;
				}
			}
		}
//		System.out.println("is new generated candidate valid? : " + SuffixModelEvaluator.isValidCandidate(ds, cand));
		return cand;
	}

	public static int[] setSuffixFalse(int i, DataSuffix ds, int[] cand) {
		List<String> trueSuffix = ds.getSuffixes().get(i); // the i-th suffix
		for(int j=0; j<cand.length; j++){
			if(j==i){
				continue;
			}
			if(StringUtil.isSuffix(ds.getSuffixes().get(j), trueSuffix) || 
					StringUtil.isSuffix(trueSuffix, ds.getSuffixes().get(j))){
				cand[j] = -1;
			}
		}
		return cand;
	}

	private boolean encoded(int[] cand, int i) {
		if(cand[i]==0){
			return false;
		}
		return true;
	}

	private boolean encodeComplete(int[] cand) {
		for(int i=0; i<cand.length; i++){
			if(cand[i]==0){
				return false;
			}
		}
		return true;
	}
	

}
