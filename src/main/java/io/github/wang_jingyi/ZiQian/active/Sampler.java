package io.github.wang_jingyi.ZiQian.active;

import java.util.List;

public interface Sampler {
	
	public List<Integer> newSample(double[] initDistribution, int sampleLength);
	
}
