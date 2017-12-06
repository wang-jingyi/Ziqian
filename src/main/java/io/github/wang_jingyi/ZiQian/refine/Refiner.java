package io.github.wang_jingyi.ZiQian.refine;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.data.VariablesValue;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.main.AlgoProfile;
import io.github.wang_jingyi.ZiQian.main.GlobalConfigs;
import io.github.wang_jingyi.ZiQian.main.SwatConfig;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.NumberUtil;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;
import libsvm.LibSVM;
import libsvm.svm_parameter;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.normalize.NormalizeMidrange;

public class Refiner{

	private List<SplittingPoint> spuriousTransitions;
	private VariablesValueInfo vvi;
	private List<Predicate> predicates;
	private PrismModel pm;
	private int negCount;
	private int posCount;
	private boolean terminateSample;
	private boolean selectiveDataCollection;
	private double classifyAccuracy;
	private List<String> allVars = AlgoProfile.vars;
	private List<String> newVars = new ArrayList<>();

	public Refiner(List<SplittingPoint> sps, VariablesValueInfo vvi, List<Predicate> pres, PrismModel pm) {
		super();
		this.spuriousTransitions = sps;
		this.vvi = vvi;
		this.predicates = pres;
		this.pm = pm;
	}

	public Refiner(List<SplittingPoint> sps, VariablesValueInfo vvi, List<Predicate> pres, PrismModel pm, boolean ts, boolean sdc) {
		super();
		this.spuriousTransitions = sps;
		this.vvi = vvi;
		this.predicates = pres;
		this.pm = pm;
		this.terminateSample = ts;
		this.selectiveDataCollection = sdc;
	}


	public LearnedPredicate refine() throws FileNotFoundException{
		for(int i=0; i<spuriousTransitions.size(); i++){
			SplittingPoint current_sp = spuriousTransitions.get(i);
			System.out.println("--- current splitting point: " + current_sp.toString());
			Dataset ds = null;
			if(vvi.getVarsValues().size()==1){
				ds = collectDataSetFromSingleTrace(vvi.getVarsValues().get(0), predicates, current_sp, pm);
			}
			else{
				ds = collectDataSet(vvi.getVarsValues(), predicates, current_sp, pm);
			}

			if(ds==null){ // if the dataset is null, i.e., only has one label, then it's not classifiable
				continue;
			}
			LearnedPredicate newPredicate = findSplitPredicates(ds);
			if(newPredicate!=null){
				return newPredicate;
			}
		}
		return null;
	}

	private LearnedPredicate findSplitPredicates(Dataset ds) throws FileNotFoundException {
		boolean oneLabel = false;
		int dsSize = ds.size();
		if(negCount==dsSize || posCount==dsSize){
			oneLabel = true;
		}

		if(oneLabel==true){
			return	null;
			//unsupervisedClassify(ds);
		}
		else{
			return supervisedClassify(ds);
		}
	}

	//	private Dataset collectDataFromPaths(List<String> pathsDirs,
	//			List<Predicate> predicates, SplittingPoint sp, PrismModel pm) throws IOException {
	//
	//		List<List<VariablesValue>> vvs = new ArrayList<>(); // extract all variables values
	//		for(String pathsDir : pathsDirs){
	//			vvs.addAll(PrismPathData.extractMEData(pathsDir, AlgoProfile.vars, Config.DATA_SIZE));
	//		}
	//		return collectDataSet(vvs, predicates, sp, pm);
	//	}

	private Dataset collectDataSetFromSingleTrace(List<VariablesValue> vvl, List<Predicate> predicates, SplittingPoint sp, PrismModel pm){

		// reset postive/negative count 
		posCount = 0;
		negCount = 0;

		Dataset ds = new DefaultDataset();
		int featureSize = AlgoProfile.vars.size();
		PredicateAbstraction pa = new PredicateAbstraction(predicates);

		List<String> testing_log = pa.abstractList(vvl); // abstract list of values
		List<Integer> id_list = new ArrayList<>();

		PrismState currentPS = null;
		PrismState state = pm.getPrismStates().get(sp.getCurrentStateId()-1);
		int start = -1;
		for(int i=1; i<=testing_log.size(); i++){
			if(StringUtil.isSuffix(state.getLabel(), testing_log.subList(0, i))){
				id_list.add(state.getId());
				currentPS = state;
				start = i;
				break;
			}
			id_list.add(-1);
		}

		assert start!=-1 : "====== state is not found in the log ======";

		for(int i=start; i<testing_log.size()-1; i++){

			int nextStateID = StringUtil.getStringIndex(testing_log.get(i), currentPS.getSigmas());
			assert nextStateID!=-1 : "====== new transition happens ======";
			PrismState nextPS = currentPS.getNextStates().get(nextStateID);
			int nextID = nextPS.getId();
			id_list.add(nextID);
			currentPS = nextPS;
		}

		for(int i=0; i<id_list.size()-1; i++){

			if(id_list.get(i)==sp.getCurrentStateId() && id_list.get(i+1)==sp.getNextStateId()){ // positive instance
				double[] values = new double[featureSize];
				for(int j=0; j<vvl.get(i).getValues().size(); j++){ // store the concrete values of the state
					values[j] = vvl.get(i).getValues().get(j).getRawDoubleValue();
				}
				Instance ins = new DenseInstance(values,"positive");
				ds.add(ins);
				posCount++;
			}
			else{
				double[] values = new double[featureSize];
				for(int j=0; j<vvl.get(i).getValues().size(); j++){ // store the concrete values of the state
					values[j] = vvl.get(i).getValues().get(j).getRawDoubleValue();
				}
				Instance ins = new DenseInstance(values,"negative");
				ds.add(ins);
				negCount++; // update negative instance count
			}
		}
		return ds;

	}

	private Dataset collectDataSet(List<List<VariablesValue>> vvs, List<Predicate> predicates, SplittingPoint sp, PrismModel pm){

		// reset postive/negative count 
		posCount = 0;
		negCount = 0;

		Dataset ds = new DefaultDataset();
		int featureSize = AlgoProfile.vars.size();

		int currentstate = sp.getCurrentStateId(); // start state of the spurious transition
		int nextstate = sp.getNextStateId(); // end state of the spurious transition

		String currentString = pm.getPrismStates().get(currentstate-1).getCurrentState(); // last observation of the start state
		String nextString = pm.getPrismStates().get(nextstate-1).getCurrentState(); // last observation of the end state

		PredicateAbstraction pa = new PredicateAbstraction(predicates);

		for(List<VariablesValue> vvl : vvs){
			int pcount = 0;
			int ncount = 0;
			List<String> vvls = pa.abstractList(vvl); // abstract list of values
			for(int i=0; i<vvls.size(); i++){
				String abs_s = vvls.get(i);
				if(!abs_s.equals(currentString)){
					continue;
				}

				double[] values = new double[featureSize];
				for(int j=0; j<vvl.get(i).getValues().size(); j++){ // store the concrete values of the state
					values[j] = vvl.get(i).getValues().get(j).getRawDoubleValue();
				}

				if(i==vvls.size()-1){ // the last observation must be a negative instance
					if(terminateSample){ 
						// sample will terminate like crowds/nand; 
						// if not terminate, we dont know whether it's positive or negative
						Instance ins = new DenseInstance(values,"negative");
						ds.add(ins);
						negCount ++;
					}
					continue;
				}

				if(vvls.get(i+1).equals(nextString)){ 

					if(selectiveDataCollection){
						if(pcount<1){
							// check if the next state is according to the counterexample path
							Instance ins = new DenseInstance(values,"positive");
							ds.add(ins);
							pcount ++;
							posCount++; // update positive instance count
						}
					}
					else{
						Instance ins = new DenseInstance(values,"positive");
						ds.add(ins);
						posCount++; // update positive instance count
					}
				}
				else{
					if(selectiveDataCollection){
						if(ncount<1){
							Instance ins = new DenseInstance(values,"negative");
							ds.add(ins);
							ncount++;
							negCount++; // update negative instance count
						}
					}
					else{
						Instance ins = new DenseInstance(values,"negative");
						ds.add(ins);
						negCount++; // update negative instance count
					}
				}
			}
		}

		//		System.out.println("- Instance in the dataset: " + ds.size() + ":   postive instance: " + posCount + ",   negative instance: " + negCount);
		return ds;

	}

	//	private List<Integer> selectWeightElements(double[] weights){
	//		List<Integer> ind = new ArrayList<>();
	//		for(int i=0; i<weights.length; i++){
	//			if(weights[i]!=0){
	//				ind.add(i);
	//			}
	//		}
	//		return ind;
	//	}

	private LearnedPredicate supervisedClassify(Dataset ds) throws FileNotFoundException {

		// dataset normalization between [0,1]
		//				System.out.println("- Normalize dataset");
		//				NormalizeMidrange dnm = new NormalizeMidrange(0.5, 1);
		//				dnm.filter(ds);

		LibSVM svm = new LibSVM();
		svm_parameter svm_para = (svm_parameter) svm.getParameters().clone();

		//		FeatureScoring fs = new GainRatio();
		//		RankingFromScoring rfs = new RankingFromScoring(fs);
		//		rfs.build(ds);

		svm_para.kernel_type = 2;
		svm_para.gamma = 10;
		svm_para.C = 1;

		System.out.println("- Kernel type: " + svm_para.kernel_type);
		svm.setParameters(svm_para);


		// output collected data for reference
		StringBuilder sb = new StringBuilder();
		for(Instance ins : ds){
			sb.append(ins.toString() + "\n");
		}
		FileUtil.writeStringToFile(GlobalConfigs.PROJECT_ROOT+"/tmp/collected_data.txt", sb.toString());

		svm.buildClassifier(ds);

		StringBuilder svm_log = new StringBuilder();
		svm_log.append("- Variables involved in the classifier: \n");
		svm_log.append(allVars.toString() + "\n");
		svm_log.append("- Weights of the classifier: \n");
		svm_log.append(NumberUtil.ArrayToString(svm.getWeights()));
		FileUtil.writeStringToFile(SwatConfig.OUTPUT_MODEL_PATH+"/swat" + (AlgoProfile.iterationCount+1)+"_classifier.txt", svm_log.toString());


		//		double[] weights = svm.getWeights();
		//		List<Integer> ind = selectWeightElements(weights);
		//		for(int i=0; i<ind.size(); i++){
		//			newVars.add(allVars.get(ind.get(i)));
		//		}

		int rightCount = 0;
		int sumCount = 0;
		for(Instance ins : ds){
			if(svm.classify(ins).equals(ins.classValue())){
				rightCount ++;
			}
			sumCount++;
		}
		classifyAccuracy = (double)rightCount/sumCount;
		System.out.println("- Classification accuracy : " + classifyAccuracy);

		if(rightCount==negCount || rightCount==posCount){ // all data are in one side, fail to classify
			System.out.println("=== Fail to find a linear splitting predicate ===");
			return null;
		}

		LearnedPredicate newPredicate = new LearnedPredicate(svm,"learned_predicate_" + AlgoProfile.iterationCount, newVars);
		return newPredicate;
	}

}
