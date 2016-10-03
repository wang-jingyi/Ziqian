package io.github.wang_jingyi.ZiQian.prism;

import io.github.wang_jingyi.ZiQian.Predicate;

import java.util.List;

public class PrismModel {

	private List<PrismState> prismStates;
	private int numOfPrismStates;
	private List<PrismState> initialStates;
	private List<Predicate> predicates;
	
	
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
