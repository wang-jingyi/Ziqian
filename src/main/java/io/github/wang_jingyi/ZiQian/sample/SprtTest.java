package io.github.wang_jingyi.ZiQian.sample;

import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.VariablesValue;
import io.github.wang_jingyi.ZiQian.prism.PrismPathData;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

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
	public boolean testHypothesis(double p, TestEnvironment te, Counterexample ce)
			throws IOException,
			ClassNotFoundException {
		
		int bi = 0;

		double p1 = p - sigma;
		double p0 = p + sigma;
		double aid = Math.pow(p1, bi) * Math.pow((1-p1), sampleSize-bi) / Math.pow(p0, bi) / Math.pow((1-p0), sampleSize-bi);
		boolean force_break = false;
		while(!acceptH0(aid) && !acceptH1(aid) && !force_break){
			
			if(te.getSampler().isDecomposed()==true){
				te.getSampler().sample();
				@SuppressWarnings("unchecked")
				List<String> concrete_trace = (List<String>) FileUtil.readObject(te.getSampler().getLatestSample());
				int counter = 0;
				if(te.test(concrete_trace, ce)){
					System.out.println("- Sample " +  counter + " is a counterexample path");
					bi++;
				}
			}
			
			else if(te.getSampler().isObtainingNewSample()){ // testing from new samples
				te.getSampler().sample();
				sampleSize++;
				System.out.println("- New sample: " + sampleSize);
				List<VariablesValue> vvs = PrismPathData.extractSEData(te.getSampler().getLatestSample(), 
						AlgoProfile.vars,Integer.MAX_VALUE,te.getData_step_size(),te.getData_delimiter()); // variables values of last simulation
				PredicateAbstraction pa = new PredicateAbstraction(te.getPredicates());
				List<String> concrete_trace = pa.abstractList(vvs);
				if(te.test(concrete_trace,ce)){ // the sample is in the counterexample
					bi++;
				}
			}
			else{ // testing from training data
				int counter = 0;
				for(List<String> concrete_trace : te.getTraining_data().getObservations()){
					if(te.test(concrete_trace, ce)){ // the sample is in the counterexample
						System.out.println("- Sample " +  counter + " is a counterexample path");
						bi++;
					}
					counter++;
				}
				force_break = true; // all training data tested, force break
			}
			aid = Math.pow(p1, bi) * Math.pow((1-p1), sampleSize-bi) / Math.pow(p0, bi) / Math.pow((1-p0), sampleSize-bi);
			
		}
		System.out.println("- Total sample size: " + sampleSize);
		System.out.println("- Number of realizable paths in the counterexample: " + bi);
		
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
	public List<Double> getTestedTransitionProb(TestEnvironment te, Counterexample ce) throws IOException{
		return SingleSampleTest.calculateTestedTranstionProb(te,ce);
	}

	@Override
	public double getProbBound() {
		return p;
	}

}
