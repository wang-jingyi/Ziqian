package io.github.wang_jingyi.ZiQian.sample;

import java.util.ArrayList;
import java.util.List;

public class CounterexamplePath{
	
	private List<Integer> counterPath = new ArrayList<>(); //  
	public List<Integer> testedTransitionCount = new ArrayList<>();
	public int concretePathCount;
	
	public CounterexamplePath(List<Integer> counterpath) {
		this.counterPath = counterpath;
		for(int i=0; i<counterPath.size()-1; i++){
			testedTransitionCount.add(0);
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(counterPath.toString());
		return sb.toString();
	}
	
	
	public List<Integer> getCounterPath() {
		return counterPath;
	}

	public void setCounterPath(List<Integer> counterPath) {
		this.counterPath = counterPath;
	}

}
