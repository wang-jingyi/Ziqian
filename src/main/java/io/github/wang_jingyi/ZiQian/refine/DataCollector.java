package io.github.wang_jingyi.ZiQian.refine;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.sample.SplittingPoint;

import java.io.IOException;
import java.util.List;

import net.sf.javaml.core.Dataset;

public interface DataCollector {
	
	public Dataset collectDataFromPaths(List<String> pathsDirs, List<Predicate> predicates, List<SplittingPoint> sps, PrismModel pm) throws IOException;
	
}
