package io.github.wang_jingyi.ZiQian.sample;

import java.io.IOException;
import java.util.List;

public class SprtTest implements HypothesisTest{

	private double p;
	private double alpha;
	private double beta;
	private double sigma;
	private int sampleSize;

	public SprtTest(double p, double alpha, double beta, double sigma) {
		this.p = p;
		this.alpha = alpha;
		this.beta = beta;
		this.sigma = sigma;
	}

	@Override
	public boolean testHypothesis(double p, Counterexample ce)
			throws IOException,
			ClassNotFoundException {

		TestEnvironment te = ce.getTestEnvironment();

		int bi = 0;

		double p1 = p - sigma;
		double p0 = p + sigma;
		double aid = Math.pow(p1, bi) * Math.pow((1-p1), sampleSize-bi) / Math.pow(p0, bi) / Math.pow((1-p0), sampleSize-bi);
		while(!acceptH0(aid) && !acceptH1(aid)){
			te.sample();
			sampleSize ++;
			System.out.println("sample : " + sampleSize);
			if(te.test(ce)){ // the sample is in the counterexample
				bi ++;
			}
			aid = Math.pow(p1, bi) * Math.pow((1-p1), sampleSize-bi) / Math.pow(p0, bi) / Math.pow((1-p0), sampleSize-bi);
		}
		System.out.println("total sample size: " + sampleSize);
		System.out.println("number of paths in counterexample: " + bi);
		
		if(acceptH0(aid)){
			return false;
		}
		if(acceptH1(aid)){
			return true;
		}
		return false;
	}

	private boolean acceptH0(double aid){
		if(aid>=(1-beta)/alpha){
			return true;
		}
		return false;
	}

	private boolean acceptH1(double aid){
		if(aid<=beta/(1-alpha)){
			return true;
		}
		return false;
	}

	@Override
	public List<Double> getTestedTransitionProb(Counterexample ce) throws IOException{
		return SingleSampleTest.calculateTestedTranstionProb(ce);
	}

	@Override
	public double getProbBound() {
		return p;
	}

}
