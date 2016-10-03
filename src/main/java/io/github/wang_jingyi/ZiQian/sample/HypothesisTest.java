package io.github.wang_jingyi.ZiQian.sample;

import java.io.IOException;
import java.util.List;

public interface HypothesisTest {
	
	public boolean testHypothesis(double p, Counterexample ce) throws IOException, ClassNotFoundException;
	
	public List<Double> getTestedTransitionProb(Counterexample ce) throws IOException;
	
	public double getProbBound();
}
