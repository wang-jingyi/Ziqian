package io.github.wang_jingyi.ZiQian.learn;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.utils.IntegerUtil;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * prefixes statistics
 * */

public class DataPrefix implements Serializable{

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Input getData() {
		return data;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6227008075940159101L;
	private List<List<String>> sortedPrefixes = new ArrayList<>();
	private List<Integer> sortedPrefixCounts = new ArrayList<>(); // occurrence of prefix
	private List<Integer> sortedPrefixFinalCount = new ArrayList<>(); // occurrence of prefix as final
	private List<List<Integer>> nextSymbolFrequency = new ArrayList<>();
	private List<List<Integer>> nextSymbolIndex = new ArrayList<>();
	private Input data;
	private Map<List<String>,Integer> prefixIndexMap = new HashMap<>(); // to index prefixes and its corresponding information more efficiently
	private int prefixesTotalNum = 0;
	private List<Double> initDistribution = new ArrayList<>();
	private List<Integer> initStates = new ArrayList<>();
	private List<String> initSigmas = new ArrayList<>();

	public List<Double> getInitDistribution() {
		return initDistribution;
	}

	@Override
	public DataPrefix clone(){
		DataPrefix dp = new DataPrefix();
		dp.sortedPrefixes = this.sortedPrefixes;
		dp.data = this.data;
		dp.prefixIndexMap = this.prefixIndexMap;
		dp.prefixesTotalNum = this.prefixesTotalNum;

		dp.sortedPrefixCounts = IntegerUtil.cloneList(this.sortedPrefixCounts);
		dp.sortedPrefixFinalCount = IntegerUtil.cloneList(this.sortedPrefixFinalCount);
		dp.nextSymbolFrequency = IntegerUtil.cloneListOfList(this.nextSymbolFrequency);
		dp.nextSymbolIndex = IntegerUtil.cloneListOfList(this.nextSymbolIndex);
		return dp;
	}

	public DataPrefix(){
		super();
	}

	public DataPrefix(Input data){
		this.data = data;
	}

	public List<List<Integer>> getNextSymbolIndex() {
		return nextSymbolIndex;
	}

	public void setNextSymbolIndex(List<List<Integer>> nextSymbolIndex) {
		this.nextSymbolIndex = nextSymbolIndex;
	}

	public void execute(){
		calSortedPrefixes();
		prefixesTotalNum = sortedPrefixes.size();
		buildPrefixIndexMap();
		calNextSymbolThings();
		calInitDistribution();
//		checkDataPrefix();
	}




	public List<String> getInitSigmas() {
		return initSigmas;
	}

	private void calInitDistribution() {
		List<Integer> initf = nextSymbolFrequency.get(0);
		List<Integer> inits = nextSymbolIndex.get(0);
		
		for(int i=0; i<initf.size(); i++){
			if(initf.get(i)!=0){
				initSigmas.add(data.getAlphabet().get(i));
				initDistribution.add((double)initf.get(i)/sortedPrefixCounts.get(0));
				initStates.add(inits.get(i)); // the index in data prefixes
			}
		}
		
	}

	//	private void checkDataPrefix() {
	//		for(int i=0; i<prefixesTotalNum; i++){
	//			int occ = sortedPrefixCounts.get(i);
	//			int freqSum = sortedPrefixFinalCount.get(i);
	//			for(int j=0; j<data.getAlphabet().size(); j++){
	//				freqSum += nextSymbolFrequency.get(i).get(j);
	//
	//			}
	////			assert occ==freqSum : "total occurrence not equal to out transitions";
	//		}
	//
	//	}

	public List<Integer> getInitStates() {
		return initStates;
	}

	public int getPrefixesTotalNum() {
		return prefixesTotalNum;
	}

	public void setPrefixesTotalNum(int prefixesTotalNum) {
		this.prefixesTotalNum = prefixesTotalNum;
	}

	public List<List<Integer>> getNextSymbolFrequency() {
		return nextSymbolFrequency;
	}

	public void setNextSymbolFrequency(List<List<Integer>> nextSymbolFrequency) {
		this.nextSymbolFrequency = nextSymbolFrequency;
	}

	private void calNextSymbolThings() {
		for(int i=0; i<sortedPrefixes.size(); i++){
			List<Integer> nextSymFrequency = new ArrayList<Integer>();
			List<Integer> nextSymIndex = new ArrayList<Integer>();
			for(int j=0; j<data.getAlphabet().size(); j++){
				List<String> nextSymPrefix = StringUtil.cloneList(sortedPrefixes.get(i));
				nextSymPrefix.add(data.getAlphabet().get(j));
				if(isInSortedPrefix(nextSymPrefix)==-1){
					nextSymFrequency.add(0);
					nextSymIndex.add(-1); // set to -1 if not in the prefix set
				}
				else{
					int ind = isInSortedPrefix(nextSymPrefix);
					nextSymIndex.add(ind);
					nextSymFrequency.add(sortedPrefixCounts.get(ind));

				}
			}
			nextSymbolFrequency.add(nextSymFrequency);
			nextSymbolIndex.add(nextSymIndex);
		}

	}

	private void buildPrefixIndexMap() {
		for(int i=0; i<sortedPrefixes.size(); i++){
			prefixIndexMap.put(sortedPrefixes.get(i), i);
		}
	}

	// get list of prefixes sorted by length and alphabet
	private void calSortedPrefixes(){
		// take one prefix from data and insert to the right position, starting from empty string
		List<String> emptyPrefix = new ArrayList<String>();
		sortedPrefixes.add(emptyPrefix); // initialize with empty prefix
		sortedPrefixCounts.add(data.getObservations().size());
		sortedPrefixFinalCount.add(0);
		for(List<String> obs : data.getObservations()){
			for(int i=1; i<=obs.size(); i++){
				List<String> nextPrefix = new ArrayList<String>(obs.subList(0, i));
				insertPrefix(nextPrefix,obs);
			}
		}
	}

	public List<List<String>> getSortedPrefixes() {
		return sortedPrefixes;
	}

	private void insertPrefix(List<String> nextPrefix, List<String> obs) {

		boolean finalPrefix = false;
		if(nextPrefix.size()==obs.size()){ // an itself prefix
			finalPrefix = true;
		}
		for(int i=0; i<sortedPrefixes.size(); i++){
			if(prefixComparator(sortedPrefixes.get(i), nextPrefix)<0){ // if precede
				sortedPrefixes.add(i, nextPrefix);
				sortedPrefixCounts.add(i,1);
				if(finalPrefix){
					sortedPrefixFinalCount.add(i,1);
				}
				else{
					sortedPrefixFinalCount.add(i,0);
				}
				break;

			}
			else if(prefixComparator(sortedPrefixes.get(i), nextPrefix)==0){ // if equal
				int count = sortedPrefixCounts.get(i)+1;
				sortedPrefixCounts.set(i, count);
				if(finalPrefix){
					int fcount = sortedPrefixFinalCount.get(i)+1;
					sortedPrefixFinalCount.set(i, fcount);
				}
				break;
			}
			else if(i==sortedPrefixes.size()-1){ // if last index, i+1 will not be reachable
				sortedPrefixes.add(nextPrefix);
				sortedPrefixCounts.add(1);
				if(finalPrefix){
					sortedPrefixFinalCount.add(1);
				}
				else{
					sortedPrefixFinalCount.add(0);
				}
				break;
			}
			else if(prefixComparator(sortedPrefixes.get(i+1), nextPrefix)<0){
				sortedPrefixes.add(i+1,nextPrefix);
				sortedPrefixCounts.add(i+1,1);
				if(finalPrefix){
					sortedPrefixFinalCount.add(i+1,1);
				}
				else{
					sortedPrefixFinalCount.add(i+1,0);
				}
				break;
			}
			else if(prefixComparator(sortedPrefixes.get(i+1), nextPrefix)==0){
				int count = sortedPrefixCounts.get(i+1)+1;
				sortedPrefixCounts.set(i+1, count);
				if(finalPrefix){
					int fcount = sortedPrefixFinalCount.get(i+1)+1; // bug fix by changing get(i) to get(i+1)
					sortedPrefixFinalCount.set(i+1, fcount);
				}
				break;
			}
			// other cases, just continue
		}
	}

	private int prefixComparator(List<String> cmp, List<String> toCmp){
		// return positive if the second prefix should be after first, 0 if equal, negative if precede

		// compare length first
		if(cmp.size()<toCmp.size()){
			return 1;
		}
		if(cmp.size()>toCmp.size()){
			return -1;
		}
		// in case of equal length, compare one by one until ordered
		for(int i=0; i<cmp.size(); i++){
			int	res = toCmp.get(i).compareTo(cmp.get(i));
			if(res==0){
				continue;
			}
			return res;
		}
		return 0;
	}

	public int isInSortedPrefix(List<String> prefixq) {
		for(int i=0; i<prefixesTotalNum; i++){
			if(StringUtil.equals(prefixq, sortedPrefixes.get(i))){
				return i;
			}
		}
		return -1;
	}

	public List<Integer> getSortedPrefixCounts() {
		return sortedPrefixCounts;
	}

	public void setSortedPrefixCounts(List<Integer> sortedPrefixCounts) {
		this.sortedPrefixCounts = sortedPrefixCounts;
	}

	public List<Integer> getSortedPrefixFinalCount() {
		return sortedPrefixFinalCount;
	}

	public void setSortedPrefixFinalCount(List<Integer> sortedPrefixFinalCount) {
		this.sortedPrefixFinalCount = sortedPrefixFinalCount;
	}

	public void setSortedPrefixes(List<List<String>> sortedPrefixes) {
		this.sortedPrefixes = sortedPrefixes;
	}

	public Map<List<String>, Integer> getPrefixIndexMap() {
		return prefixIndexMap;
	}

	public void setPrefixIndexMap(Map<List<String>, Integer> prefixIndexMap) {
		this.prefixIndexMap = prefixIndexMap;
	}
}
