package io.github.wang_jingyi.ZiQian.refine;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.VariablesValue;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismPathData;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.sample.SplittingPoint;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.NumberUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import libsvm.LibSVM;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

public class Refiner implements DataCollector, Divider{

	private boolean oneLabel = false;
	private List<String> allVars = AlgoProfile.vars;
	private List<String> newVars = new ArrayList<>();


	public Refiner() {
		super();
	}

	public void updateAlgoProfile(){

	}

	public Dataset collectDataFromVarsValueInfo(VariablesValueInfo vvl, List<Predicate> predicates, List<SplittingPoint> sps, PrismModel pm){
		return collectDataSet(vvl.getVarsValues(), predicates, sps, pm);
	}

	@Override
	public Dataset collectDataFromPaths(List<String> pathsDirs,
			List<Predicate> predicates, List<SplittingPoint> sps, PrismModel pm) throws IOException {

		List<List<VariablesValue>> vvs = new ArrayList<>(); // extract all variables values
		for(String pathsDir : pathsDirs){
			vvs.addAll(PrismPathData.extractMEData(pathsDir, AlgoProfile.vars, Config.DATA_SIZE));
		}
		return collectDataSet(vvs, predicates, sps, pm);
	}

	private Dataset collectDataSet(List<List<VariablesValue>> vvs, List<Predicate> predicates, List<SplittingPoint> sps, PrismModel pm){
		boolean allOneLabel = false;
		for(int z=0; z<sps.size(); z++){
			SplittingPoint sp = sps.get(z);

			Dataset ds = new DefaultDataset();
			
			int negcount = 0;

			int featureSize = AlgoProfile.vars.size();

			int currentstate = sp.getCurrentStateId();
			int nextstate = sp.getNextStateId();


			String currentString = pm.getPrismStates().get(currentstate-1).getCurrentState();
			String nextString = pm.getPrismStates().get(nextstate-1).getCurrentState();

			PredicateAbstraction pa = new PredicateAbstraction(predicates);

			for(List<VariablesValue> vvl : vvs){
				int pcount = 0;
				int ncount = 0;
				List<String> vvls = pa.abstractList(vvl);
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
						if(Config.TERMINATE_SAMPLE){ // sample will terminate like crowds/nand
							Instance ins = new DenseInstance(values,"negative");
							ds.add(ins);
							negcount ++;
						}
						continue;
					}

					if(vvls.get(i+1).equals(nextString) 
//							&& i==0 // only collect first and end state
							){ // check if the next state is according to the counterexample path
						
						if(Config.SELECTIVE_DATA_COLLECTION){
							if(pcount<1){
								Instance ins = new DenseInstance(values,"positive");
								ds.add(ins);
								pcount ++;
							}
						}
						else{
							Instance ins = new DenseInstance(values,"positive");
							ds.add(ins);
							pcount ++;
						}
						
					}
					else
//						if(i==0)	// only collect first and end state
					{
						if(Config.SELECTIVE_DATA_COLLECTION){
							if(ncount<1){
								Instance ins = new DenseInstance(values,"negative");
								ds.add(ins);
								ncount ++;
								negcount ++;
							}
						}
						else{
							Instance ins = new DenseInstance(values,"negative");
							ds.add(ins);
							ncount ++;
							negcount ++;
						}
						
					}
				}
			}

			if(negcount==ds.size() || negcount==0){ // 
				oneLabel = true;
			}
			else{
				oneLabel = false;
			}

			System.out.println("instance in the dataset: " + ds.size());
			System.out.println("negative instance in the dataset: " + negcount);

			if(!oneLabel){ // if have both positive and negative instance
				System.out.println("instance in the dataset: " + ds.size());
				System.out.println("negative instance in the dataset: " + negcount);
				System.out.println("transition to split: " + sp.toString());
				return ds;
			}
			System.out.println("splitting point " + sp.toString() + "only has one label.");
			if(z==sps.size()-1){ // if all splitting point have only one label
				System.out.println("no splitting point with two lables.");
				allOneLabel = true;
				System.out.println("all one label happens");
				System.exit(0);
//				z = -1 ;
//				continue;
			}

			if(allOneLabel){
				System.out.println("transition to split: " + sp.toString());
				return ds;
			}
		}

		return null;
	}

	@Override
	public LearnedPredicate findSplitPredicates(Dataset ds) throws FileNotFoundException {
		System.out.println("one lable? :" + oneLabel);
		if(oneLabel==true){
			return	unsupervisedClassify(ds);
		}
		else{
			return supervisedClassify(ds);
		}
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
		
		// output collected data for reference
		StringBuilder sb = new StringBuilder();
		for(Instance ins : ds){
			sb.append(ins.toString() + "\n");
//			System.out.println(ins.toString());
		}
		FileUtil.writeStringToFile("/Users/jingyi/Documents/collected_data.txt", sb.toString());
		
		svm.buildClassifier(ds);
		
		StringBuilder svm_log = new StringBuilder();
		svm_log.append("variables involved in the classifier: \n");
		svm_log.append(allVars.toString() + "\n");
		svm_log.append("weights of the classifier: \n");
		svm_log.append(NumberUtil.doubleArrayToString(svm.getWeights()));
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
		System.out.println("accuracy : " + (double)rightCount/sumCount);
		LearnedPredicate newPredicate = new LearnedPredicate(svm,"learned_predicate_" + AlgoProfile.iterationCount, newVars);
		return newPredicate;
	}

	private LearnedPredicate unsupervisedClassify(Dataset ds){

		System.out.println("build cluster...");
		//		System.out.println(ds.toString());
		KMeans km = new KMeans(2);
		Dataset[] dss = km.cluster(ds);

		System.out.println("instance in first cluster: " + dss[0].size());
		System.out.println("instance in second cluster: " + dss[1].size());

		Dataset labeledDataset = new DefaultDataset();
		for(Instance ins : dss[0]){
			ins.setClassValue("positive");
		}
		for(Instance ins : dss[1]){
			ins.setClassValue("negative");
		}
		labeledDataset.addAll(dss[0]);
		labeledDataset.addAll(dss[1]);

		LibSVM svm = new LibSVM();
		svm.buildClassifier(labeledDataset);
		double[] weights = svm.getWeights();

		List<Integer> ind = selectWeightElements(weights);
		for(int i=0; i<ind.size(); i++){
			newVars.add(allVars.get(ind.get(i)));
		}


		System.out.println("weights of the classifier: " + NumberUtil.doubleArrayToString(svm.getWeights()));

		int rightCount = 0;
		int sumCount = 0;
		System.out.println("calculate accuracy...");
		for(Instance ins : labeledDataset){
			if(svm.classify(ins).equals(ins.classValue())){
				rightCount ++;
			}
			sumCount++;
		}
		System.out.println("accuracy : " + (double)rightCount/sumCount);
		LearnedPredicate newPredicate = new LearnedPredicate(svm,"learned_predicate_" + AlgoProfile.iterationCount, newVars);
		return newPredicate;
	}


}
