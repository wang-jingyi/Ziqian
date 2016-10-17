package io.github.wang_jingyi.ZiQian.active;

import java.io.Serializable;

public class Interval implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7417585182624900729L;
	public int start;
	public int end;
	Interval() { start = 0; end = 0; }
	Interval(int s, int e) { start = s; end = e; }
	
	@Override
	public String toString() {
		return "Interval [start=" + start + ", end=" + end + "]";
	}
	
}
