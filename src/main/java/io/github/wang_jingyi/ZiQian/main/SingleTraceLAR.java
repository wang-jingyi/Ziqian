package io.github.wang_jingyi.ZiQian.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.data.SWaTInput;
import io.github.wang_jingyi.ZiQian.exceptions.UnsupportedTestingTypeException;
import io.github.wang_jingyi.ZiQian.learn.LearnPST;
import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;
import io.github.wang_jingyi.ZiQian.prism.FormatPrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;
import io.github.wang_jingyi.ZiQian.prism.PrismState;
import io.github.wang_jingyi.ZiQian.refine.Refiner;
import io.github.wang_jingyi.ZiQian.refine.SplittingPoint;
import io.github.wang_jingyi.ZiQian.swat.SAnalyzer;
import io.github.wang_jingyi.ZiQian.swat.SWaTIterationResult;
import io.github.wang_jingyi.ZiQian.swat.Validator;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

public class SingleTraceLAR {

	private List<Predicate> property;
	private List<Predicate> predicate_set;
	private int maximumIteration = 20;
	private String TRAINING_LOG_PATH;
	private String TESTING_LOG_PATH;
	private String OUTPUT_MODEL_PATH;
	private String MODEL_NAME;
	private int LARModelSize;
	private int previous_count = 50;
	private String data_delimiter;
	private int data_step_size;
	private int data_size;
	private boolean terminate_sample;
	private boolean selective_data_collection;
	private double epsilon = 0.00000001;


	public void execute() throws FileNotFoundException, ClassNotFoundException, IOException, UnsupportedTestingTypeException{

		int iteration = 0;
		while(iteration<maximumIteration){
			
			System.out.println("============ iteration " + (iteration+1) + " ============");
			SWaTIterationResult iteration_result = new SWaTIterationResult();
			iteration_result.setIteration(iteration+1);
			
			
			AlgoProfile.iterationCount = iteration + 1;
			AlgoProfile.predicates = predicate_set;
			
			TimeProfile.iteration_start_time = System.nanoTime();
			
			System.out.print("prepare data for learning...   ");
			SWaTInput input = new SWaTInput(TRAINING_LOG_PATH, TESTING_LOG_PATH, predicate_set, previous_count, 
					data_size, data_step_size, data_delimiter);
			input.execute();
			
			Validator val = new Validator(input.getAbstractTrainingInput().getObservations().get(0), input.getAbstractTestingInput());
			double safety_thres = val.getSafe_thres();
			iteration_result.setTraining_unsafe_prob(val.getTraining_unsafe_prob());
			iteration_result.setTest_unsafe_prob(val.getTesting_unsafe_prob());

			AlgoProfile.vars = input.getTraining_vvi().getVars();	
			AlgoProfile.varsLength = input.getTraining_vvi().getVarsLength();

			String modelName = MODEL_NAME + "_" + iteration;

			// learning
			System.out.print("learn...   ");
			TimeProfile.learning_start_time = System.nanoTime();
			
			LearningDTMC learner = 
//					new PSTGoldenSearch(1e-2, 2).selectCriterion(input.getAbstractTrainingInput()); 
					new LearnPST(epsilon);
			learner.learn(input.getAbstractTrainingInput());
			learner.PrismModelTranslation(input.getAbstractTrainingInput(), predicate_set, modelName);
//			identifyInitialStates(learner.getPrismModel(), input.getPreviousObservation());
			PrismModel bestDTMC = learner.getPrismModel();
			
			iteration_result.setNumber_of_state(bestDTMC.getNumOfPrismStates());
			
			// update LAR model size
			if(bestDTMC.getNumOfPrismStates()==LARModelSize){
				System.out.println("\n" + iteration_result);
				System.out.println("\n====== Cannot obtain a new linear predicate, verification fails ======");
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
//			System.out.println("write learned model into PRISM format...   ");
			FormatPrismModel fpm = new FormatPrismModel("dtmc", OUTPUT_MODEL_PATH, modelName, true);
			fpm.translateToFormat(learner.getPrismModel(),input.getAbstractTrainingInput());
//			System.out.println("- Learned model wrote to : " + OUTPUT_MODEL_PATH + "/" + modelName + ".pm");
//			System.out.println("- Number of states in the learned model: " + bestDTMC.getNumOfPrismStates());
			TimeProfile.learning_end_time = System.nanoTime();
			TimeProfile.learning_times.add(TimeProfile.nanoToSeconds(TimeProfile.learning_end_time
					-TimeProfile.learning_start_time));
			
			
			// verify the safety property against the model
			TimeProfile.pmc_start_time = System.nanoTime();
			
			double model_unsafe_prob = 0;
			for(PrismState state : bestDTMC.getPrismStates()){
				if(state.getCurrentState().startsWith("11")){
					model_unsafe_prob += bestDTMC.getInitialDistribution().get(state.getId()-1);
				}
			}
			
			
			iteration_result.setLearned_unsafe_prob(model_unsafe_prob);
			
			TimeProfile.pmc_end_time = System.nanoTime();
			
			if(model_unsafe_prob<safety_thres){
				TimeProfile.iteration_end_time = System.nanoTime();
				TimeProfile.iteration_times.add(TimeProfile.nanoToSeconds
						(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time));
				double iteration_time = TimeProfile.nanoToSeconds(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time);
				iteration_result.setIteration_time(iteration_time);
				System.out.println("\n" + iteration_result);
				System.out.println("\n====== property verified ======");
				AlgoProfile.result = "true";
				FileUtil.writeObject(OUTPUT_MODEL_PATH + "/predicates", AlgoProfile.predicates);
				TimeProfile.main_end_time = System.nanoTime();;
				TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
				TimeProfile.outputTimeProfile();
				TimeProfile.outputTimeProfile(GlobalConfigs.OUTPUT_MODEL_PATH+"/time_profile.txt");
				System.exit(0);
			}
			
			// validate if the learned model is spurious
			
			System.out.print("valildate the learned result...   ");
			
			TimeProfile.spurious_start_time= System.nanoTime();
			
			System.out.print("look for spurious transitions...   ");
			
			TimeProfile.spurious_end_time = System.nanoTime();
			TimeProfile.spurious_check_times.add(TimeProfile.nanoToSeconds(TimeProfile.spurious_end_time
					-TimeProfile.spurious_start_time));
			
			if(!val.isSpurious(model_unsafe_prob)){
				TimeProfile.iteration_end_time = System.nanoTime();
				double iteration_time = TimeProfile.nanoToSeconds(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time);
				TimeProfile.iteration_times.add(TimeProfile.nanoToSeconds
						(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time));
				iteration_result.setIteration_time(iteration_time);
				System.out.println("\n" + iteration_result);
				System.out.println("\n======= property violated ======");
				FileUtil.writeObject(OUTPUT_MODEL_PATH + "/predicates", AlgoProfile.predicates);
				TimeProfile.main_end_time = System.nanoTime();;
				TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
				TimeProfile.outputTimeProfile();
				TimeProfile.outputTimeProfile(GlobalConfigs.OUTPUT_MODEL_PATH+"/time_profile.txt");
				System.exit(0);
			}
			
			TimeProfile.ce_generation_start_time = System.nanoTime();
			SAnalyzer analyzer = new SAnalyzer(input.getAbstractTestingInput());
			List<SplittingPoint> sps = analyzer.findSplitingStates(bestDTMC);
			TimeProfile.ce_generation_end_time = System.nanoTime();
			TimeProfile.ce_generation_times.add(TimeProfile.nanoToSeconds(TimeProfile.ce_generation_end_time
					-TimeProfile.ce_generation_start_time));

			// refinement
			System.out.print("------ refine the abstraction...   ");
			TimeProfile.refine_start_time = System.nanoTime();
			Refiner refiner = new Refiner(sps, input.getTraining_vvi(), input.getPredicates(), bestDTMC, terminate_sample,
					selective_data_collection); // we should use the training data to refine the abstraction
			Predicate newPredicate = refiner.refine();
			TimeProfile.refine_end_time = System.nanoTime();
			TimeProfile.refine_times.add(TimeProfile.nanoToSeconds(TimeProfile.refine_end_time
					-TimeProfile.refine_start_time));

			if(newPredicate==null){
				TimeProfile.iteration_end_time = System.nanoTime();
				TimeProfile.iteration_times.add(TimeProfile.nanoToSeconds
						(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time));
				double iteration_time = TimeProfile.nanoToSeconds(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time);
				iteration_result.setIteration_time(iteration_time);
				System.out.println("\n" + iteration_result);
				System.out.println("\n======= Fail to learn a new predicate, verification fails ======");
				FileUtil.writeObject(OUTPUT_MODEL_PATH + "/predicates", AlgoProfile.predicates);
				TimeProfile.main_end_time = System.nanoTime();;
				TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
				TimeProfile.outputTimeProfile();
				TimeProfile.outputTimeProfile(GlobalConfigs.OUTPUT_MODEL_PATH+"/time_profile.txt");
				System.exit(0);
			}

			predicate_set.add(newPredicate);
			TimeProfile.iteration_end_time = System.nanoTime();
			double iteration_time = TimeProfile.nanoToSeconds(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time);
			TimeProfile.iteration_times.add(TimeProfile.nanoToSeconds
					(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time));
			iteration_result.setIteration_time(iteration_time);
			
			System.out.println("\n" + iteration_result);
			
			AlgoProfile.predicates = property;
			AlgoProfile.newIteration = true;
			AlgoProfile.iterationCount++;
			iteration ++;
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


	public void setTRAINING_LOG_PATH(String tRAINING_LOG_PATH) {
		TRAINING_LOG_PATH = tRAINING_LOG_PATH;
	}

	public void setTESTING_LOG_PATH(String tESTING_LOG_PATH) {
		TESTING_LOG_PATH = tESTING_LOG_PATH;
	}

	public void setOUTPUT_MODEL_PATH(String oUTPUT_MODEL_PATH) {
		OUTPUT_MODEL_PATH = oUTPUT_MODEL_PATH;
	}


	public void setMODEL_NAME(String mODEL_NAME) {
		MODEL_NAME = mODEL_NAME;
	}


	public void setLARModelSize(int lARModelSize) {
		LARModelSize = lARModelSize;
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

//	private void identifyInitialStates(PrismModel model, List<String> previous_observation){
//
//		List<PrismState> states = model.getPrismStates();
//		List<PrismState> iss = new ArrayList<>();
//		for(PrismState state : states){
//			if(StringUtil.isSuffix(state.getLabel(), previous_observation)){
//				iss.add(state);
//			}
//		}
//		model.setInitialStates(iss);
//	}
}
