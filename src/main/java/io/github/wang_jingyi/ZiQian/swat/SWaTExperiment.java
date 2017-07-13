package io.github.wang_jingyi.ZiQian.swat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.CheckLearned;
import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.TruePredicate;
import io.github.wang_jingyi.ZiQian.exceptions.PrismNoResultException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedTestingTypeException;
import io.github.wang_jingyi.ZiQian.learn.LearnPST;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.profile.TimeProfile;
import io.github.wang_jingyi.ZiQian.refine.Refiner;
import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.run.GlobalConfigs;
import io.github.wang_jingyi.ZiQian.sample.Counterexample;
import io.github.wang_jingyi.ZiQian.sample.CounterexampleGenerator;
import io.github.wang_jingyi.ZiQian.sample.CounterexamplePath;
import io.github.wang_jingyi.ZiQian.sample.ModelTesting;
import io.github.wang_jingyi.ZiQian.sample.Sampler;
import io.github.wang_jingyi.ZiQian.sample.SingleSampleTest;
import io.github.wang_jingyi.ZiQian.sample.SingleTraceSampler;
import io.github.wang_jingyi.ZiQian.sample.SprtTest;
import io.github.wang_jingyi.ZiQian.sample.TestEnvironment;
import io.github.wang_jingyi.ZiQian.swat.property.OverHigh;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

public class SWaTExperiment {

	private List<Predicate> property;
	private int maximumIteration = 20;
	private TestEnvironment te = TestEnvironment.te;
	private Sampler sampler;
	private String DATA_PATH;
	private String OUTPUT_MODEL_PATH;
	private String PROPERTY_LEARN_FILE;
	private int PROPERTY_INDEX;
	private int boundedSteps = -1;
	private double safetyBound = 0.4;
	private String MODEL_NAME;
	private String TESTING_TYPE = "sst";
	private int sstSampleSize = 10;
	private int LARModelSize;
	private double error_alpha = 0.05;
	private double error_beta = 0.05;
	private double confidence_inteval;
	private String data_delimiter;
	private int data_step_size;
	private boolean terminate_sample;
	private boolean selective_data_collection;


	public void singleLAR() throws FileNotFoundException, ClassNotFoundException, IOException, UnsupportedTestingTypeException{

		List<Predicate> pres = new ArrayList<>();
		pres.add(new TruePredicate());
		pres.add(new OverHigh(SwatConfig.SENSOR,SwatConfig.SENSOR_THRES));

		AlgoProfile.predicates = pres;

		SWaTInput input = new SWaTInput(SwatConfig.TRAINING_LOG_PATH, SwatConfig.TESTING_LOG, pres, SwatConfig.PREVIOUS_COUNT);
		input.execute();

		AlgoProfile.vars = input.getTraining_vvi().getVars();	
		AlgoProfile.varsLength = input.getTraining_vvi().getVarsLength();

		int iteration = 0;
		while(iteration<maximumIteration){
			
			String modelName = SwatConfig.MODEL_NAME + "_" + iteration;
			
			// learning
			LearningDTMC learner = new LearnPST(0.0000001);
			learner.learn(input.getAbstractTrainingInput());
			learner.PrismModelTranslation(input.getAbstractTrainingInput(), pres, "swat");
			identifyInitialStates(learner.getPrismModel(), input.getPreviousObservation());
			PrismModel bestDTMC = learner.getPrismModel();

			// translate learned model to .pm file
			FormatPrismModel fpm = new FormatPrismModel("dtmc", SwatConfig.OUTPUT_MODEL_PATH, modelName, true);
			fpm.translateToFormat(learner.getPrismModel(),input.getAbstractTrainingInput());

			// verify the property against the model
			CheckLearned cl = new CheckLearned(SwatConfig.OUTPUT_MODEL_PATH + "/" + modelName + ".pm" , 
					SwatConfig.PROPERTY_LEARN_FILE, SwatConfig.PROPERTY_INDEX);
			try {
				cl.check();
			} catch (PrismNoResultException e) {
				e.printStackTrace();
			}

			// counterexample generation
			CounterexampleGenerator counterg = new CounterexampleGenerator(bestDTMC,  // generate counterexamples
					SwatConfig.BOUNDED_STEP, SwatConfig.SAFETY_THRESHOLD);
			List<CounterexamplePath> counterPaths = counterg.generateCounterexamples();
			
			ModelTesting mt = new ModelTesting();
			if(TESTING_TYPE.equalsIgnoreCase("sst")){
				mt.setHypothesisTesting(new SingleSampleTest(sstSampleSize));
			}
			else if(TESTING_TYPE.equalsIgnoreCase("sprt")){
				mt.setHypothesisTesting(new SprtTest(safetyBound, error_alpha, error_beta, confidence_inteval));
			}
			else{
				throw new UnsupportedTestingTypeException();
			}

			// find spurious transitions
			Counterexample ce = new Counterexample(bestDTMC, counterPaths, mt.getHypothesisTesting());
			
			// validate counterexample
			Sampler sampler = new SingleTraceSampler(SwatConfig.DECOMPOSED_DATA_PATH, input.getAbstractTestingInput(), bestDTMC, ce, 
					input.getPreviousObservation());
			te.init(input.getPredicates(), sampler, input.getAbstractTrainingInput(), SwatConfig.DELIMITER, SwatConfig.STEP_SIZE);
			ce.analyze(te);


			// refinement
			Refiner refiner = new Refiner(ce.getSortedSplittingPoints(), input.getTraining_vvi(), input.getPredicates(), bestDTMC, terminate_sample,
					selective_data_collection);
			Predicate newPredicate = refiner.refine();

			if(newPredicate==null){
				TimeProfile.iteration_end_time = System.nanoTime();
				TimeProfile.iteration_times.add(TimeProfile.nanoToSeconds
						(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time));
				System.out.println("======= Fail to learn a new predicate, verification fails ======");
				FileUtil.writeObject(SwatConfig.OUTPUT_MODEL_PATH + "/predicates", AlgoProfile.predicates);
				System.exit(0);
			}

			input.getPredicates().add(newPredicate);
			TimeProfile.iteration_end_time = System.nanoTime();
			TimeProfile.iteration_times.add(TimeProfile.nanoToSeconds(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time));
			AlgoProfile.predicates = property;
			AlgoProfile.newIteration = true;
			AlgoProfile.iterationCount++;
		}

	}

	private void identifyInitialStates(PrismModel model, List<String> previous_observation){

		List<PrismState> states = model.getPrismStates();
		List<PrismState> iss = new ArrayList<>();
		for(PrismState state : states){
			if(StringUtil.isSuffix(state.getLabel(), previous_observation)){
				iss.add(state);
			}
		}
		model.setInitialStates(iss);
	}

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, UnsupportedTestingTypeException{
		SWaTExperiment exp = new SWaTExperiment();
		exp.singleLAR();
	}


}
