package io.github.wang_jingyi.ZiQian.refine;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.VariablesValue;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.sample.SplittingPoint;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.NumberUtil;
import libsvm.LibSVM;
import libsvm.svm_parameter;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

public class Refiner{

	private List<SplittingPoint> spuriousTransitions;
	private VariablesValueInfo vvi;
	private List<Predicate> predicates;
	private PrismModel pm;
	private int negCount;
	private int posCount;
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

	public LearnedPredicate refine() throws FileNotFoundException{
		for(int i=0; i<spuriousTransitions.size(); i++){
			System.out.println("current splitting point: " + spuriousTransitions.get(i).toString());
			Dataset ds = collectDataSet(vvi.getVarsValues(), predicates, spuriousTransitions.get(i), pm);
			//			ds = normalizeDataset(ds);
			if(ds==null){ // if the dataset is null, i.e., only has one label, then it's not classifiable
				continue;
			}
			LearnedPredicate newPredicate = findSplitPredicates(ds);
			if(newPredicate!=null){
				return newPredicate;
			}
		}
		System.out.println("cannot find a splitting predicate for all the spurious transitions, algorithm terminates.");
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
			//					unsupervisedClassify(ds);
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
					if(Config.TERMINATE_SAMPLE ){ 
						// sample will terminate like crowds/nand; 
						// if not terminate, we dont know whether it's positive or negative
						Instance ins = new DenseInstance(values,"negative");
						ds.add(ins);
						negCount ++;
					}
					continue;
				}

				if(vvls.get(i+1).equals(nextString)){ 

					if(Config.SELECTIVE_DATA_COLLECTION){
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
					if(Config.SELECTIVE_DATA_COLLECTION){
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

		System.out.println("instance in the dataset: " + ds.size());
		System.out.println("transition to split: " + sp.toString());
		return ds;

	}

	private List<Integer> selectWeightElements(double[] weights){
		List<Integer> ind = new ArrayList<>();
		for(int i=0; i<weights.length; i++){
			if(weights[i]!=0){
				ind.add(i);
			}
		}
		return ind;
	}

	private LearnedPredicate supervisedClassify(Dataset ds) throws FileNotFoundException {

		LibSVM svm = new LibSVM();
		svm_parameter svm_para = (svm_parameter) svm.getParameters().clone();

		//		svm_para.kernel_type = 0;
		//		svm_para.gamma = 0.1;
		//		svm_para.C = 10;

		System.out.println("kernel type: " + svm_para.kernel_type);
		svm.setParameters(svm_para);


		// output collected data for reference
		StringBuilder sb = new StringBuilder();
		for(Instance ins : ds){
			sb.append(ins.toString() + "\n");
		}
		FileUtil.writeStringToFile(Config.PROJECT_ROOT+"/tmp/collected_data.txt", sb.toString());

		svm.buildClassifier(ds);

		StringBuilder svm_log = new StringBuilder();
		svm_log.append("variables involved in the classifier: \n");
		svm_log.append(allVars.toString() + "\n");
		svm_log.append("weights of the classifier: \n");
		svm_log.append(NumberUtil.ArrayToString(svm.getWeights()));
		FileUtil.writeStringToFile(Config.OUTPUT_MODEL_PATH+"/" + Config.MODEL_NAME + (AlgoProfile.iterationCount+1)+"_classifier.txt", svm_log.toString());


		double[] weights = svm.getWeights();
		List<Integer> ind = selectWeightElements(weights);
		for(int i=0; i<ind.size(); i++){
			newVars.add(allVars.get(ind.get(i)));
		}

		int rightCount = 0;
		int sumCount = 0;
		System.out.println("calculate accuracy...");
		for(Instance ins : ds){
			if(svm.classify(ins).equals(ins.classValue())){
				rightCount ++;
			}
			sumCount++;
		}
		classifyAccuracy = (double)rightCount/sumCount;
		System.out.println("accuracy : " + classifyAccuracy);

		if(rightCount==negCount || rightCount==posCount){ // all data are in one side, fail to classify
			System.out.println("fail to find a splitting predicate...");
			return null;
		}

		LearnedPredicate newPredicate = new LearnedPredicate(svm,"learned_predicate_" + AlgoProfile.iterationCount, newVars);
		return newPredicate;
	}

	//	private Dataset normalizeDataset(Dataset ds){
	//		Instance maxValuesIns = DatasetTools.maxAttributes(ds);
	//		for(densen  
	//				Instance ins : ds){
	//			ins = ins.divide(maxValuesIns);
	//			System.out.println("normalized instance: " + ins);
	//		}
	//		return ds;
	//	}

	//	private LearnedPredicate unsupervisedClassify(Dataset ds){
	//
	//		System.out.println("build cluster...");
	//		//		System.out.println(ds.toString());
	//		KMeans km = new KMeans(2);
	//		Dataset[] dss = km.cluster(ds);
	//
	//		System.out.println("instance in first cluster: " + dss[0].size());
	//		System.out.println("instance in second cluster: " + dss[1].size());
	//
	//		Dataset labeledDataset = new DefaultDataset();
	//		for(Instance ins : dss[0]){
	//			ins.setClassValue("positive");
	//		}
	//		for(Instance ins : dss[1]){
	//			ins.setClassValue("negative");
	//		}
	//		labeledDataset.addAll(dss[0]);
	//		labeledDataset.addAll(dss[1]);
	//
	//		LibSVM svm = new LibSVM();
	//		svm.buildClassifier(labeledDataset);
	//		double[] weights = svm.getWeights();
	//
	//		List<Integer> ind = selectWeightElements(weights);
	//		for(int i=0; i<ind.size(); i++){
	//			newVars.add(allVars.get(ind.get(i)));
	//		}
	//
	//
	//		System.out.println("weights of the classifier: " + NumberUtil.ArrayToString(svm.getWeights()));
	//
	//		int rightCount = 0;
	//		int sumCount = 0;
	//		System.out.println("calculate accuracy...");
	//
	//		for(Instance ins : labeledDataset){
	//			if(svm.classify(ins).equals(ins.classValue())){
	//				rightCount ++;
	//			}
	//			sumCount++;
	//		}
	//
	//		classifyAccuracy = (double)rightCount/sumCount;
	//		System.out.println("accuracy : " + classifyAccuracy);
	//
	//		if(rightCount==negCount || rightCount==posCount){ // all data are in one side, fail to classify
	//			System.out.println("fail to find a splitting predicate...");
	//			return null;
	//		}
	//
	//		LearnedPredicate newPredicate = new LearnedPredicate(svm,"learned_predicate_" + AlgoProfile.iterationCount, newVars);
	//		return newPredicate;
	//	}


}
