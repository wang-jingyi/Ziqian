package io.github.wang_jingyi.ZiQian.sample;

import com.google.common.base.Objects;

/*
 * a point in the counterexample path to split a state
 * 
 * */

public class SplittingPoint {
	
	private int currentStateId;
	private int nextStateId;
	
	public SplittingPoint(int ci, int ni) {
		this.currentStateId = ci;
		this.nextStateId = ni;
	}
	
	public SplittingPoint() {
		super();
	}
	

	public int getCurrentStateId() {
		return currentStateId;
	}

	public int getNextStateId() {
		return nextStateId;
	}
	
	@Override
	public SplittingPoint clone(){
		return new SplittingPoint(currentStateId,nextStateId);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[" + currentStateId + "," + nextStateId + "]");
		return sb.toString();
	}
	
	@Override
    public int hashCode(){
        return Objects.hashCode(this.currentStateId, this.nextStateId);
    }
	
	@Override
	public boolean equals(Object obj){
		if ( !(obj instanceof SplittingPoint)) {
            return false;
        }
		
		SplittingPoint other = (SplittingPoint)obj;
		
		if(currentStateId==other.currentStateId && nextStateId==other.nextStateId){
			return true;
		}
		return false;
	}
}
