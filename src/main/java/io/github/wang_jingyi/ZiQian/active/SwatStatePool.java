package io.github.wang_jingyi.ZiQian.active;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwatStatePool {
	
	private Map<SwatState, Integer> swatStateMap;
	private Map<Integer, SwatState> reSwatStateMap;
	private List<SwatTrace> traces;
	
	public SwatStatePool(List<SwatTrace> traces) {
		super();
		this.swatStateMap  = new HashMap<SwatState, Integer>();
		this.reSwatStateMap = new HashMap<Integer, SwatState>();
		this.traces = traces;
		buildPool();
	}
	
	private void buildPool(){
		
		int stateIndex = 1;
		for(SwatTrace trace : traces){
			for(SwatState ss : trace.getTrace()){
				if(!swatStateMap.containsKey(ss)){
					swatStateMap.put(ss, stateIndex);
					reSwatStateMap.put(stateIndex, ss);
				}
				stateIndex ++;
			}
		}
	}

	public Map<SwatState, Integer> getSwatStateMap() {
		return swatStateMap;
	}
	
	public Map<Integer, SwatState> getReSwatStateMap() {
		return reSwatStateMap;
	}

}
