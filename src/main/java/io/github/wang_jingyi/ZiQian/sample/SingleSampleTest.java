package io.github.wang_jingyi.ZiQian.sample;

import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.VariablesValue;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismPathData;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.utils.IntegerUtil;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SingleSampleTest implements HypothesisTest{

	private int sampleSize;
	private double p;

	public SingleSampleTest(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	@Override
	public boolean testHypothesis(double p, TestEnvironment te, Counterexample ce) throws IOException, ClassNotFoundException {
		int cpCounter = 0;
		
		if(te.getSampler().isObtainingNewSample()){ // testing from new samples
			for(int i=0; i<sampleSize; i++){
				System.out.println("- Sample: " + (i+1));
				te.getSampler().sample();
				List<VariablesValue> vvs = PrismPathData.extractSEData(te.getSampler().getLatestSample(), 
						AlgoProfile.vars,Integer.MAX_VALUE,te.getData_step_size(),te.getData_delimiter()); // variables values of last simulation
				PredicateAbstraction pa = new PredicateAbstraction(te.getPredicates());
				List<String> concrete_trace = pa.abstractList(vvs);
				if(te.test(concrete_trace,ce)){
					System.out.println("- Sample " + i + " is a counterexample path");
					cpCounter++;
				}
			}
		}
		else{ // testing from training data
			int counter = 0;
			for(List<String> concrete_trace : te.getTraining_data().getObservations()){
				if(te.test(concrete_trace, ce)){
					System.out.println("- Sample " +  counter + " is a counterexample path");
					cpCounter++;
				}
				counter++;
				if(counter>=sampleSize){break;} 
			}
		}
		System.out.println("- Sample complete");
		if(pValueHZero(sampleSize, p, cpCounter) < pValueHOne(sampleSize, p, cpCounter)){
			return true;
		}
		return false;
	}

	private double pValueHZero(int n, double p, int c){
		//		assert c >= 1 : "no path is in counterexample.";
		if(c==0){
			return 1;
		}
		double pvalue = 1 - IntegerUtil.BinCumulativeProb(n, p, c-1);
		return pvalue;
	}

	private double pValueHOne(int n, double p, int c){
		double pvalue = IntegerUtil.BinCumulativeProb(n, p, c);
		return pvalue;
	}

	public static List<Double> calculateTestedTranstionProb(TestEnvironment te, Counterexample ce) throws IOException{
		List<SplittingPoint> sps = ce.getAllSplittingPoints();
		PrismModel pm = ce.getPrismModel();

		List<Double> transProb = new ArrayList<>();

		Map<Integer, Integer> startingStateCounts = new HashMap<Integer, Integer>();
		Map<SplittingPoint, Integer> spCounts = new HashMap<>();

		for(SplittingPoint sp : sps){
			if(!startingStateCounts.containsKey(sp.getCurrentStateId())){
				startingStateCounts.put(sp.getCurrentStateId(), 0);
			}
			spCounts.put(sp, 0);
		}

		for(List<String> concrete_trace : te.getTraining_data().getObservations()){
			PrismState currentPS = pm.getInitialStates().get(0);
			for(int i=1; i<concrete_trace.size()-1; i++){
				int currentID = currentPS.getId();
				if(startingStateCounts.containsKey(currentID)){
					int currentCount = startingStateCounts.get(currentID);
					currentCount ++;
					startingStateCounts.put(currentID, currentCount);
				}
				else{
					startingStateCounts.put(currentID, 1);
				}

				int nextStateID = StringUtil.getStringIndex(concrete_trace.get(i), currentPS.getSigmas());
				if(nextStateID==-1){
//					System.out.println("- New transition happens.");
					continue;
				}
//				assert nextStateID!=-1 : "- Concrete trace not in the model.";
				PrismState nextPS = currentPS.getNextStates().get(nextStateID);
				int nextID = nextPS.getId();

				for(SplittingPoint sp : sps){
					if(sp.getCurrentStateId()==currentID && sp.getNextStateId()==nextID){
						int spCount = spCounts.get(sp);
						spCount++;
						spCounts.put(sp, spCount);
					}
				}
				currentPS = nextPS;
			}
		}

		for(SplittingPoint sp : sps){
			int nextCount = spCounts.get(sp);
			int startCount = startingStateCounts.get(sp.getCurrentStateId());
			if(startCount==0){
				transProb.add(0.0);
				continue;
			}
			transProb.add((double)nextCount/startCount);
		}
		return transProb;
	}


	@Override
	public List<Double> getTestedTransitionProb(TestEnvironment te, Counterexample ce) throws IOException {
		return calculateTestedTranstionProb(te, ce);
	}

	@Override
	public double getProbBound() {
		return p;
	}

}
