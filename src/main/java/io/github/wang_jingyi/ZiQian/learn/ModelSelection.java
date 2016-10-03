package io.github.wang_jingyi.ZiQian.learn;

import io.github.wang_jingyi.ZiQian.Input;

import java.io.FileNotFoundException;
import java.io.IOException;


/*
 * Interface for model selection
 * Users need to provide the InputData to learn and specify the learning algorithm to use
 * */

public interface ModelSelection {
	
	public LearningDTMC selectCriterion(Input data) throws FileNotFoundException, ClassNotFoundException, IOException;

}
