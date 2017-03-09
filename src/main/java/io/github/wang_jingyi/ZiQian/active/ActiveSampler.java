package io.github.wang_jingyi.ZiQian.active;

import java.util.List;

public interface ActiveSampler {
	
	public List<Integer> newSample(double[] initDistribution, int sampleLength);
	
}
