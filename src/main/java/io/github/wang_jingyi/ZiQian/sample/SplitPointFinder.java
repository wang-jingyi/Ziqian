package io.github.wang_jingyi.ZiQian.sample;

import io.github.wang_jingyi.ZiQian.prism.PrismModel;

import java.util.List;

public interface SplitPointFinder {
	
	public List<SplittingPoint> findSplitingStates(PrismModel pm);  // return a set of splitting points with probability deviation in descending matter
	
}
