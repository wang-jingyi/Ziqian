package io.github.wang_jingyi.ZiQian;

import io.github.wang_jingyi.ZiQian.example.CrowdPositive;
import io.github.wang_jingyi.ZiQian.exceptions.PrismNoResultException;
import io.github.wang_jingyi.ZiQian.learn.AAlergia;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.learn.ModelSelection;
import io.github.wang_jingyi.ZiQian.prism.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.refine.Refiner;
import io.github.wang_jingyi.ZiQian.run.PlatformDependent;
import io.github.wang_jingyi.ZiQian.sample.Counterexample;
import io.github.wang_jingyi.ZiQian.sample.CounterexampleGenerator;
import io.github.wang_jingyi.ZiQian.sample.CounterexamplePath;
import io.github.wang_jingyi.ZiQian.sample.HypothesisTest;
import io.github.wang_jingyi.ZiQian.sample.PrismSampler;
import io.github.wang_jingyi.ZiQian.sample.Sampler;
import io.github.wang_jingyi.ZiQian.sample.SprtTest;
import io.github.wang_jingyi.ZiQian.sample.TestEnvironment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CounterExtractionTest {

	@Test
	public void testCounterExtraction() throws IOException, ClassNotFoundException, PrismNoResultException{
		String modelPath = "/crowds/TotalRuns=5,CrowdSize=10/paths";
		int dataSize = Integer.MAX_VALUE;
		ExtractPrismData epd = new ExtractPrismData(PlatformDependent.CAV_MODEL_ROOT+modelPath, dataSize,1, " ");
		VariablesValueInfo vvl = epd.getVariablesValueInfo();
		
		AlgoProfile.vars = vvl.getVars();
		AlgoProfile.varLength = vvl.getVarsLength();
		
		List<Predicate> pres = new ArrayList<>();
		pres.add(new TruePredicate());
		pres.add(new CrowdPositive());
		
		PredicateAbstraction pa = new PredicateAbstraction(pres);
		Input data = pa.abstractInput(vvl.getVarsValues());
		
		ModelSelection gs = new AAlergia(Math.pow(2, -6), Math.pow(2, 6)); //
		LearningDTMC bestDTMC = gs.selectCriterion(data);
		bestDTMC.PrismModelTranslation(data, pres, "Crowd"); //

		int iteration = 0;
		String modelName = "testCrowd" + iteration;
		
		// format to .pm file
		System.out.println("Formatting the model to .pm file for model checking...");
		FormatPrismModel fpm = new FormatPrismModel("dtmc", PlatformDependent.CAV_MODEL_ROOT + "/crowds" , modelName);
		fpm.translateToFormat(bestDTMC.getPrismModel(), data);
		
		CheckLearned cl = new CheckLearned(PlatformDependent.CAV_MODEL_ROOT + "/crowds/"+modelName+".pm" , PlatformDependent.CAV_MODEL_ROOT + "/crowds/crowds_learn.pctl", 1);
		cl.check();
		
		CounterexampleGenerator counterg = new CounterexampleGenerator(bestDTMC.getPrismModel(), -1, 0.15);
		List<CounterexamplePath> counterPaths = counterg.generateCounterexamples();
		
		System.out.println("hypothesis testing...");
		
		TestEnvironment te = TestEnvironment.te;
		Sampler sampler = new PrismSampler(PlatformDependent.CAV_MODEL_ROOT+"/crowds/crowds.pm", PlatformDependent.CAV_MODEL_ROOT+"/crowds/testPaths"
				,"TotalRuns=5,CrowdSize=10");
		te.init(pres, sampler);
//		HypothesisTest sst = new SingleSampleTest(5);
		HypothesisTest sst = new SprtTest(0.2, 0.1, 0.1, 0.1);
		Counterexample ce = new Counterexample(bestDTMC.getPrismModel(), counterPaths, sst);
		System.out.println("analyzing counterexample...");
		ce.analyze(te);
		
		
		Refiner refiner = new Refiner(ce.getSortedSplittingPoints(),vvl,pres,bestDTMC.getPrismModel());
		Predicate newPredicate = refiner.refine();
		pres.add(newPredicate);
		AlgoProfile.newIteration = true;
		AlgoProfile.iterationCount ++;
		
	}
	
	
}
