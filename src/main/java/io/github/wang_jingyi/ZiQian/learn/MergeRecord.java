package io.github.wang_jingyi.ZiQian.learn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MergeRecord implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1685454053486466573L;
	private DataPrefix dp;
	private List<HashSet<Integer>> groups = new ArrayList<>();
	private Map<Integer, HashSet<Integer>> idMaps = new HashMap<>();
	
	public MergeRecord(DataPrefix dp) {
		this.dp = dp;
	}
	
	public Map<Integer, HashSet<Integer>> getIdMaps() {
		return idMaps;
	}

	public void setIdMaps(Map<Integer, HashSet<Integer>> idMaps) {
		this.idMaps = idMaps;
	}

	public DataPrefix getDp() {
		return dp;
	}

	public void setDp(DataPrefix dp) {
		this.dp = dp;
	}

	public List<HashSet<Integer>> getGroups() {
		return groups;
	}

	public void setGroups(List<HashSet<Integer>> groups) {
		this.groups = groups;
	}
}
