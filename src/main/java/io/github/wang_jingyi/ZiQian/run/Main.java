package io.github.wang_jingyi.ZiQian.run;

import io.github.wang_jingyi.ZiQian.CheckLearned;
import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.PredicateSet;
import io.github.wang_jingyi.ZiQian.TruePredicate;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.evolution.LearnMergeEvolutions;
import io.github.wang_jingyi.ZiQian.example.CrowdPositive;
import io.github.wang_jingyi.ZiQian.exceptions.PrismNoResultException;
import io.github.wang_jingyi.ZiQian.exceptions.SimulationException;
import io.github.wang_jingyi.ZiQian.prism.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismPathData;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.profile.TimeProfile;
import io.github.wang_jingyi.ZiQian.refine.Refiner;
import io.github.wang_jingyi.ZiQian.sample.Counterexample;
import io.github.wang_jingyi.ZiQian.sample.CounterexampleGenerator;
import io.github.wang_jingyi.ZiQian.sample.CounterexamplePath;
import io.github.wang_jingyi.ZiQian.sample.HypothesisTest;
import io.github.wang_jingyi.ZiQian.sample.Simulation;
import io.github.wang_jingyi.ZiQian.sample.SprtTest;
import io.github.wang_jingyi.ZiQian.sample.TestEnvironment;
import io.github.wang_jingyi.ZiQian.swat.SwatSimulation;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, SimulationException {
		
		Config.initConfig();
		FileUtil.cleanDirectory(Config.OUTPUT_MODEL_PATH);
		FileUtil.cleanDirectory(Config.TESTING_PATH);
		Config.writePropertyLearnFile();
		
		if(FileUtil.filesInDir(Config.DATA_PATH).size()<1){
			if(Config.SWAT){
				for(int i=1; i<=100; i++){
					System.out.println("simulation: " + i);
					int time =  1 + new Random().nextInt(5);
					SwatSimulation.simulate(Config.SWAT_SAMPLE_STEP, Config.SWAT_RECORD_STEP, time, Config.DATA_PATH, i);
				}
			}
			else{
				for(int i=1; i<=1000; i++){
					System.out.println("simulation: " + i);
					Simulation sim = new Simulation(Config.ORIG_MODEL_FILE, Config.DATA_PATH, "path"+i, Config.MODEL_SETTING);
					sim.run();
				}
			}
		}
		
		if(FileUtil.filesInDir(Config.DATA_PATH).size()<1){
			throw new SimulationException();
		}
		
		List<String> varsSet 
		= PrismPathData.extractPathVars(Config.DATA_PATH);
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
		TimeProfile.mainStartTime = System.nanoTime();
		ExtractPrismData epd = new ExtractPrismData(Config.DATA_PATH, Config.DATA_SIZE, Config.STEP_SIZE);
		VariablesValueInfo vvl = epd.getVariablesValueInfo(varsSet);
		
		AlgoProfile.vars = vvl.getVars();	
		AlgoProfile.varLength = vvl.getVarsLength();
		
		List<Predicate> pres = new ArrayList<>();
		pres.add(new TruePredicate());
//		pres.add(new NandReliable(20));
//		pres.add(new EglFormulaA());
//		pres.add(new EglFormulaB());
		pres.add(new CrowdPositive());
//		pres.add(new Underflow());
//		pres.add(new Overflow());
		
		AlgoProfile.predicates = pres;
		
		TestEnvironment te = TestEnvironment.te;
		
		System.out.println("total length:" + vvl.getTotalLength());
		
		int iteration = 0;
		int larModelSize = 0;
		while(iteration<20){
			iteration++;
			AlgoProfile.runTimeLog.append("-------------------------------------" + "iteration: " + iteration + "-------------------------------------");
			System.out.println("-------------------------------------" + "iteration: " + iteration + "-------------------------------------");
			System.out.println("number of predicates: " + pres.size());
			larModelSize = run(vvl,pres,iteration,larModelSize,te);
			TimeProfile.iterationEndTime = System.nanoTime();
			TimeProfile.iterationTime = TimeProfile.iterationEndTime - TimeProfile.mainStartTime;
			System.out.println("iteration " + iteration + " time is: " + TimeProfile.nanoToSeconds(TimeProfile.iterationTime));
			TimeProfile.sb.append("iteration " + iteration + " time is: " + TimeProfile.nanoToSeconds(TimeProfile.iterationTime) + "\n");
		}
		
		System.out.println("End of the program.");
		
	}
	
	
	private static int run(VariablesValueInfo vvl, List<Predicate> pres, int iteration, int larModelSize, TestEnvironment te) throws FileNotFoundException, ClassNotFoundException, IOException{
		
		PredicateSet ps = new PredicateSet(pres);
		PredicateAbstraction pa = new PredicateAbstraction(pres);
		Input data = pa.abstractInput(vvl.getVarsValues());
		
		String modelName = Config.MODEL_NAME + iteration;
		
//		ModelSelection gs = new PrefixMergeGoldenSearch(Math.pow(2, -6), Math.pow(2, 6)); //
//		LearningDTMC bestDTMC = gs.selectCriterion(data);
//		bestDTMC.PrismModelTranslation(data, ps, modelName); //

		LearnMergeEvolutions bestDTMC = new LearnMergeEvolutions();
		bestDTMC.learn(data);
		bestDTMC.PrismModelTranslation(data, ps, modelName);
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
		
		te.init(ps, Config.ORIG_MODEL_FILE, Config.MODEL_SETTING,
				Config.TESTING_PATH);
		
//		HypothesisTest sst = new SingleSampleTest(1);
		HypothesisTest sst = new SprtTest(0.2, 0.1, 0.1, 0.1);
		Counterexample ce = new Counterexample(bestDTMC.getPrismModel(), counterPaths, te, sst);
		System.out.println("analyzing counterexample...");
		ce.analyze();
		
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
