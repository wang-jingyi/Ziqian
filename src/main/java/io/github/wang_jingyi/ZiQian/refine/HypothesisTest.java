package io.github.wang_jingyi.ZiQian.refine;

import java.io.IOException;
import java.util.List;

public interface HypothesisTest {
	
	public boolean testHypothesis(double p, TestEnvironment te, Counterexample ce) throws IOException, ClassNotFoundException;
	
	public List<Double> getTestedTransitionProb(TestEnvironment te, Counterexample ce) throws IOException;
	
	public double getProbBound();
}
