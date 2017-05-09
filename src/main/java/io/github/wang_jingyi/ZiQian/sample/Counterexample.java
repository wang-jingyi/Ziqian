package io.github.wang_jingyi.ZiQian.sample;

import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.profile.TimeProfile;
import io.github.wang_jingyi.ZiQian.run.GlobalConfigs;
import io.github.wang_jingyi.ZiQian.utils.MapUtil;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * includes all the information of a counterexample,
 * to find the spurious state transitions to split 
 * */

public class Counterexample implements SplitPointFinder{

	private List<CounterexamplePath> counterPaths;
	private int longestCounterPath;
	private PrismModel pm;
	private List<Double> transProbInCps = new ArrayList<>();
	private List<Double> testedTransProbInCps = new ArrayList<>();
	private HypothesisTest ht;
	private List<SplittingPoint> allSplittingPoints;
	private List<SplittingPoint> sortedSplittingPoints; // splitting point with probability deviation 0 is not included

	public Counterexample(PrismModel pm, List<CounterexamplePath> cps, HypothesisTest ht) {
		this.pm = pm;
		this.counterPaths = cps;
		this.ht = ht;
		updateLongestPaths();
	}

	public Counterexample(PrismModel pm, String cpFilePath, TestEnvironment te, HypothesisTest ht) {
		this.pm = pm;
		this.counterPaths = CounterexampleOperations.extractCounterexample(cpFilePath);
		this.ht = ht;
		updateLongestPaths();
	}

	private void updateLongestPaths(){
		for(CounterexamplePath cp : counterPaths){
			if(cp.getCounterPath().size()>longestCounterPath){
				longestCounterPath = cp.getCounterPath().size();
			}
		}
	}

	public void analyze(TestEnvironment te) throws ClassNotFoundException, IOException{
		
		TimeProfile.ht_start_time = System.nanoTime();
		boolean result = ht.testHypothesis(ht.getProbBound(), te, this);
		TimeProfile.ht_end_time = System.nanoTime();
		TimeProfile.hypothesis_testing_times.add(TimeProfile.nanoToSeconds(TimeProfile.ht_end_time
				-TimeProfile.ht_start_time));
		
		if(result){
			System.out.println("- Result: the counterexample is actual with certain confidence");
			System.out.println("- Counterexample wrote to file."); // to do
			System.out.println("====== The property is voilated ======");
			TimeProfile.iteration_end_time = System.nanoTime();
			TimeProfile.iteration_times.add(TimeProfile.nanoToSeconds(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time));
			TimeProfile.main_end_time = System.nanoTime();
			TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
			TimeProfile.outputTimeProfile();
			TimeProfile.outputTimeProfile(GlobalConfigs.PROJECT_ROOT+"/time_profile.txt");
			System.exit(0);
		}
		else{
			System.out.println("- Result: the counterexample is spurious");
			System.out.println("------ Find spurious transitions ------");
			TimeProfile.spurious_start_time = System.nanoTime();
			allSplittingPoints = findAllSplittingPoints();
			calCETransProbs(te);
			testedTransProbInCps = ht.getTestedTransitionProb(te, this);
			sortedSplittingPoints = findSplitingStates(pm);
			TimeProfile.spurious_end_time = System.nanoTime();
			TimeProfile.spurious_check_times.add(TimeProfile.nanoToSeconds(TimeProfile.spurious_end_time
					-TimeProfile.spurious_start_time));
		}
	}

	private List<SplittingPoint> findAllSplittingPoints() {
		List<SplittingPoint> sps = new ArrayList<>();
		for(int j=0; j<counterPaths.size(); j++){
			CounterexamplePath cp = counterPaths.get(j);
			for(int i=0; i<cp.getCounterPath().size()-1; i++){
				int currentid = cp.getCounterPath().get(i);
				int nextid = cp.getCounterPath().get(i+1);
				SplittingPoint sp = new SplittingPoint(currentid, nextid);
				boolean flag = false;
				for(SplittingPoint asp : sps){
					if(asp.equals(sp)){
						flag = true;
						break;
					}
				}
				if(!flag){
					sps.add(sp);
				}
			}
		}
		return sps;
	}

	public List<SplittingPoint> getSortedSplittingPoints() {
		return sortedSplittingPoints;
	}

	public boolean checkMembership(List<String> sample) throws FileNotFoundException, ClassNotFoundException, IOException{ // check if a sampled abstract path is in counterexample
		for(CounterexamplePath cp : counterPaths){
			List<Integer> state_path = getStatePath(sample, pm);
			if(state_path.equals(cp.getCounterPath())){
				cp.concretePathCount++;
				return true;
			}
		}
		return false;
	}
	
	// given a trace, get its abstract state trace
	private List<Integer> getStatePath(List<String> sample, PrismModel pm) {
		
		List<Integer> state_trace = new ArrayList<Integer>();
		int next_symbol_index = -1;
		int current_state_index = 1;
		PrismState current_state = pm.getPrismStates().get(current_state_index-1);
		state_trace.add(pm.getPrismStates().get(current_state_index-1).getId());
		
		for(int i=0; i<sample.size()-1; i++){
			String tmps = sample.get(i+1);
			List<String> next_symbols = current_state.getSigmas();
			next_symbol_index = StringUtil.getStringIndex(tmps, next_symbols);
			if(next_symbol_index==-1){
				return state_trace; // return, new transition happens
			}
			else{
				current_state = current_state.getNextStates().get(next_symbol_index);
				state_trace.add(current_state.getId());
			}
		}
		return state_trace;
	}

	public List<CounterexamplePath> getCounterPaths() {
		return counterPaths;
	}

	public PrismModel getPrismModel(){
		return pm;
	}

	private void calCETransProbs(TestEnvironment te) throws IOException{
		for(SplittingPoint sp : allSplittingPoints){
			transProbInCps.add(calTransitionProb(sp, pm));
		}
		testedTransProbInCps = ht.getTestedTransitionProb(te, this);
	}

	public List<SplittingPoint> getAllSplittingPoints() {
		return allSplittingPoints;
	}

	@Override
	public List<SplittingPoint> findSplitingStates(PrismModel pm) {
		Map<SplittingPoint, Double> probDeviation = new HashMap<SplittingPoint, Double>();
		List<SplittingPoint> sps = new ArrayList<>();

		for(int i=0; i<transProbInCps.size(); i++){
			double deviation = transProbInCps.get(i) - testedTransProbInCps.get(i);
//			deviation = deviation * transProbInCps.get(i); // take the original transition probability into account, the smaller the better
//			if(deviation==0){ // exclude splitting point with probability 0
//				continue;
//			}
			probDeviation.put(allSplittingPoints.get(i), deviation);
		}
		System.out.println("- Probability deviation of splitting point: " + probDeviation);

		LinkedHashMap<SplittingPoint, Double> sortedMap = 
				(LinkedHashMap<SplittingPoint, Double>) MapUtil.sortByValue(probDeviation);
		for(SplittingPoint sp : sortedMap.keySet()){
			sps.add(sp);
		}
		
		List<SplittingPoint> finalsps = new ArrayList<>();
//		if(Config.LOOP_FIRST){
//			// put splitting points of loops in front
//			List<Integer> loops = new ArrayList<>();
//			for(int i=0; i<sps.size(); i++){ // find looping splitting point 
//				SplittingPoint sp = sps.get(i);
//				if(sp.getCurrentStateId()==sp.getNextStateId()){
//					loops.add(i);
//					finalsps.add(sp);
//				}
//			}
//			
//			int tmp = 0;
//			for(int k=0; k<loops.size(); k++){
//				int j = loops.get(k);
//				sps.remove(j-tmp);
//				tmp ++;
//			}
//		}
		finalsps.addAll(sps);
		System.out.println("- Maximum deviation: " + probDeviation.get(sps.get(0)));
		return finalsps;
	}

	private double calTransitionProb(SplittingPoint sp, PrismModel pm){
		double tp = 0;
		PrismState curPS = pm.getPrismStates().get(sp.getCurrentStateId()-1);
		for(int j=0; j<curPS.getNextStates().size(); j++){
			if(curPS.getNextStates().get(j).getId()==sp.getNextStateId()){
				tp = curPS.getTransitionProb().get(j);
				break;
			}
		}
		return tp;
	}

	public List<Double> calTransitionProb(CounterexamplePath cp, PrismModel pm) {
		List<Double> transProb = new ArrayList<>();
		List<Integer> cpPath = cp.getCounterPath();

		for(int i=0; i<cp.getCounterPath().size()-1; i++){
			PrismState curPS = pm.getPrismStates().get(cpPath.get(i)-1);
			for(int j=0; j<curPS.getNextStates().size(); j++){
				if(curPS.getNextStates().get(j).getId()==cpPath.get(i+1)){
					transProb.add(curPS.getTransitionProb().get(j));
					break;
				}
				if(j==curPS.getNextStates().size()-1){
					transProb.add(0.0);
				}
			}
		}
		return transProb;
	}

}
