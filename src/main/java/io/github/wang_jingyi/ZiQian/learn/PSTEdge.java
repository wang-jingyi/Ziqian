package io.github.wang_jingyi.ZiQian.learn;


public class PSTEdge {
	
	private String label;
	private PSTNode destPSTNode;
	private double transProb;
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public PSTNode getDestPSTNode() {
		return destPSTNode;
	}

	public void setDestPSTNode(PSTNode destPSTNode) {
		this.destPSTNode = destPSTNode;
	}

	public double getTransProb() {
		return transProb;
	}

	public void setTransProb(double transProb) {
		this.transProb = transProb;
	}

	
	public PSTEdge(String label, PSTNode node, double transProb){
		this.label = label;
		this.destPSTNode = node;
		this.transProb = transProb;
	}
	
	public PSTEdge(PSTNode node){ // when extending a node
		this.destPSTNode = node;
	}

	@Override
	public String toString() {
		return "PSTEdge [label=" + label + ", destPSTNode=" + destPSTNode + ", transProb=" + transProb + "]";
	}
	
	
	
}
