package io.github.wang_jingyi.ZiQian.sample;

import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.VariablesValue;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismPathData;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
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
	public boolean testHypothesis(double p, Counterexample ce) throws IOException, ClassNotFoundException {
		TestEnvironment te = ce.getTestEnvironment();
		int cpCounter = 0;
		for(int i=0; i<sampleSize; i++){
			te.sample();
			System.out.println("sample: " + (i+1));
			if(te.test(ce)){
				System.out.println("sample " + i + " is a counterexample path.");
				cpCounter++;
			}
		}
		
		System.out.println("sample complete.");
		
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
	
	
	static List<Double> calculateTestedTranstionProb(Counterexample ce) throws IOException{
		TestEnvironment te = ce.getTestEnvironment();
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

		List<String> paths = FileUtil.filesInDir(te.getOutputFilePath());
		paths.addAll(FileUtil.filesInDir(Config.DATA_PATH)); // add learning data statistics in
		
		for(String path : paths){
			List<VariablesValue> vvs = PrismPathData.extractSEData(path, AlgoProfile.vars,Integer.MAX_VALUE, 
					Config.STEP_SIZE); // variables values of last simulation
			PredicateAbstraction pa = new PredicateAbstraction(te.getPredicateSet().getPredicates());
			List<String> absExs = pa.abstractList(vvs);
			
			PrismState currentPS = pm.getInitialStates().get(0);
			for(int i=1; i<absExs.size()-1; i++){
				int currentID = currentPS.getId();
				if(startingStateCounts.containsKey(currentID)){
					int currentCount = startingStateCounts.get(currentID);
					currentCount ++;
					startingStateCounts.put(currentID, currentCount);
				}
				else{
					startingStateCounts.put(currentID, 1);
				}
				
				int nextStateID = StringUtil.getStringIndex(absExs.get(i), currentPS.getSigmas());
				if(nextStateID==-1){
					System.out.println("new transition happens.");
					continue;
				}
				assert nextStateID!=-1 : "concrete path not in the model.";
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
				transProb.add(-1.0);
				continue;
			}
			transProb.add((double)nextCount/startCount);
		}
		return transProb;
	}
	
	
	@Override
	public List<Double> getTestedTransitionProb(Counterexample ce) throws IOException {
		return calculateTestedTranstionProb(ce);
	}

	@Override
	public double getProbBound() {
		return p;
	}

	
	
}
