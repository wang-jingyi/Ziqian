package io.github.wang_jingyi.ZiQian.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.CheckLearned;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.data.SWaTInput;
import io.github.wang_jingyi.ZiQian.exceptions.PrismNoResultException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedTestingTypeException;
import io.github.wang_jingyi.ZiQian.learn.LearnPST;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.refine.Counterexample;
import io.github.wang_jingyi.ZiQian.refine.CounterexampleGenerator;
import io.github.wang_jingyi.ZiQian.refine.CounterexamplePath;
import io.github.wang_jingyi.ZiQian.refine.ModelTesting;
import io.github.wang_jingyi.ZiQian.refine.Refiner;
import io.github.wang_jingyi.ZiQian.refine.Sampler;
import io.github.wang_jingyi.ZiQian.refine.SingleSampleTest;
import io.github.wang_jingyi.ZiQian.refine.SingleTraceSampler;
import io.github.wang_jingyi.ZiQian.refine.SprtTest;
import io.github.wang_jingyi.ZiQian.refine.TestEnvironment;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

public class SingleTraceLAR {

	private List<Predicate> property;
	private List<Predicate> predicate_set;
	private int maximumIteration = 20;
	private TestEnvironment te = TestEnvironment.te;
	private Sampler sampler;
	private String TRAINING_LOG_PATH;
	private String TESTING_LOG_PATH;
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
	private int previous_count = 50;
	private String data_delimiter;
	private int data_step_size;
	private int data_size;
	private boolean terminate_sample;
	private boolean selective_data_collection;
	private double epsilon = 0.0000001;


	public void execute() throws FileNotFoundException, ClassNotFoundException, IOException, UnsupportedTestingTypeException{

		AlgoProfile.predicates = predicate_set;

		SWaTInput input = new SWaTInput(TRAINING_LOG_PATH, TESTING_LOG_PATH, predicate_set, previous_count, 
				data_size, data_step_size, data_delimiter);
		input.execute();

		AlgoProfile.vars = input.getTraining_vvi().getVars();	
		AlgoProfile.varsLength = input.getTraining_vvi().getVarsLength();

		int iteration = 0;
		while(iteration<maximumIteration){

			String modelName = MODEL_NAME + "_" + iteration;

			// learning
			LearningDTMC learner = new LearnPST(epsilon);
			learner.learn(input.getAbstractTrainingInput());
			learner.PrismModelTranslation(input.getAbstractTrainingInput(), predicate_set, modelName);
			identifyInitialStates(learner.getPrismModel(), input.getPreviousObservation());
			PrismModel bestDTMC = learner.getPrismModel();


			// update LAR model size
			if(bestDTMC.getNumOfPrismStates()==LARModelSize){
				System.out.println("====== Cannot obtain a new linear predicate, verification fails ======");
				TimeProfile.main_end_time = System.nanoTime();;
				TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
				TimeProfile.outputTimeProfile();
				TimeProfile.outputTimeProfile(GlobalConfigs.OUTPUT_MODEL_PATH+"/time_profile.txt");
				FileUtil.writeObject(OUTPUT_MODEL_PATH + "/predicates", AlgoProfile.predicates);
				System.exit(0);
			}
			else{
				LARModelSize = bestDTMC.getNumOfPrismStates();
			}

			// translate learned model to .pm file
			FormatPrismModel fpm = new FormatPrismModel("dtmc", OUTPUT_MODEL_PATH, modelName, true);
			fpm.translateToFormat(learner.getPrismModel(),input.getAbstractTrainingInput());


			// verify the property against the model
			TimeProfile.pmc_start_time = System.nanoTime();
			CheckLearned cl = new CheckLearned(OUTPUT_MODEL_PATH + "/" + modelName + ".pm" , 
					PROPERTY_LEARN_FILE, PROPERTY_INDEX);
			try {
				cl.check();
			} catch (PrismNoResultException e) {
				e.printStackTrace();
			}

			// counterexample generation
			TimeProfile.ce_generation_start_time = System.nanoTime();
			CounterexampleGenerator counterg = new CounterexampleGenerator(bestDTMC,  // generate counterexamples
					boundedSteps, safetyBound);
			List<CounterexamplePath> counterPaths = counterg.generateCounterexamples();
			TimeProfile.ce_generation_end_time = System.nanoTime();;
			TimeProfile.ce_generation_times.add(TimeProfile.nanoToSeconds(TimeProfile.ce_generation_end_time
					-TimeProfile.ce_generation_start_time));

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
			sampler = new SingleTraceSampler(SwatConfig.DECOMPOSED_DATA_PATH, input.getAbstractTestingInput(), bestDTMC, ce, 
					input.getPreviousObservation());
			te.init(input.getPredicates(), sampler, input.getAbstractTrainingInput(), SwatConfig.DELIMITER, SwatConfig.STEP_SIZE);
			ce.analyze(te);


			// refinement
			TimeProfile.refine_start_time = System.nanoTime();
			Refiner refiner = new Refiner(ce.getSortedSplittingPoints(), input.getTraining_vvi(), input.getPredicates(), bestDTMC, terminate_sample,
					selective_data_collection);
			Predicate newPredicate = refiner.refine();
			TimeProfile.refine_end_time = System.nanoTime();
			TimeProfile.refine_times.add(TimeProfile.nanoToSeconds(TimeProfile.refine_end_time
					-TimeProfile.refine_start_time));

			if(newPredicate==null){
				TimeProfile.iteration_end_time = System.nanoTime();
				TimeProfile.iteration_times.add(TimeProfile.nanoToSeconds
						(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time));
				System.out.println("======= Fail to learn a new predicate, verification fails ======");
				FileUtil.writeObject(SwatConfig.OUTPUT_MODEL_PATH + "/predicates", AlgoProfile.predicates);
				TimeProfile.main_end_time = System.nanoTime();;
				TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
				TimeProfile.outputTimeProfile();
				TimeProfile.outputTimeProfile(GlobalConfigs.OUTPUT_MODEL_PATH+"/time_profile.txt");
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
	

	public String getOUTPUT_MODEL_PATH() {
		return OUTPUT_MODEL_PATH;
	}
	
	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public void setProperty(List<Predicate> property) {
		this.property = property;
	}
	
	public void setPredicate_set(List<Predicate> predicate_set) {
		this.predicate_set = predicate_set;
	}

	public void setMaximumIteration(int maximumIteration) {
		this.maximumIteration = maximumIteration;
	}

	public void setTe(TestEnvironment te) {
		this.te = te;
	}

	public void setSampler(Sampler sampler) {
		this.sampler = sampler;
	}

	public void setTRAINING_LOG_PATH(String tRAINING_LOG_PATH) {
		TRAINING_LOG_PATH = tRAINING_LOG_PATH;
	}

	public void setTESTING_LOG_PATH(String tESTING_LOG_PATH) {
		TESTING_LOG_PATH = tESTING_LOG_PATH;
	}

	public void setOUTPUT_MODEL_PATH(String oUTPUT_MODEL_PATH) {
		OUTPUT_MODEL_PATH = oUTPUT_MODEL_PATH;
	}

	public void setPROPERTY_LEARN_FILE(String pROPERTY_LEARN_FILE) {
		PROPERTY_LEARN_FILE = pROPERTY_LEARN_FILE;
	}

	public void setPROPERTY_INDEX(int pROPERTY_INDEX) {
		PROPERTY_INDEX = pROPERTY_INDEX;
	}

	public void setBoundedSteps(int boundedSteps) {
		this.boundedSteps = boundedSteps;
	}

	public void setSafetyBound(double safetyBound) {
		this.safetyBound = safetyBound;
	}

	public void setMODEL_NAME(String mODEL_NAME) {
		MODEL_NAME = mODEL_NAME;
	}

	public void setTESTING_TYPE(String tESTING_TYPE) {
		TESTING_TYPE = tESTING_TYPE;
	}

	public void setSstSampleSize(int sstSampleSize) {
		this.sstSampleSize = sstSampleSize;
	}

	public void setLARModelSize(int lARModelSize) {
		LARModelSize = lARModelSize;
	}

	public void setError_alpha(double error_alpha) {
		this.error_alpha = error_alpha;
	}

	public void setError_beta(double error_beta) {
		this.error_beta = error_beta;
	}

	public void setConfidence_inteval(double confidence_inteval) {
		this.confidence_inteval = confidence_inteval;
	}

	public void setPrevious_count(int previous_count) {
		this.previous_count = previous_count;
	}

	public void setData_delimiter(String data_delimiter) {
		this.data_delimiter = data_delimiter;
	}

	public void setData_step_size(int data_step_size) {
		this.data_step_size = data_step_size;
	}

	public void setData_size(int data_size) {
		this.data_size = data_size;
	}

	public void setTerminate_sample(boolean terminate_sample) {
		this.terminate_sample = terminate_sample;
	}

	public void setSelective_data_collection(boolean selective_data_collection) {
		this.selective_data_collection = selective_data_collection;
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


}
