package io.github.wang_jingyi.ZiQian.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.wang_jingyi.ZiQian.CheckLearned;
import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.TruePredicate;
import io.github.wang_jingyi.ZiQian.data.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.data.PrismPathData;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.example.CrowdPositive;
import io.github.wang_jingyi.ZiQian.exceptions.PrismNoResultException;
import io.github.wang_jingyi.ZiQian.exceptions.SimulationException;
import io.github.wang_jingyi.ZiQian.learn.AAlergia;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.learn.ModelSelection;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.refine.Counterexample;
import io.github.wang_jingyi.ZiQian.refine.CounterexampleGenerator;
import io.github.wang_jingyi.ZiQian.refine.CounterexamplePath;
import io.github.wang_jingyi.ZiQian.refine.HypothesisTest;
import io.github.wang_jingyi.ZiQian.refine.PrismSampler;
import io.github.wang_jingyi.ZiQian.refine.Refiner;
import io.github.wang_jingyi.ZiQian.refine.Sampler;
import io.github.wang_jingyi.ZiQian.refine.SprtTest;
import io.github.wang_jingyi.ZiQian.refine.SwatSampler;
import io.github.wang_jingyi.ZiQian.refine.TestEnvironment;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, SimulationException {
		
		Config.initConfig();
		FileUtil.cleanDirectory(Config.OUTPUT_MODEL_PATH);
		FileUtil.cleanDirectory(Config.TESTING_PATH);
		Config.writePropertyLearnFile();
		GlobalConfigs.OUTPUT_MODEL_PATH = Config.OUTPUT_MODEL_PATH;
		
		if(FileUtil.filesInDir(Config.DATA_PATH).size()<1){
			if(Config.SWAT){
				for(int i=1; i<=100; i++){
					System.out.println("simulation: " + i);
					int time =  1 + new Random().nextInt(5);
					Sampler sampler = new SwatSampler(false, Config.DATA_PATH, Config.SWAT_SAMPLE_STEP, Config.SWAT_RECORD_STEP, Config.SWAT_RUNNING_TIME);
					sampler.sample();
				}
			}
			else{
				for(int i=1; i<=1000; i++){
					System.out.println("-sample: " + i);
					Sampler sampler = new PrismSampler(Config.ORIG_MODEL_FILE, Config.DATA_PATH, Config.MODEL_SETTING);
					sampler.sample();
				}
			}
		}
		
		if(FileUtil.filesInDir(Config.DATA_PATH).size()<1){
			throw new SimulationException();
		}
		
		List<String> varsSet 
		= PrismPathData.extractPathVars(Config.DATA_PATH, Config.DELIMITER);
//				= new ArrayList<>();
//		varsSet.add("new");
//		varsSet.add("runCount");
//		varsSet.add("run");
//		varsSet.add("lastSeen");
//		varsSet.add("good");
//		varsSet.add("bad");
//		varsSet.add("recordLast");
//		varsSet.add("badObserve");
//		varsSet.add("done");
//		varsSet.add("observe0");
		
		
		System.out.println("data path: " + Config.DATA_PATH) ;
		ExtractPrismData epd = new ExtractPrismData(Config.DATA_PATH, Config.DATA_SIZE, Config.STEP_SIZE, 
				Config.DELIMITER, !Config.TERMINATE_SAMPLE);
		VariablesValueInfo vvl = epd.getVariablesValueInfo(varsSet);
		
		AlgoProfile.vars = vvl.getVars();	
		AlgoProfile.varsLength = vvl.getVarsLength();
		
		List<Predicate> pres = new ArrayList<>();
		pres.add(new TruePredicate());
//		pres.add(new NandReliable(20));
//		pres.add(new EglFormulaA());
//		pres.add(new EglFormulaB());
		pres.add(new CrowdPositive());
//		pres.add(new Underflow("LIT301",250));
//		pres.add(new Overflow("LS602",580));
		
		AlgoProfile.predicates = pres;
		
		TestEnvironment te = TestEnvironment.te;
		
		System.out.println("total length:" + vvl.getTotalLength());
		
		int iteration = 0;
		int larModelSize = 0;
		while(iteration<20){
			iteration++;
			System.out.println("-------------------------------------" + "iteration: " + iteration + "-------------------------------------");
			System.out.println("number of predicates: " + pres.size());
			larModelSize = run(vvl,pres,iteration,larModelSize,te);
		}
		
		System.out.println("End of the program.");
		
	}
	
	
	private static int run(VariablesValueInfo vvl, List<Predicate> pres, int iteration, int larModelSize, TestEnvironment te) throws FileNotFoundException, ClassNotFoundException, IOException{
		
		PredicateAbstraction pa = new PredicateAbstraction(pres);
		Input data = pa.abstractInput(vvl.getVarsValues());
		
		String modelName = Config.MODEL_NAME + iteration;
		
		ModelSelection gs = new AAlergia(Math.pow(2, -6), Math.pow(2, 6)); //
		LearningDTMC bestDTMC = gs.selectCriterion(data);
		bestDTMC.PrismModelTranslation(data, pres, modelName); //

//		LearnMergeEvolutions bestDTMC = new LearnMergeEvolutions();
//		bestDTMC.learn(data);
//		bestDTMC.PrismModelTranslation(data, pres, modelName);
//		
		
		
		// format to .pm file
		System.out.println("formatting the model to .pm file for model checking...");
		FormatPrismModel fpm = new FormatPrismModel("dtmc", Config.OUTPUT_MODEL_PATH , modelName);
		fpm.translateToFormat(bestDTMC.getPrismModel(), data);
		
		if(bestDTMC.getPrismModel().getNumOfPrismStates()==larModelSize){
			System.out.println("cannot split states any more, verification fails.");
			System.exit(0);
		}
		else{
			larModelSize = bestDTMC.getPrismModel().getNumOfPrismStates();
		}
		
		CheckLearned cl = new CheckLearned(Config.OUTPUT_MODEL_PATH + "/" + modelName + ".pm" , 
				Config.PROPERTY_LEARN_FILE, Config.PROPERTY_INDEX);
		try {
			cl.check();
		} catch (PrismNoResultException e) {
			e.printStackTrace();
		}
		
		CounterexampleGenerator counterg = new CounterexampleGenerator(bestDTMC.getPrismModel(),  // generate counterexamples
				Config.BOUNDED_STEP, Config.SAFETY_THRESHOLD);
		List<CounterexamplePath> counterPaths = counterg.generateCounterexamples();
		
		System.out.println("hypothesis testing...");
		
		Sampler sampler = new PrismSampler(Config.ORIG_MODEL_FILE, Config.TESTING_PATH, Config.MODEL_SETTING);
		te.init(pres,sampler,data,Config.DELIMITER,Config.STEP_SIZE);
		
//		HypothesisTest sst = new SingleSampleTest(1);
		HypothesisTest sst = new SprtTest(0.2, 0.1, 0.1, 0.1);
	
		boolean loop_first = false;
		Counterexample ce = new Counterexample(bestDTMC.getPrismModel(), counterPaths, sst, loop_first);
		System.out.println("analyzing counterexample...");
		ce.analyze(te);
		
		System.out.println("refine the predicate set...");
		
		Refiner refiner = new Refiner(ce.getSortedSplittingPoints(), vvl, pres, bestDTMC.getPrismModel());
//		List<String> dataPaths = new ArrayList<>();
//		dataPaths.add(Config.DATA_PATH);
//		dataPaths.add(Config.TESTING_PATH);
		
//		Dataset ds = refiner.collectDataFromPaths(dataPaths, ps.getPredicates(), 
//				ce.getSortedSplittingPoints(), bestDTMC.getPrismModel());
		Predicate newPredicate = refiner.refine();
		
		if(newPredicate==null){
			System.out.println("fail to learn a new predicate...");
			System.exit(0);
		}
		
		pres.add(newPredicate);
		AlgoProfile.predicates = pres;
		AlgoProfile.newIteration = true;
		AlgoProfile.iterationCount ++;
		
		return larModelSize;
	}
	
}
