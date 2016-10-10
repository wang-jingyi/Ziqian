package io.github.wang_jingyi.ZiQian.active;

import java.util.List;

public interface Sampler {
	
	public List<Integer> newSample(List<Double> initDistribution, int sampleLength);
	
}
