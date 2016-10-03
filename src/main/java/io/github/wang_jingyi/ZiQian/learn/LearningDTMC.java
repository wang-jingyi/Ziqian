package io.github.wang_jingyi.ZiQian.learn;

import io.github.wang_jingyi.ZiQian.Input;
import io.github.wang_jingyi.ZiQian.PredicateSet;
import io.github.wang_jingyi.ZiQian.prism.PrismModel;

import java.io.FileNotFoundException;
import java.io.IOException;


/*
 * a learning algorithm should implement:
 * 1. learn given InputData
 * 2. translate a learned model to a PrismModel
 * 3. access to the translated PrismModel for next phase
 * */

public interface LearningDTMC {
	
	public void learn(Input data) throws FileNotFoundException, IOException, ClassNotFoundException;
	public void PrismModelTranslation(Input data, PredicateSet ps, String modelName);
	public PrismModel getPrismModel();
	
}
