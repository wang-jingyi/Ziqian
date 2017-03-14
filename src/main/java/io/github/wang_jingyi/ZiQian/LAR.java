package io.github.wang_jingyi.ZiQian;

import io.github.wang_jingyi.ZiQian.evolution.LearnMergeEvolutions;
import io.github.wang_jingyi.ZiQian.exceptions.PrismNoResultException;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedLearningTypeException;
import io.github.wang_jingyi.ZiQian.learn.AAlergia;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.refine.Refiner;
import io.github.wang_jingyi.ZiQian.sample.Counterexample;
import io.github.wang_jingyi.ZiQian.sample.CounterexampleGenerator;
import io.github.wang_jingyi.ZiQian.sample.CounterexamplePath;
import io.github.wang_jingyi.ZiQian.sample.HypothesisTest;
import io.github.wang_jingyi.ZiQian.sample.Sampler;
import io.github.wang_jingyi.ZiQian.sample.SprtTest;
import io.github.wang_jingyi.ZiQian.sample.TestEnvironment;

import java.io.IOException;
import java.util.List;

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
	private int LARModelSize;
	
	
	public LAR() {
		super();
	}
	
	
	public void execute() throws ClassNotFoundException, IOException, UnsupportedLearningTypeException{
		
		int iteration = 0;
		while(iteration<maximumIteration){
			
			PredicateAbstraction pa = new PredicateAbstraction(property);
			Input data = pa.abstractInput(vvi.getVarsValues());
			
			String modelName = MODEL_NAME + "_" + iteration;
			DTMCLearner learner = new DTMCLearner();
			
			if(LEARNING_TYPE.equals("AA")){
				learner.setLearner(new AAlergia(Math.pow(2, -6), Math.pow(2, 6)).selectCriterion(data));
			}
			else if(LEARNING_TYPE.equals("GA")){
				learner.setLearner(new LearnMergeEvolutions());
				learner.getLearner().learn(data);
			}
			else{
				throw new UnsupportedLearningTypeException();
			}
			
			LearningDTMC bestDTMC = learner.getLearner();
			bestDTMC.PrismModelTranslation(data, pa.getPredicates(), modelName);
			System.out.println("formatting the model to .pm file for model checking...");
			FormatPrismModel fpm = new FormatPrismModel("dtmc", OUTPUT_MODEL_PATH, modelName);
			fpm.translateToFormat(bestDTMC.getPrismModel(), data);
			
			// update LAR model size
			if(bestDTMC.getPrismModel().getNumOfPrismStates()==LARModelSize){
				System.out.println("cannot split states any more, verification fails.");
				System.exit(0);
			}
			else{
				LARModelSize = bestDTMC.getPrismModel().getNumOfPrismStates();
			}
			
			CheckLearned cl = new CheckLearned(OUTPUT_MODEL_PATH + "/" + modelName + ".pm" , 
					PROPERTY_LEARN_FILE, PROPERTY_INDEX);
			try {
				cl.check();
			} catch (PrismNoResultException e) {
				e.printStackTrace();
			}
			
			CounterexampleGenerator counterg = new CounterexampleGenerator(bestDTMC.getPrismModel(),  // generate counterexamples
					boundedSteps, safetyBound);
			List<CounterexamplePath> counterPaths = counterg.generateCounterexamples();
			
			System.out.println("hypothesis testing...");
			te.init(property, sampler);
//			HypothesisTest sst = new SingleSampleTest(1);
			HypothesisTest sst = new SprtTest(0.2, 0.1, 0.1, 0.1);
			Counterexample ce = new Counterexample(bestDTMC.getPrismModel(), counterPaths, sst);
			System.out.println("analyzing counterexample...");
			ce.analyze(te);
			
			System.out.println("refine the predicate set...");
			
			Refiner refiner = new Refiner(ce.getSortedSplittingPoints(), vvi, property, bestDTMC.getPrismModel());
//			List<String> dataPaths = new ArrayList<>();
//			dataPaths.add(Config.DATA_PATH);
//			dataPaths.add(Config.TESTING_PATH);
			
//			Dataset ds = refiner.collectDataFromPaths(dataPaths, ps.getPredicates(), 
//					ce.getSortedSplittingPoints(), bestDTMC.getPrismModel());
			Predicate newPredicate = refiner.refine();
			
			if(newPredicate==null){
				System.out.println("fail to learn a new predicate...");
				System.exit(0);
			}
			
			property.add(newPredicate);
			AlgoProfile.predicates = property;
			AlgoProfile.newIteration = true;
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



}
