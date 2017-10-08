package io.github.wang_jingyi.ZiQian.swat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.wang_jingyi.ZiQian.main.AlgoProfile;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.refine.SplitPointFinder;
import io.github.wang_jingyi.ZiQian.refine.SplittingPoint;
import io.github.wang_jingyi.ZiQian.utils.MapUtil;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

public class SAnalyzer implements SplitPointFinder{

	List<String> testing_log;
	List<Double> transProbInCps = new ArrayList<>();
	List<Double> testedTransProbInCps = new ArrayList<>();
	List<SplittingPoint> all_sps = new ArrayList<>();
	List<SplittingPoint> sortedSplittingPoints; // splitting point with probability deviation 0 is not included
	
	public SAnalyzer(List<String> testing_log) {
		this.testing_log = testing_log;
	}

	@Override
	public List<SplittingPoint> findSplitingStates(PrismModel pm) {
		return findAllSplittingPoints(pm);
	}

	private List<SplittingPoint> findAllSplittingPoints(PrismModel pm) {
		List<PrismState> all_states = pm.getPrismStates();

		for(int j=1; j<all_states.size(); j++){ // dummy initial state is excluded
			PrismState current_state = all_states.get(j);
			for(int i=1; i<current_state.getNextStates().size(); i++){
				PrismState next_state = current_state.getNextStates().get(i);
				SplittingPoint sp = new SplittingPoint(current_state.getId(), next_state.getId());
				boolean flag = false;
				for(SplittingPoint asp : all_sps){
					if(asp.equals(sp)){
						flag = true;
						break;
					}
				}
				if(!flag){
					all_sps.add(sp);
					transProbInCps.add(current_state.getTransitionProb().get(i)); // record the transition probabilities
				}
			}
		}
		testedTransProbInCps = calculateTestedTranstionProb(pm);
		
		Map<SplittingPoint, Double> probDeviation = new HashMap<SplittingPoint, Double>();
		
		for(int i=0; i<transProbInCps.size(); i++){
			double deviation = transProbInCps.get(i) - testedTransProbInCps.get(i);
//			deviation = deviation * transProbInCps.get(i); // take the original transition probability into account, the smaller the better
			if(deviation==1 || deviation<0){ // exclude splitting point with probability 0
				continue;
			}
			probDeviation.put(all_sps.get(i), deviation);
		}
//		System.out.println("- Probability deviation of splitting point: " + probDeviation);
		
		LinkedHashMap<SplittingPoint, Double> sortedMap = 
				(LinkedHashMap<SplittingPoint, Double>) MapUtil.sortByValue(probDeviation);
		List<SplittingPoint> sps = new ArrayList<>();
		for(SplittingPoint sp : sortedMap.keySet()){
			sps.add(sp);
		}
		
		List<SplittingPoint> finalsps = new ArrayList<>();
		if(AlgoProfile.loop_first){
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
//		finalsps.remove(0);
//		System.out.println("- Maximum deviation: " + probDeviation.get(sps.get(0)));
		return finalsps;
	}

	private List<Double> calculateTestedTranstionProb(PrismModel pm){

		List<Double> transProb = new ArrayList<>();

		Map<Integer, Integer> startingStateCounts = new HashMap<Integer, Integer>();
		Map<SplittingPoint, Integer> spCounts = new HashMap<>();

		for(SplittingPoint sp : all_sps){
			if(!startingStateCounts.containsKey(sp.getCurrentStateId())){
				startingStateCounts.put(sp.getCurrentStateId(), 0);
			}
			spCounts.put(sp, 0);
		}
		
		PrismState currentPS = null;
		int start = 1;
		for(int i=1; i<testing_log.size(); i++){
			for(PrismState state : pm.getPrismStates()){
				if(StringUtil.isSuffix(state.getLabel(), testing_log.subList(0, i))){
					currentPS = state;
					start = i;
					break;
				}
			}
		}
		
		for(int i=start+1; i<testing_log.size()-1; i++){
			int currentID = currentPS.getId();
			if(startingStateCounts.containsKey(currentID)){
				int currentCount = startingStateCounts.get(currentID);
				currentCount ++;
				startingStateCounts.put(currentID, currentCount);
			}
			else{
				startingStateCounts.put(currentID, 1);
			}

			int nextStateID = StringUtil.getStringIndex(testing_log.get(i), currentPS.getSigmas());
			if(nextStateID==-1){
				continue;
			}
			PrismState nextPS = currentPS.getNextStates().get(nextStateID);
			int nextID = nextPS.getId();

			for(SplittingPoint sp : all_sps){
				if(sp.getCurrentStateId()==currentID && sp.getNextStateId()==nextID){
					int spCount = spCounts.get(sp);
					spCount++;
					spCounts.put(sp, spCount);
				}
			}
			currentPS = nextPS;
		}

		for(SplittingPoint sp : all_sps){
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


}
