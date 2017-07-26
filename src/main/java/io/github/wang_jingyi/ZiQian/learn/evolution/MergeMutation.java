package io.github.wang_jingyi.ZiQian.learn.evolution;

import io.github.wang_jingyi.ZiQian.learn.DataPrefix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

public class MergeMutation implements EvolutionaryOperator<int[]>{
	
	private DataPrefix dp;
	private int mutationCount; // number of mutation each candidate
	
	
	public MergeMutation(DataPrefix dp, int mutationCount){
		this.dp = dp;
		this.mutationCount = mutationCount;
	}
	
	public List<int[]> apply(List<int[]> selectedCandidates, Random rng) {
		List<int[]> mutatedPopulation = new ArrayList<int[]>();
		for(int[] individual : selectedCandidates){
			for(int i=0; i<mutationCount; i++){
				int rint = rng.nextInt(dp.getPrefixesTotalNum());
				List<String> curPrefix = dp.getSortedPrefixes().get(rint);
				int prefixSize = curPrefix.size();
				if(prefixSize==0){
					break;
				}
				String lastString = curPrefix.get(prefixSize-1);
				int rintCate = individual[rint];
				for(int j=rint; j<dp.getPrefixesTotalNum(); j++){
					List<String> jPrefix = dp.getSortedPrefixes().get(j);
					int jSize = jPrefix.size();
					String lastJs = jPrefix.get(jSize-1);
					System.out.println(lastJs);
					if(lastString.equals(lastJs)){
						System.out.println("mutation happens.");
						individual[j] = rintCate; // mutate to same group with rint 
						break;
					}
				}
			}
			mutatedPopulation.add(individual);
		}
		return mutatedPopulation;
	}

}
