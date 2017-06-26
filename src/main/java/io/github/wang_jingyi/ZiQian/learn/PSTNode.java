package io.github.wang_jingyi.ZiQian.learn;

import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PSTNode {
	private List<String> label; // label of the node
	private List<PSTEdge> edges; // outgoing edges
	private boolean isLeaf = false;
	private boolean extending = false;
	
	
	public PSTNode nextNode(String sigma){
		for(PSTEdge edge : edges){
			if(edge.getLabel().equals(sigma)){
				return edge.getDestPSTNode();
			}
		}
		return null;
	}
	
	public boolean isExtending() {
		return extending;
	}
	public void setExtending(boolean extending) {
		this.extending = extending;
	}
	public boolean isLeaf() {
		return isLeaf;
	}
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	public List<String> getLabel() {
		return label;
	}
	public void setLabel(List<String> label) {
		this.label = label;
	}
	public List<PSTEdge> getPSTEdges() {
		return edges;
	}
	public void setPSTEdges(List<PSTEdge> edges) {
		this.edges = edges;
	}
	
	public PSTNode(List<String> label, List<PSTEdge> edges){
		this.label = label;
		this.edges = edges;
	}
	
	public PSTNode(){
		this.edges = new ArrayList<PSTEdge>();
		this.label = new ArrayList<String>();
	}
	
	
	@Override
	public String toString() {
		return "PSTNode [label=" + label + ", edges=" + edges + ", isLeaf=" + isLeaf + ", extending=" + extending + "]";
	}

	public boolean equal(PSTNode compared){
		return StringUtil.equals(this.getLabel(), compared.getLabel());
	}
}
