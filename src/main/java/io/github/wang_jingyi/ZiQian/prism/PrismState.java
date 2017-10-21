package io.github.wang_jingyi.ZiQian.prism;

import java.util.ArrayList;
import java.util.List;

public class PrismState {

	private int id;
	private List<String> label;
	private List<PrismState> nextStates = new ArrayList<PrismState>();
	private List<Double> transitionProb = new ArrayList<Double>();
	private List<String> tranProbInString = new ArrayList<String>();
	private List<String> sigmas = new ArrayList<String>();

	public PrismState(int id, List<String> label){
		this.id = id;
		this.label = label;
	}

	// since label is a list of observations including history,
	// the last string of the label will be the current observation
	public String getCurrentState(){ 
		int labelLength = label.size();
		if(labelLength==0){
			return new String();
		}
		return label.get(labelLength-1);
	}
	

	@Override
	public String toString() {
		return "PrismState [id=" + id + ", label=" + label + ", transitionProb=" + transitionProb + ", sigmas=" + sigmas
				+ "]";
	}

	public List<String> getLabel() {
		return label;
	}

	public void setLabel(List<String> label) {
		this.label = label;
	}

	public List<PrismState> getNextStates() {
		return nextStates;
	}

	public void setNextStates(List<PrismState> nextStates) {
		this.nextStates = nextStates;
	}

	public List<Double> getTransitionProb() {
		return transitionProb;
	}

	public void setTransitionProb(List<Double> transitionProb) {
		this.transitionProb = transitionProb;
	}

	public Object clone() throws CloneNotSupportedException{
		PrismState newState = (PrismState) super.clone();
		return newState;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public List<String> getTranProbInString() {
		return tranProbInString;
	}

	public void setTranProbInString(List<String> tranProbInString) {
		this.tranProbInString = tranProbInString;
	}
	
	public List<String> getSigmas() {
		return sigmas;
	}

	public void setSigmas(List<String> sigmas) {
		this.sigmas = sigmas;
	}


}
