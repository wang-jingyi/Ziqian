package io.github.wang_jingyi.ZiQian.active;

import java.util.HashMap;
import java.util.Map;

public class TransitionPoint {
	
	private int row;
	private int column;
	
	public TransitionPoint(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	public static void main(String[] args){
		Map<TransitionPoint, Double> tm = new HashMap<TransitionPoint, Double>();
		TransitionPoint tp0 = new TransitionPoint(1, 1);
		tm.put(tp0, 0.1);
		System.out.println("hashcode0: " + tp0.hashCode());
		TransitionPoint tp = new TransitionPoint(1, 1);
		System.out.println("hashcode1: " + tp.hashCode());
		System.out.println("equals: " + tp0.equals(tp));
		System.out.println("contains: " + tm.containsKey(tp));
		tm.put(tp, 0.2);
		System.out.println(tm);
	}
	

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[index: " + row + "," + column + "]");
		return sb.toString();
	}

	public boolean equals(TransitionPoint rt){
		if(rt.row == this.row && rt.column == this.column){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransitionPoint other = (TransitionPoint) obj;
		if (column != other.column)
			return false;
		if (row != other.row)
			return false;
		return true;
	}

}
