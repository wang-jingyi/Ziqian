package io.github.wang_jingyi.ZiQian.active;

import java.util.List;

public class SampleStatisticGetter {
	
	public static int[][] getFrequencyMatrix(List<List<Integer>> traces, int stateNumber){
		
		int[][] freqMatrix = new int[stateNumber][stateNumber];
		for(List<Integer> trace : traces){
			int tl = trace.size();
			for(int i=0; i<trace.size()-1; i++){
				freqMatrix[trace.get(i)][trace.get(i+1)] ++;
			}
			freqMatrix[trace.get(tl-1)][trace.get(tl-1)] ++;
		}
		return freqMatrix;
	}
	
}
