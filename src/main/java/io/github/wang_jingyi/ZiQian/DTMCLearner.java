package io.github.wang_jingyi.ZiQian;

import io.github.wang_jingyi.ZiQian.learn.LearningDTMC;

public class DTMCLearner {
	
	private LearningDTMC learner;
	
	public DTMCLearner() {
	}
	
	public DTMCLearner(LearningDTMC learner) {
		this.learner = learner;
	}

	public void setLearner(LearningDTMC learner) {
		this.learner = learner;
	}

	public LearningDTMC getLearner() {
		return learner;
	}

}
