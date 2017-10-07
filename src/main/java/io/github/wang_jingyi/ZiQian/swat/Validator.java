package io.github.wang_jingyi.ZiQian.swat;

import java.util.List;

public class Validator {
	
	List<String> testing_log;
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

	public Validator(double safe_thres, List<String> testing_log) {
		this.safe_thres = safe_thres;
		this.testing_log = testing_log;
		testing_unsafe_prob = computeTestingProb();
		confidence = computeConfidence();
	}
	
	private double computeConfidence(){
		return 1;
	}
	
	@Override
	public String toString() {
		return "Validator [testing_unsafe_prob=" + testing_unsafe_prob + ", confidence=" + confidence + ", safe_thres="
				+ safe_thres + "]";
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
	
	
	public boolean isSpurious(double learned_unsafe_prob){
		if(testing_unsafe_prob>=learned_unsafe_prob){
			return false;
		}
		return true;
	}
	

}
