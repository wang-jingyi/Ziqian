package io.github.wang_jingyi.ZiQian;

import java.io.Serializable;
import java.util.List;

public class Input implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7964318177771019699L;
	private List<String> alphabet;
	private List<List<String>> observations;
	private int dataSize;
	
	public Input(List<String> alpha, List<List<String>> observations){
		this.alphabet = alpha;
		this.observations = observations;
		this.dataSize = getInputSize();
	}
	
	@Override
	public String toString() {
		return "Input [alphabet=" + alphabet + ", observations=" + observations
				+ ", dataSize=" + dataSize + "]";
	}

	public List<String> getAlphabet() {
		return alphabet;
	}
	public List<List<String>> getObservations() {
		return observations;
	}
	
	public int getDataSize() {
		return dataSize;
	}

	private int getInputSize(){
		int ds = 0;
		for(List<String> obs : observations){
			ds += obs.size();
		}
		return ds;
	}
	
}
