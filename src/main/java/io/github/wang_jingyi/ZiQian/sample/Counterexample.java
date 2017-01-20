package io.github.wang_jingyi.ZiQian.sample;

import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.run.Config;
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
 * to find the state transition to split 
 * */

public class Counterexample implements SplitPointFinder{

	private List<CounterexamplePath> counterPaths;
	private int longestCounterPath;
	private PrismModel pm;
	private List<Double> transProbInCps = new ArrayList<>();
	private List<Double> testedTransProbInCps = new ArrayList<>();
	private TestEnvironment te;
	private HypothesisTest ht;
	private List<SplittingPoint> allSplittingPoints;
	private List<SplittingPoint> sortedSplittingPoints; // splitting point with probability deviation 0 is not included

	public Counterexample(PrismModel pm, List<CounterexamplePath> cps, TestEnvironment te, HypothesisTest ht) {
		this.pm = pm;
		this.counterPaths = cps;
		this.te = te;
		this.ht = ht;
		updateLongestPaths();
	}

	public Counterexample(PrismModel pm, String cpFilePath, TestEnvironment te, HypothesisTest ht) {
		this.pm = pm;
		this.counterPaths = CounterexampleOps.extractCounterexample(cpFilePath);
		this.te = te;
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

	public void analyze() throws ClassNotFoundException, IOException{
		if(ht.testHypothesis(ht.getProbBound(), this)){
			System.out.println("the property is voilated.");
			System.out.println("counterexample wrote to file."); // to do
			System.exit(0);
		}
		else{
			allSplittingPoints = findAllSplittingPoints();
			calCETransProbs();
			testedTransProbInCps = ht.getTestedTransitionProb(this);
			sortedSplittingPoints = findSplitingStates(pm);
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
			if(CheckValidity(cp, sample, pm)){
				cp.concretePathCount++;
				return true;
			}
		}
		return false;
	}

	public List<CounterexamplePath> getCounterPaths() {
		return counterPaths;
	}

	public PrismModel getPrismModel(){
		return pm;
	}

	private void calCETransProbs() throws IOException{
		for(SplittingPoint sp : allSplittingPoints){
			transProbInCps.add(calTransitionProb(sp, pm));
		}
		testedTransProbInCps = ht.getTestedTransitionProb(this);
	}

	public TestEnvironment getTestEnvironment() {
		return te;
	}

	private boolean CheckValidity(CounterexamplePath cp, List<String> pathData, PrismModel pm) throws FileNotFoundException, ClassNotFoundException, IOException{
		List<Integer> path = cp.getCounterPath();
		
		if(pathData.size()<=1){
			return false;
		}
		
		for(int i=0; i<path.size()-1; i++){
			String tmps = pathData.get(i+1);
			int j = StringUtil.getStringIndex(tmps, pm.getPrismStates().get(path.get(i)).getSigmas());
			if(j==-1){
				return false;
			}
			else{
				int curCount = cp.testedTransitionCount.get(i); // record transition appearance
				cp.testedTransitionCount.set(i,curCount+1);
				int k = pm.getPrismStates().get(path.get(i)).getNextStates().get(j).getId();
				if(k!=path.get(i)+1){
					return false;
				}
			}
		}
		return true;
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
			if(deviation==0){ // exclude splitting point with probability 0
				continue;
			}
			probDeviation.put(allSplittingPoints.get(i), deviation);
		}
		System.out.println("probability deviation of splitting point: " + probDeviation);

		LinkedHashMap<SplittingPoint, Double> sortedMap = (LinkedHashMap<SplittingPoint, Double>) MapUtil.sortByValue(probDeviation);
		for(SplittingPoint sp : sortedMap.keySet()){
			sps.add(sp);
		}
		
		List<SplittingPoint> finalsps = new ArrayList<>();
		if(Config.LOOP_FIRST){
			// put splitting points of loops in front
			List<Integer> loops = new ArrayList<>();
			for(int i=0; i<sps.size(); i++){ // find looping splitting point 
				SplittingPoint sp = sps.get(i);
				if(sp.getCurrentStateId()==sp.getNextStateId()){
					loops.add(i);
					finalsps.add(sp);
				}
			}
			
			int tmp = 0;
			for(int k=0; k<loops.size(); k++){
				int j = loops.get(k);
				sps.remove(j-tmp);
				tmp ++;
			}
		}
		finalsps.addAll(sps);

		System.out.println("splitting points: " + sps);
		System.out.println("maximum deviation: " + probDeviation.get(sps.get(0)));
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
