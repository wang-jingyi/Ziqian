package io.github.wang_jingyi.ZiQian.active;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwatStatePool {
	
	private Map<SwatState, Integer> swatStateMap;
	private Map<Integer, SwatState> reSwatStateMap;
	private List<SwatTrace> traces;
	private int stateNumber;
	
	public SwatStatePool(List<SwatTrace> traces) {
		super();
		this.swatStateMap  = new HashMap<SwatState, Integer>();
		this.reSwatStateMap = new HashMap<Integer, SwatState>();
		this.traces = traces;
	}
	
	public void buildPool(){
		
		int stateIndex = 0; // notice start from 0
		for(SwatTrace trace : traces){
			for(SwatState ss : trace.getTrace()){
				if(!swatStateMap.containsKey(ss)){
					swatStateMap.put(ss, stateIndex);
					reSwatStateMap.put(stateIndex, ss);
					stateIndex ++;
				}
			}
		}
		stateNumber = stateIndex;
	}

	public void setStateNumber(int stateNumber) {
		this.stateNumber = stateNumber;
	}

	public int getStateNumber() {
		return stateNumber;
	}
	
	public Map<SwatState, Integer> getSwatStateMap() {
		return swatStateMap;
	}
	
	public Map<Integer, SwatState> getReSwatStateMap() {
		return reSwatStateMap;
	}
	
	@Override
	public String toString() {
		return "SwatStatePool [swatStateMap=" + swatStateMap
				+ ", reSwatStateMap=" + reSwatStateMap + "]";
	}

}
