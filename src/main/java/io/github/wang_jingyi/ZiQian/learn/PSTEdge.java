package io.github.wang_jingyi.ZiQian.learn;


public class PSTEdge {
	
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

	private String label;
	private PSTNode destPSTNode;
	private double transProb;
	
	
	public PSTEdge(String label, PSTNode node, double transProb){
		this.label = label;
		this.destPSTNode = node;
		this.transProb = transProb;
	}
	
	public PSTEdge(PSTNode node){ // when extending a node
		this.destPSTNode = node;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		if(label==null){
			sb.append("[ edge: label: empty" + "; transiton probability: " + transProb + "; destination node:" + 
					destPSTNode.toString() + " ]");
		}
		else{
			sb.append("[ edge: label: " + label.toString() + "; transiton probability: " + transProb + 
					"; destination node:" + destPSTNode.toString() + " ]");
		}
		
		
		return sb.toString();
	}
}
