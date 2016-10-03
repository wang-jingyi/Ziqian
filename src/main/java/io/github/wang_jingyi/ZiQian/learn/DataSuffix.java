package io.github.wang_jingyi.ZiQian.learn;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSuffix implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Input data;
	private List<List<String>> suffixes; // note that empty string is not included in suffixes, take care
	private Map<List<String>,Integer> suffixIndexMap; // get the index of suffixes
	private List<List<Integer>> suffixGroupedIds;
	private List<Map<String,Double>> suffixGeneratingDistribution;
	private Map<String,Double> generatingDistribution;
	private int maxHistoryLength;
	
	
	public DataSuffix(Input data, int n) {
		this.data = data;
		this.maxHistoryLength = n;
		this.suffixes = new ArrayList<List<String>>();
		this.suffixIndexMap = new HashMap<List<String>, Integer>();
		this.suffixGeneratingDistribution = new ArrayList<Map<String,Double>>();
		this.generatingDistribution = new HashMap<String, Double>();
	}
	
	public void execute(){
		calSuffixes();
		calGeneratingDistribution();
		calSuffixGeneratingDistribution();
	}

	public List<List<Integer>> getSuffixGroupedIds() {
		return suffixGroupedIds;
	}

	private void calGeneratingDistribution() {
		List<String> observation = data.getObservations().get(0);
		for(String s : data.getAlphabet()){
			List<String> sl = new ArrayList<String>();
			sl.add(s);
			double tmp = StringUtil.calOccProb(sl, observation);
			if(tmp>0){
				generatingDistribution.put(s, tmp);
			}
			
		}
	}
	
	private void calSuffixes() {
		List<String> observation = data.getObservations().get(0);
		List<String> event =  observation;
		for(int i=0; i<event.size(); i++){
			for(int j=0; j<maxHistoryLength; j++){
				if(i-j>=0){
					List<String> sl = event.subList(i-j, i+1);
					if(StringUtil.getStringListIndex(sl, suffixes)==-1){
						suffixes.add(sl);
					}
				}
			}
		}
		for(int i=0; i<suffixes.size(); i++){
			suffixIndexMap.put(suffixes.get(i), i);
		}
		System.out.println("size of data suffixes: " + suffixes.size());
	}
	
	private void calSuffixGeneratingDistribution(){
		List<String> observation = data.getObservations().get(0);
		List<String> event = observation;
		for(int i=0; i<suffixes.size(); i++){
			Map<String,Double> generatingDistribution = new HashMap<String, Double>();
			for(String s : data.getAlphabet()){
				double p = StringUtil.calNextSymbolTransProb(suffixes.get(i), s, event);
				if(p>0){
					generatingDistribution.put(s, p);
				}
			}
			suffixGeneratingDistribution.add(generatingDistribution);
		}
	}
	
	public Map<List<String>, Integer> getSuffixIndexMap() {
		return suffixIndexMap;
	}

	public Input getData() {
		return data;
	}

	public List<List<String>> getSuffixes() {
		return suffixes;
	}

	public int getMaxHistoryLength() {
		return maxHistoryLength;
	}
	
	public List<Map<String, Double>> getSuffixGeneratingDistribution() {
		return suffixGeneratingDistribution;
	}
	
	public Map<String, Double> getGeneratingDistribution() {
		return generatingDistribution;
	}

}
	
	
