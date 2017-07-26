package io.github.wang_jingyi.ZiQian.learn.evolution;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.learn.DataSuffix;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

public class SuffixModelEvaluator implements FitnessEvaluator<int[]>{
	
	private Input data;
	private DataSuffix ds;
	private int modelSize;
	
	
	public SuffixModelEvaluator(Input data, DataSuffix ds) {
		this.data = data;
		this.ds = ds;
	}

	public double getRealFitness(int[] candidate){
		if(!isValidCandidate(ds,candidate)){
			return Double.MAX_VALUE;
		}
		return getLogDataProbability(data,ds,candidate) - 0.5 * calModelSize(candidate) * Math.log(candidate.length);
	}
	
	public static boolean isValidCandidate(DataSuffix ds, int[] candidate) {
		for(int i=0; i<candidate.length; i++){
			if(candidate[i]==1){
				List<String> includedSuffix = ds.getSuffixes().get(i);
				for(List<String> s : ds.getSuffixIndexMap().keySet()){
					if(StringUtil.equals(s, includedSuffix)){ // this bug causes my morning
						continue;
					}
					if(StringUtil.isSuffix(s, includedSuffix) || StringUtil.isSuffix(includedSuffix, s)){
						int j = ds.getSuffixIndexMap().get(s);
						if(candidate[j]==-1){
							continue;
						}
						else{
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public int getModelSize(){
		return modelSize;
	}
	
	
	private int calModelSize(int[] candidate) {
		for(int i=0; i<candidate.length; i++){
			if(candidate[i]==1){
				modelSize ++;
			}
		}
		return modelSize;
	}
	
	public static double getLogDataProbability(Input data, DataSuffix ds, int[] candidate, List<String> event){
		double logLikelihood = 0;
		logLikelihood += Math.log(StringUtil.calOccProb(event.subList(0, 1), event));
		for(int i=1; i<event.size(); i++){
			List<String> currentEvent = event.subList(0, i);
			boolean suffixFound = false;
			for(int j=0; j<candidate.length; j++){
				if(candidate[j]==1 && StringUtil.isSuffix(ds.getSuffixes().get(j), currentEvent)){
					suffixFound = true;
					logLikelihood += Math.log(ds.getSuffixGeneratingDistribution().get(j).get(event.get(i)));
					break;
				}
			}
			if(suffixFound==false){
				logLikelihood += Math.log(ds.getGeneratingDistribution().get(event.get(i)));
			}
		}
		return logLikelihood;
	}
	
	public static double getLogDataProbability(Input data, DataSuffix ds, int[] candidate) {
		List<String> event = data.getObservations().get(0);
		double logLikelihood = 0;
		logLikelihood += Math.log(StringUtil.calOccProb(event.subList(0, 1), event));
		for(int i=1; i<event.size(); i++){
			List<String> currentEvent = event.subList(0, i);
			boolean suffixFound = false;
			for(int j=0; j<candidate.length; j++){
				if(candidate[j]==1 && StringUtil.isSuffix(ds.getSuffixes().get(j), currentEvent)){
					suffixFound = true;
					logLikelihood += Math.log(ds.getSuffixGeneratingDistribution().get(j).get(event.get(i)));
					break;
				}
			}
			if(suffixFound==false){
				logLikelihood += Math.log(ds.getGeneratingDistribution().get(event.get(i)));
			}
		}
		return logLikelihood;
	}

	public boolean isNatural() {
		return false;
	}

	public double getFitness(int[] candidate, List<? extends int[]> population) {
		return Math.abs(getRealFitness(candidate)); // fitness must be positive
	}

}
