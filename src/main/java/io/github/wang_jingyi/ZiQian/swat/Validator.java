package io.github.wang_jingyi.ZiQian.swat;

import java.util.List;

import io.github.wang_jingyi.ZiQian.main.SwatConfig;

public class Validator {
	
	List<String> training_log;
	List<String> testing_log;
	double training_unsafe_prob;
	double testing_unsafe_prob;
	double confidence;
	double safe_thres;
	
	public double getTesting_unsafe_prob() {
		return testing_unsafe_prob;
	}

	public double getConfidence() {
		return confidence;
	}

	public double getSafe_thres() {
		return safe_thres;
	}
	
	public Validator(List<String> training_log, List<String> testing_log){
		this.training_log = training_log;
		this.testing_log = testing_log;
		this.training_unsafe_prob = computeTrainingProb();
		this.testing_unsafe_prob = computeTestingProb();
		this.confidence = computeConfidence();
		this.safe_thres = training_unsafe_prob * (1+SwatConfig.SAFETY_THRESHOLD);
	}

	public Validator(double safe_thres, List<String> training_log, List<String> testing_log) {
		this.safe_thres = safe_thres;
		this.training_log = training_log;
		this.testing_log = testing_log;
		training_unsafe_prob = computeTrainingProb();
		testing_unsafe_prob = computeTestingProb();
		confidence = computeConfidence();
	}
	
	private double computeTrainingProb() {
		int count = 0;
		for(String entry : training_log){
			if(entry.startsWith("11")){
				count++;
			}
		}
		double training_unsafe_prob = (double)count/training_log.size();
//		System.out.println("--- unsafe probability in the training log: " + training_unsafe_prob);
		return training_unsafe_prob;
	}

	public double getTraining_unsafe_prob() {
		return training_unsafe_prob;
	}

	private double computeConfidence(){
		return 1;
	}
	

	private double computeTestingProb(){
		
		int count = 0;
		for(String entry : testing_log){
			if(entry.startsWith("11")){
				count++;
			}
		}
		double testing_unsafe_prob = (double)count/testing_log.size();
//		System.out.println("--- unsafe probability in the testing log: " + testing_unsafe_prob);
		return testing_unsafe_prob;
	}
	
	
	@Override
	public String toString() {
		return "Validator [training_unsafe_prob=" + training_unsafe_prob + ", testing_unsafe_prob="
				+ testing_unsafe_prob + ", confidence=" + confidence + ", safe_thres=" + safe_thres + "]";
	}

	public boolean isSpurious(double learned_unsafe_prob){
		if(testing_unsafe_prob>=learned_unsafe_prob){
			return false;
		}
		return true;
	}
	

}
