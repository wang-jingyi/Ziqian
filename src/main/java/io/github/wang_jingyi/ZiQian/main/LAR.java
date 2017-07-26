package io.github.wang_jingyi.ZiQian.main;

import java.io.IOException;
import java.util.List;

import io.github.wang_jingyi.ZiQian.CheckLearned;
import io.github.wang_jingyi.ZiQian.DTMCLearner;
import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.exceptions.PrismNoResultException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedLearningTypeException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedTestingTypeException;
import io.github.wang_jingyi.ZiQian.learn.AAlergia;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.learn.evolution.LearnMergeEvolutions;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.refine.Counterexample;
import io.github.wang_jingyi.ZiQian.refine.CounterexampleGenerator;
import io.github.wang_jingyi.ZiQian.refine.CounterexamplePath;
import io.github.wang_jingyi.ZiQian.refine.ModelTesting;
import io.github.wang_jingyi.ZiQian.refine.Refiner;
import io.github.wang_jingyi.ZiQian.refine.Sampler;
import io.github.wang_jingyi.ZiQian.refine.SingleSampleTest;
import io.github.wang_jingyi.ZiQian.refine.SprtTest;
import io.github.wang_jingyi.ZiQian.refine.TestEnvironment;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

public class LAR {
	
	private VariablesValueInfo vvi;
	private List<Predicate> property;
	private int maximumIteration = 20;
	private TestEnvironment te;
	private Sampler sampler;
	private String DATA_PATH;
	private String OUTPUT_MODEL_PATH;
	private String PROPERTY_LEARN_FILE;
	private int PROPERTY_INDEX;
	private int boundedSteps = -1;
	private double safetyBound;
	private String MODEL_NAME;
	private String LEARNING_TYPE;
	private double alergia_epsilon = 0.5;
	private String TESTING_TYPE;
	private int sstSampleSize = 1000;
	private int LARModelSize;
	private double error_alpha;
	private double error_beta;
	private double confidence_inteval;
	private String data_delimiter;
	private int data_step_size;
	private boolean terminate_sample;
	private boolean selective_data_collection;
	
	
	public LAR() {
		super();
	}
	
	
	public void execute() throws ClassNotFoundException, IOException, UnsupportedLearningTypeException, UnsupportedTestingTypeException{
		
		int iteration = 0;
		while(iteration<maximumIteration){
			iteration++;
			TimeProfile.iteration_start_time = System.nanoTime();;
			System.out.println("\n****** Iteration : " + iteration + " ******\n");
			
			TimeProfile.learning_start_time = System.nanoTime();
			PredicateAbstraction pa = new PredicateAbstraction(property);
			Input data = pa.abstractInput(vvi.getVarsValues());
			
			
			String modelName = MODEL_NAME + "_" + iteration;
			DTMCLearner learner = new DTMCLearner();
			
			if(LEARNING_TYPE.equals("AA")){
				learner.setLearner(new AAlergia(0,2).selectCriterion(data));
			}
			else if(LEARNING_TYPE.equals("GA")){
				learner.setLearner(new LearnMergeEvolutions());
			}
			else{
				throw new UnsupportedLearningTypeException();
			}
			
			LearningDTMC bestDTMC = learner.getLearner();
			bestDTMC.learn(data);
			bestDTMC.PrismModelTranslation(data, pa.getPredicates(), modelName);
			System.out.println("------ Writing learned model into PRISM format ------");
			FormatPrismModel fpm = new FormatPrismModel("dtmc", OUTPUT_MODEL_PATH, modelName, true);
			fpm.translateToFormat(bestDTMC.getPrismModel(), data);
			System.out.println("- Learned model wrote to : " + OUTPUT_MODEL_PATH + "/" + modelName + ".pm");
			System.out.println("- Number of states in the learned model: " + bestDTMC.getPrismModel().getNumOfPrismStates());
			TimeProfile.learning_end_time = System.nanoTime();;
			TimeProfile.learning_times.add(TimeProfile.nanoToSeconds(TimeProfile.learning_end_time
					-TimeProfile.learning_start_time));
			
			// update LAR model size
			if(bestDTMC.getPrismModel().getNumOfPrismStates()==LARModelSize){
				System.out.println("====== Cannot obtain a new linear predicate, verification fails ======");
				TimeProfile.main_end_time = System.nanoTime();;
				TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
				TimeProfile.outputTimeProfile();
				TimeProfile.outputTimeProfile(GlobalConfigs.OUTPUT_MODEL_PATH+"/time_profile.txt");
				FileUtil.writeObject(Config.OUTPUT_MODEL_PATH + "/predicates", AlgoProfile.predicates);
				System.exit(0);
			}
			else{
				LARModelSize = bestDTMC.getPrismModel().getNumOfPrismStates();
			}
			
			TimeProfile.pmc_start_time = System.nanoTime();
			CheckLearned cl = new CheckLearned(OUTPUT_MODEL_PATH + "/" + modelName + ".pm" , 
					PROPERTY_LEARN_FILE, PROPERTY_INDEX);
			try {
				cl.check();
			} catch (PrismNoResultException e) {
				e.printStackTrace();
			}
			
			System.out.println("------ Generating counterexample ------");
			TimeProfile.ce_generation_start_time = System.nanoTime();
			CounterexampleGenerator counterg = new CounterexampleGenerator(bestDTMC.getPrismModel(),  // generate counterexamples
					boundedSteps, safetyBound);
			List<CounterexamplePath> counterPaths = counterg.generateCounterexamples();
			TimeProfile.ce_generation_end_time = System.nanoTime();;
			TimeProfile.ce_generation_times.add(TimeProfile.nanoToSeconds(TimeProfile.ce_generation_end_time
					-TimeProfile.ce_generation_start_time));
			
			System.out.println("------ Hypothesis testing of counterexample ------");
			te.init(property, sampler, data, data_delimiter, data_step_size);
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
			
			Counterexample ce = new Counterexample(bestDTMC.getPrismModel(), counterPaths, mt.getHypothesisTesting());
			ce.analyze(te);
			
			System.out.println("------ Refine the predicate set ------");
			
			TimeProfile.refine_start_time = System.nanoTime();
			Refiner refiner = new Refiner(ce.getSortedSplittingPoints(), vvi, property, bestDTMC.getPrismModel(), terminate_sample,
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
				FileUtil.writeObject(Config.OUTPUT_MODEL_PATH + "/predicates", AlgoProfile.predicates);
				TimeProfile.main_end_time = System.nanoTime();;
				TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
				TimeProfile.outputTimeProfile();
				TimeProfile.outputTimeProfile(GlobalConfigs.OUTPUT_MODEL_PATH+"/time_profile.txt");
				System.exit(0);
			}
			
			property.add(newPredicate);
			TimeProfile.iteration_end_time = System.nanoTime();
			TimeProfile.iteration_times.add(TimeProfile.nanoToSeconds(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time));
			AlgoProfile.predicates = property;
			AlgoProfile.newIteration = true;
			AlgoProfile.iterationCount++;
		}
		
	} 
	
	public String getDATA_PATH() {
		return DATA_PATH;
	}


	public void setDATA_PATH(String dATA_PATH) {
		DATA_PATH = dATA_PATH;
	}
	
	public VariablesValueInfo getVvi() {
		return vvi;
	}


	public void setVvi(VariablesValueInfo vvi) {
		this.vvi = vvi;
	}


	public List<Predicate> getProperty() {
		return property;
	}


	public void setProperty(List<Predicate> property) {
		this.property = property;
	}


	public int getMaximumIteration() {
		return maximumIteration;
	}


	public void setMaximumIteration(int maximumIteration) {
		this.maximumIteration = maximumIteration;
	}


	public TestEnvironment getTe() {
		return te;
	}


	public void setTe(TestEnvironment te) {
		this.te = te;
	}


	public Sampler getSampler() {
		return sampler;
	}


	public void setSampler(Sampler sampler) {
		this.sampler = sampler;
	}


	public String getOUTPUT_MODEL_PATH() {
		return OUTPUT_MODEL_PATH;
	}


	public void setOUTPUT_MODEL_PATH(String oUTPUT_MODEL_PATH) {
		OUTPUT_MODEL_PATH = oUTPUT_MODEL_PATH;
	}


	public String getMODEL_NAME() {
		return MODEL_NAME;
	}


	public void setMODEL_NAME(String mODEL_NAME) {
		MODEL_NAME = mODEL_NAME;
	}


	public String getLEARNING_TYPE() {
		return LEARNING_TYPE;
	}


	public void setLEARNING_TYPE(String lEARNING_TYPE) {
		LEARNING_TYPE = lEARNING_TYPE;
	}
	
	public double getAlergia_epsilon() {
		return alergia_epsilon;
	}


	public void setAlergia_epsilon(double alergia_epsilon) {
		this.alergia_epsilon = alergia_epsilon;
	}

	public int getLARModelSize() {
		return LARModelSize;
	}


	public void setLARModelSize(int lARModelSize) {
		LARModelSize = lARModelSize;
	}


	public int getPROPERTY_INDEX() {
		return PROPERTY_INDEX;
	}


	public void setPROPERTY_INDEX(int pROPERTY_INDEX) {
		PROPERTY_INDEX = pROPERTY_INDEX;
	}


	public String getPropertyLearnFile() {
		return PROPERTY_LEARN_FILE;
	}
	
	public String getPROPERTY_LEARN_FILE() {
		return PROPERTY_LEARN_FILE;
	}


	public void setPROPERTY_LEARN_FILE(String pROPERTY_LEARN_FILE) {
		PROPERTY_LEARN_FILE = pROPERTY_LEARN_FILE;
	}


	public int getBoundedSteps() {
		return boundedSteps;
	}


	public void setBoundedSteps(int boundedSteps) {
		this.boundedSteps = boundedSteps;
	}


	public double getSafetyBound() {
		return safetyBound;
	}


	public void setSafetyBound(double safetyBound) {
		this.safetyBound = safetyBound;
	}

	public double getError_alpha() {
		return error_alpha;
	}


	public double getError_beta() {
		return error_beta;
	}


	public void setError_alpha(double error_alpha) {
		this.error_alpha = error_alpha;
	}


	public void setError_beta(double error_beta) {
		this.error_beta = error_beta;
	}
	
	public double getConfidence_inteval() {
		return confidence_inteval;
	}


	public void setConfidence_inteval(double confidence_inteval) {
		this.confidence_inteval = confidence_inteval;
	}
	
	public String getData_delimiter() {
		return data_delimiter;
	}

	public void setData_delimiter(String data_delimiter) {
		this.data_delimiter = data_delimiter;
	}
	
	public int getData_step_size() {
		return data_step_size;
	}


	public void setData_step_size(int data_step_size) {
		this.data_step_size = data_step_size;
	}
	
	public String getTESTING_TYPE() {
		return TESTING_TYPE;
	}


	public void setTESTING_TYPE(String tESTING_TYPE) {
		TESTING_TYPE = tESTING_TYPE;
	}


	public int getSstSampleSize() {
		return sstSampleSize;
	}


	public void setSstSampleSize(int sstSampleSize) {
		this.sstSampleSize = sstSampleSize;
	}
	
	public void setTerminateSample(boolean b){
		this.terminate_sample = b;
	}
	
	public void setSelectiveDataCollection(boolean b){
		this.selective_data_collection = b;
	}
}
