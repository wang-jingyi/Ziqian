package io.github.wang_jingyi.ZiQian.refine;

import java.io.FileNotFoundException;

import net.sf.javaml.core.Dataset;

public interface Divider {
	
	public LearnedPredicate findSplitPredicates(Dataset ds) throws FileNotFoundException;
	
}
