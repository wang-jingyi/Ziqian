package io.github.wang_jingyi.ZiQian.prism;

import io.github.wang_jingyi.ZiQian.Predicate;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

public class PrismModel {

	private List<PrismState> prismStates;
	private int numOfPrismStates;
	private List<PrismState> initialStates;
	private List<Double> initialDistribution;
	private List<Predicate> predicates;
	private RealMatrix transition_matrix;
	
	public RealMatrix getTransitionMatrix(){
		return transition_matrix;
	}
	
	public List<Double> getInitialDistribution() {
		return initialDistribution;
	}

	public void setInitialDistribution(List<Double> initialDistribution) {
		this.initialDistribution = initialDistribution;
	}

	public RealMatrix getTransition_matrix() {
		return transition_matrix;
	}

	public void setTransitionMatrix(RealMatrix transition_matrix){
		this.transition_matrix = transition_matrix;
	}
	
	public List<Predicate> getPredicates() {
		return predicates;
	}
	public void setPredicates(List<Predicate> predicates) {
		this.predicates = predicates;
	}
	public List<PrismState> getPrismStates() {
		return prismStates;
	}
	public void setPrismStates(List<PrismState> prismStates) {
		this.prismStates = prismStates;
	}
	public int getNumOfPrismStates() {
		return numOfPrismStates;
	}
	public void setNumOfPrismStates(int numOfPrismStates) {
		this.numOfPrismStates = numOfPrismStates;
	}
	public List<PrismState> getInitialStates() {
		return initialStates;
	}
	public void setInitialStates(List<PrismState> initialStates) {
		this.initialStates = initialStates;
	}
	
	
}
