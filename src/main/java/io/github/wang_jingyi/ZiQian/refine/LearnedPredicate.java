package io.github.wang_jingyi.ZiQian.refine;

import java.io.Serializable;
import java.util.List;

import io.github.wang_jingyi.ZiQian.Predicate;
import io.github.wang_jingyi.ZiQian.VariablesValue;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

public class LearnedPredicate implements Predicate, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2900688502055037029L;
	private Classifier classifier;
	private String predicateName;
	
	@Override
	public String toString() {
		return "LearnedPredicate [classifier=" + classifier + ", predicateName=" + predicateName + ", vars=" + vars
				+ "]";
	}

	private List<String> vars;
	
	public LearnedPredicate(Classifier cls, String pn, List<String> vars){
		this.classifier = cls;
		this.predicateName = pn;
		this.vars = vars;
	}
	
	@Override
	public boolean check(VariablesValue vv){ // the size of vars should be same with feature size of an instance in the classifier
		
		double[] values = new double[vv.getVars().size()];
		for(int i=0; i<vv.getValues().size(); i++){
			values[i] = vv.getValues().get(i).getRawDoubleValue();
		}
		
		Instance ins = new DenseInstance(values);
		
		if(classifier.classify(ins).equals("positive")){
			return true;
		}
		return false;
	}

	@Override
	public String getPredicateName() {
		return predicateName;
	}

	@Override
	public List<String> getVariables() {
		return vars;
	}
	
}
