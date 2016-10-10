package io.github.wang_jingyi.ZiQian.active;

import java.util.List;

public interface Estimator {
	
	public MarkovChain estimate(List<List<Integer>> frequencyMatrix);

}
