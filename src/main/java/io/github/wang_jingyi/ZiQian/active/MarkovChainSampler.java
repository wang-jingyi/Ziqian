package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.List;

public class MarkovChainSampler implements Sampler {
	
	MarkovChain mc;
	
	public MarkovChainSampler(MarkovChain mc) {
		this.mc = mc;
	}

	@Override
	public List<Integer> newSample(double[] initDistribution, int sampleLength) {
		int startIndex = MarkovChain.nextState(initDistribution);
		return samplePath(sampleLength, startIndex);
	}

	// sample the markov chain of certain length
	private List<Integer> samplePath(int pathLength, int startIndex){
		List<Integer> path = new ArrayList<Integer>();
		int crstate = startIndex;
		path.add(startIndex);
		for(int i=0; i<pathLength; i++){
			crstate = mc.nextState(crstate);
			path.add(crstate);
		}
		return path;
	}

}
