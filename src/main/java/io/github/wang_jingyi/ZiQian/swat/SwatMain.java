package io.github.wang_jingyi.ZiQian.swat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.CheckLearned;
import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.PredicateSet;
import io.github.wang_jingyi.ZiQian.TruePredicate;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.evolution.LearnMergeEvolutions;
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
import io.github.wang_jingyi.ZiQian.sample.SprtTest;
import io.github.wang_jingyi.ZiQian.sample.TestEnvironment;
import io.github.wang_jingyi.ZiQian.swat.property.OverHigh;

public class SwatMain {
	
public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, SimulationException {
		
		SwatConfig.writePropertyLearnFile();

		List<String> varsSet 
		= PrismPathData.extractPathVars(SwatConfig.DATA_PATH, SwatConfig.DELIMITER);
		
		TimeProfile.mainStartTime = System.nanoTime();
		ExtractPrismData epd = new ExtractPrismData(SwatConfig.DATA_PATH, SwatConfig.DATA_SIZE, SwatConfig.STEP_SIZE);
		VariablesValueInfo vvl = epd.getVariablesValueInfo(varsSet);
		
		AlgoProfile.vars = vvl.getVars();	
		AlgoProfile.varLength = vvl.getVarsLength();
		
		List<Predicate> pres = new ArrayList<>();
		pres.add(new TruePredicate());
		pres.add(new OverHigh("LIT101",800));
		
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
		
		String modelName = "SWAT_" + iteration;
		
//		ModelSelection gs = new PrefixMergeGoldenSearch(Math.pow(2, -6), Math.pow(2, 6)); //
//		LearningDTMC bestDTMC = gs.selectCriterion(data);
//		bestDTMC.PrismModelTranslation(data, ps, modelName); //

		LearnMergeEvolutions bestDTMC = new LearnMergeEvolutions();
		bestDTMC.learn(data);
		bestDTMC.PrismModelTranslation(data, ps, modelName);
//		
		
		
		// format to .pm file
		System.out.println("formatting the model to .pm file for model checking...");
		FormatPrismModel fpm = new FormatPrismModel("dtmc", SwatConfig.OUTPUT_MODEL_PATH, modelName);
		fpm.translateToFormat(bestDTMC.getPrismModel(), data);
		
		if(bestDTMC.getPrismModel().getNumOfPrismStates()==larModelSize){
			System.out.println("cannot split states any more, verification fails.");
			System.exit(0);
		}
		else{
			larModelSize = bestDTMC.getPrismModel().getNumOfPrismStates();
		}
		
		CheckLearned cl = new CheckLearned(SwatConfig.OUTPUT_MODEL_PATH + "/" + modelName + ".pm" , 
				SwatConfig.PROPERTY_LEARN_FILE, SwatConfig.PROPERTY_INDEX);
		try {
			cl.check();
		} catch (PrismNoResultException e) {
			e.printStackTrace();
		}
		
		CounterexampleGenerator counterg = new CounterexampleGenerator(bestDTMC.getPrismModel(),  // generate counterexamples
				SwatConfig.BOUNDED_STEP, SwatConfig.SAFETY_THRESHOLD);
		List<CounterexamplePath> counterPaths = counterg.generateCounterexamples();
		
		System.out.println("hypothesis testing...");
		
		te.init(ps, null, null, null);
		
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
