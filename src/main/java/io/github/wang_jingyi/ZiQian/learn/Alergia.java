package io.github.wang_jingyi.ZiQian.learn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.main.AlgoProfile;
import io.github.wang_jingyi.ZiQian.main.GlobalConfigs;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.IntegerUtil;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

public class Alergia implements LearningDTMC{

	private int numOfStates;
	private double alpha = 2; // default threshold
	private double selectionCriterion; // BIC score in this learning algorithm
	private PrismModel probabilisticPrefixAutomata = new PrismModel(); // this will be used for generating .pm file later
	private DataPrefix dp;
	private DataPrefix origdp;
	private List<Boolean> merged = new ArrayList<Boolean>();// to record whether a state is merged or not
	private List<Integer> validStateIndex = new ArrayList<Integer>(); // indexes of valid states
	private MergeRecord record;
	
	public Alergia() {
		super();
	}
	
	public Alergia(double alpha){
		this.alpha = alpha;
	}
	
	@Override
	public void learn(Input data) throws FileNotFoundException, IOException, ClassNotFoundException {
		
		if(AlgoProfile.prefixCalculated==false || AlgoProfile.newIteration==true){
			dp = new DataPrefix(data);
			dp.execute();
			dp.printPrefixTreeInfo();
			FileUtil.writeObject(GlobalConfigs.PROJECT_ROOT + "/tmp/dataPrefix.ser", dp);
			AlgoProfile.prefixCalculated = true;
			AlgoProfile.newIteration = false;
		}
		else{
			dp = (DataPrefix)FileUtil.readObject(GlobalConfigs.PROJECT_ROOT + "/tmp/dataPrefix.ser");
		}
		origdp = dp.clone();
		record = new MergeRecord(origdp);
		alergia(data);
		getAutomataSize();
		calculateCriterion(data);
	}

	private void getAutomataSize() { // valid state indexes are updated after this
		List<Integer> queue = new ArrayList<Integer>();
		queue.add(0);
		validStateIndex.add(0);
		while(queue.size()!=0){
			int st = queue.get(0);
			queue.remove(0);
			for(int k=0; k<dp.getNextSymbolIndex().get(st).size(); k++){
				int next = dp.getNextSymbolIndex().get(st).get(k);
				if(next!=-1 && !validStateIndex.contains(next)){
					validStateIndex.add(next);
					queue.add(next);
				}
			}
		}
		Collections.sort(validStateIndex);
		numOfStates = validStateIndex.size();
		System.out.println("--- Number of states in the learned model: " + numOfStates);
	}

	private void calculateCriterion(Input data) {
		double dataLogLikelihood = 0;
		for(List<String> oneObservation : data.getObservations()){
			dataLogLikelihood += Math.log(calculateStringListPb(data, oneObservation));
		}
		selectionCriterion = dataLogLikelihood - 0.5 * numOfStates * (data.getAlphabet().size()-1) * Math.log(data.getDataSize());// state number needs to substract 1 for empty state
	}

	private double calculateStringListPb(Input data, List<String> slist){
		double slistPb = 1;
		int currentStateIndex = 0;
		for(int i=0; i<slist.size(); i++){
			double nextTransitionPb = 0;
			String nextSymbol = slist.get(i);
			int alphaInd = getAlphabetIndex(data, nextSymbol);
			assert alphaInd!=-1 : "some symbol not in alphabet";
			nextTransitionPb = (double)dp.getNextSymbolFrequency().get(currentStateIndex).get(alphaInd)/dp.getSortedPrefixCounts().get(currentStateIndex);
			slistPb = slistPb * nextTransitionPb;
			currentStateIndex = dp.getNextSymbolIndex().get(currentStateIndex).get(alphaInd); // update current state index
			if(currentStateIndex==-1){
				slistPb = 0;
				return slistPb;
			}
		}
		double fp = (double)dp.getSortedPrefixFinalCount().get(currentStateIndex)/dp.getSortedPrefixCounts().get(currentStateIndex); 
		slistPb = slistPb * fp;
		return slistPb;
	}

	public static int getAlphabetIndex(Input data, String s){
		for(int i=0; i<data.getAlphabet().size(); i++){
			if(s.equals(data.getAlphabet().get(i))){
				return i;
			}
		}
		return -1;
	}

	private void alergia(Input data) {
		for(int i=0; i<dp.getSortedPrefixes().size(); i++){// initialize merged
			merged.add(false);
		}
		List<Integer> red = new ArrayList<Integer>(); // indexes of red states
		List<Integer> blue = new ArrayList<Integer>(); // indexes of blue states
		for(String s : data.getAlphabet()){	// initialization of red and blue states
			List<String> tmp = new ArrayList<String>();
			tmp.add(s);
			if(origdp.isInSortedPrefix(tmp)!=-1){
				HashSet<Integer> newGroup = new HashSet<>();
				red.add(origdp.isInSortedPrefix(tmp));
				newGroup.add(origdp.isInSortedPrefix(tmp));
				record.getGroups().add(newGroup);
			}
		}
		for(int rind : red){
			for(String s : data.getAlphabet()){ 
				List<String> next = StringUtil.cloneList(origdp.getSortedPrefixes().get(rind));
				next.add(s);
				if(origdp.isInSortedPrefix(next)>=0){ // is included in sorted prefixes
					blue.add(origdp.isInSortedPrefix(next));
				}
			}
		}

		while(blue.size()>0){ // j indexes blue states
			Collections.sort(blue);
			int j = blue.get(0);
			blue.remove(0); // remove from blue
			List<Integer> redWithSameLastSymbol = new ArrayList<Integer>();
			
			for(int i : red){
				if(StringUtil.getLastString(dp.getSortedPrefixes().get(i)).equals(StringUtil.getLastString(dp.getSortedPrefixes().get(j)))){
					redWithSameLastSymbol.add(i);
				}
			}
			
			for(int i : redWithSameLastSymbol){
				if(Compatiable(j,i,data)){  // here test should be conducted on original dp
					merged.set(j, true);
					merge(j,i);
					updateRecord(j,i);
					break;
				}
			}
			if(merged.get(j)==false){
				HashSet<Integer> newGroup = new HashSet<>();
				red.add(j);
				newGroup.add(j);
				record.getGroups().add(newGroup);
			}
			
			List<Integer> nextRED = new ArrayList<Integer>();
			for(int rr : red){
				for(int kk=0; kk<dp.getNextSymbolIndex().get(rr).size(); kk++){
					int nr = dp.getNextSymbolIndex().get(rr).get(kk);
					if(nr!=-1 && !IntegerUtil.isInList(nr, nextRED)){
						nextRED.add(nr);
					}
				}
			}
			
			for(int rr : nextRED){
				if(!IntegerUtil.isInList(rr, red) && !IntegerUtil.isInList(rr, blue)){
					blue.add(rr);
				}
			}
		}
	}

	private void updateRecord(int j, int i) { // add j to i's group
		for(HashSet<Integer> group : record.getGroups()){
			if(group.contains(i)){
				group.add(j);
			}
		}
	}

	private void merge(int j, int i) { // merge the j-th prefix to i-th

		List<Integer> red = new ArrayList<Integer>();
		List<Integer> blue = new ArrayList<Integer>();

		// update next state indexes
		for(int k=0; k<dp.getPrefixesTotalNum(); k++){ // direct all prefixes to j to i instead
			for(int ind=0; ind<dp.getNextSymbolIndex().get(k).size(); ind++){
				if(dp.getNextSymbolIndex().get(k).get(ind)==j){
					dp.getNextSymbolIndex().get(k).set(ind, i);
				}
			}
		}

		red.add(i);
		blue.add(j);

		while(blue.size()!=0){
			int qr = red.get(0);
			int qb = blue.get(0);
			red.remove(0);
			blue.remove(0);

			// update final state frequency
			int ifinalcount = dp.getSortedPrefixFinalCount().get(qr);
			int jfinlacount = dp.getSortedPrefixFinalCount().get(qb);
			dp.getSortedPrefixFinalCount().set(qr, ifinalcount+jfinlacount);
			
			List<Integer> sameTran = new ArrayList<Integer>();
//			List<Integer> diffTran = new ArrayList<Integer>();
			
			for(int k=0; k<dp.getNextSymbolIndex().get(0).size(); k++){
				if(dp.getNextSymbolIndex().get(qr).get(k)!=-1 && dp.getNextSymbolIndex().get(qb).get(k)!=-1){
					sameTran.add(k);
					int ni = dp.getNextSymbolFrequency().get(qr).get(k);
					int nj = dp.getNextSymbolFrequency().get(qb).get(k);
					dp.getNextSymbolFrequency().get(qr).set(k, ni+nj);
				}
				if(dp.getNextSymbolIndex().get(qr).get(k)==-1 && dp.getNextSymbolIndex().get(qb).get(k)!=-1){
//					diffTran.add(k);
					int nj = dp.getNextSymbolFrequency().get(qb).get(k);
					int jn = dp.getNextSymbolIndex().get(qb).get(k);
					dp.getNextSymbolFrequency().get(qr).set(k, nj);
					dp.getNextSymbolIndex().get(qr).set(k, jn);
				}
			}
			
			for(int r : sameTran){ 
				red.add(dp.getNextSymbolIndex().get(qr).get(r));
			}
			for(int b : sameTran){
				blue.add(dp.getNextSymbolIndex().get(qb).get(b));
			}
		}
		updatePrefixCount();
	}

	private void updatePrefixCount() {
		for(int i=0; i<dp.getPrefixesTotalNum(); i++){
			int prefixTotalNum = 0;
			for(int j=0; j<dp.getNextSymbolFrequency().get(i).size(); j++){
				prefixTotalNum += dp.getNextSymbolFrequency().get(i).get(j);
			}
			prefixTotalNum += dp.getSortedPrefixFinalCount().get(i);
			dp.getSortedPrefixCounts().set(i, prefixTotalNum);
		}
	}
	
	private boolean Compatiable(int j, int i, Input data) { // check if the i-th and j-th(blue) prefix are compatible
			double threshold = logNumOfStringWithPrefix(i) + logNumOfStringWithPrefix(j); // correct calculation
			return compatibleRecursive(i,j,1,1,threshold,data);
	}

	private boolean compatibleRecursive(int i, int j, double pr, double pb,
			double threshold, Input data) {
		double pfr = 0; // final state probability
		double pfb = 0;
		List<Double> tranPR = new ArrayList<Double>(); // next state transition probability for red state
		List<Double> tranPB = new ArrayList<Double>(); // for blue
		List<Integer> nextR = new ArrayList<Integer>(); // next state transition index for red
		List<Integer> nextB = new ArrayList<Integer>(); // for blue

		if(Math.max(pr, pb)<=threshold){
			return true;
		}
		if((pr>threshold && pb==0) || (pb>threshold && pr==0)){
			return false;
		}

		if(pr==0){
			pfr = 0;
		}
		else{
			pfr = pr*(double)origdp.getSortedPrefixFinalCount().get(i)/origdp.getSortedPrefixCounts().get(i);
			for(int k=0; k<data.getAlphabet().size(); k++){
				if(i==-1){
					nextR.add(-1);
					tranPR.add((double) 0);
				}
				else{
					nextR.add(dp.getNextSymbolIndex().get(i).get(k));
					double ntran = (double)origdp.getNextSymbolFrequency().get(i).get(k)/origdp.getSortedPrefixCounts().get(i);
					tranPR.add(ntran);
				}

			}
		}
		

		if(pb==0){
			pfb = 0;
		}
		else{
			pfb = pb*(double)origdp.getSortedPrefixFinalCount().get(j)/origdp.getSortedPrefixCounts().get(j);
			for(int k=0; k<data.getAlphabet().size(); k++){
				if(j==-1){
					nextB.add(-1);
					tranPB.add((double) 0);
				}
				else{
					nextB.add(origdp.getNextSymbolIndex().get(j).get(k));
					double ntran = (double)origdp.getNextSymbolFrequency().get(j).get(k)/origdp.getSortedPrefixCounts().get(j);
					tranPB.add(ntran);
				}

			}
		}

		if(Math.abs(pfr-pfb)>threshold){
			return false;
		}
		for(int k=0; k<data.getAlphabet().size(); k++){
			if(nextR.get(k)!=-1 || nextB.get(k)!=-1){ // "or" or "and" here, RTC
				double newpr = pr * tranPR.get(k);
				double newpb = pb * tranPB.get(k);
				if(!compatibleRecursive(nextR.get(k), nextB.get(k), newpr, newpb, threshold, data)){
					return false;
				}
			}
		}
		return true;
	}

	private double logNumOfStringWithPrefix(int i) {
		int num = origdp.getSortedPrefixCounts().get(i);
		double result = Math.sqrt((double) 6 * alpha * Math.log(num)/num);
		return result;
	}
	
	@Override
	public void PrismModelTranslation(Input data, List<Predicate> predicates, String modelName) {
		List<PrismState> prismStates = new ArrayList<PrismState>(); // list of prism states
		List<PrismState> initialStates = new ArrayList<PrismState>(); // list of initial states
		List<List<Integer>> nextStateIdx = new ArrayList<List<Integer>>();
		List<List<Integer>> nextStateFrq = new ArrayList<List<Integer>>();
		
		for(int i=0; i<validStateIndex.size(); i++){
			nextStateIdx.add(dp.getNextSymbolIndex().get(validStateIndex.get(i)));
			nextStateFrq.add(dp.getNextSymbolFrequency().get(validStateIndex.get(i)));
		}
		
		
//		List<String> emptylabel = new ArrayList<>(); // add empty state first with id 1
//		emptylabel.add("empty");
//		PrismState emptyps = new PrismState(1, emptylabel);
//		prismStates.add(emptyps);
//		
//		List<Integer> initPSid = new ArrayList<>();
		
		
		List<Integer> mask = new ArrayList<Integer>(); // mask of new state id, starting from 2, because prism models start from state 1
		for(int i=1; i<=validStateIndex.size(); i++){
			mask.add(i);
		}
		
		for(List<Integer> li : nextStateIdx){
			for(int l=0; l<li.size(); l++){
				int lind = IntegerUtil.indexInList(li.get(l), validStateIndex);
				if(lind!=-1){
					li.set(l, mask.get(lind));
				}
			}
		}
		
		for(int i=0; i<mask.size(); i++){
			int origID = validStateIndex.get(i);
			for(HashSet<Integer> group : record.getGroups()){
				if(group.contains(origID)){
					record.getIdMaps().put(mask.get(i), group);
					break;
				}
			}
			PrismState ps = new PrismState(mask.get(i), dp.getSortedPrefixes().get(origID)); // state id starts from 1
			prismStates.add(ps);
//			if(IntegerUtil.isInList(origID, dp.getInitStates())){ // add initial states after empty states
//				initPSid.add(mask.get(i));
//			}
		}
		
		// update next states things for empty state
//		for(int i : initPSid){
//			emptyps.getNextStates().add(prismStates.get(i-1)); // add next states for empty state
//		}
//		emptyps.setTransitionProb(dp.getInitDistribution()); // set transition probability
//		emptyps.setSigmas(dp.getInitSigmas());
		
		
		for(int i=0; i<validStateIndex.size(); i++){
			int sumFrq = 0;
			for(int j=0; j<data.getAlphabet().size(); j++){
				sumFrq += nextStateFrq.get(i).get(j); // note that here, the final state count is not calculated in
				// the so-called re-normalization in the original paper
			}
			PrismState ps = prismStates.get(i);
			for(int j=0; j<data.getAlphabet().size(); j++){
				if(nextStateIdx.get(i).get(j)!=-1){
					ps.getSigmas().add(data.getAlphabet().get(j));
					int symTranNum = nextStateFrq.get(i).get(j);
					double transPb = (double)symTranNum/sumFrq;
					String transPbInString = Integer.toString(symTranNum)+"/"+Integer.toString(sumFrq);
					ps.getTransitionProb().add(transPb);
					ps.getTranProbInString().add(transPbInString);
					ps.getNextStates().add(prismStates.get(nextStateIdx.get(i).get(j)-1)); // next state id !
				}
				assert ps.getSigmas().size()==ps.getTransitionProb().size() : "sigma and transition probability size not equal";
			}
		}
		
		initialStates.add(prismStates.get(0)); // empty state as the initial state
		
		probabilisticPrefixAutomata.setPrismStates(prismStates);
		probabilisticPrefixAutomata.setInitialStates(initialStates);
		probabilisticPrefixAutomata.setPredicates(predicates);
		probabilisticPrefixAutomata.setNumOfPrismStates(numOfStates);
	}

	public PrismModel getPrismModel() {
		return probabilisticPrefixAutomata;
	}
	
	public MergeRecord getMergeRecord() {
		return record;
	}

	public int getNumOfStates() {
		return numOfStates;
	}

	public void setNumOfStates(int numOfStates) {
		this.numOfStates = numOfStates;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double epsilon) {
		this.alpha = epsilon;
	}

	public double getSelectionCriterion() {
		return selectionCriterion;
	}

	public void setSelectionCriterion(double selectionCriterion) {
		this.selectionCriterion = selectionCriterion;
	}

}
