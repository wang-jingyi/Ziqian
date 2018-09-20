package io.github.wang_jingyi.ZiQian.data;

import io.github.wang_jingyi.ZiQian.FormatVariablesValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjingyi
 *
 */
public class ExtractPrismData implements FormatVariablesValue{
	
	private String dataPath; // path of data in prism format
	private int stepSize; // sample size
	private int dataSize; // total number of data
	private String delimiter; // delimiter of data file
	private boolean random_length;
	
	public ExtractPrismData(String datapath, int datasize, int stepSize, String delimiter, boolean random_length) {
		this.dataPath = datapath;
		this.dataSize = datasize;
		this.stepSize = stepSize;
		this.delimiter = delimiter;
		this.random_length = random_length;
	}

	@Override
	public VariablesValueInfo getVariablesValueInfo() throws IOException {
		List<String> vars = PrismPathData.extractPathVars(dataPath, delimiter);
		List<List<VariablesValue>> vvs = PrismPathData.extractMEData(dataPath, vars, dataSize, stepSize, delimiter, random_length);
		List<Integer> varsLength = PrismPathData.updateMEVarsLength(vvs);
		return new VariablesValueInfo(vars, varsLength, vvs);
	}
	
	
	// extract specific variables information
	public VariablesValueInfo getVariablesValueInfo(List<String> vars) throws IOException {
		List<List<VariablesValue>> vvs = PrismPathData.extractMEData(dataPath, vars, dataSize, stepSize, delimiter, random_length);
		List<Integer> varsLength = new ArrayList<>();
//				PrismPathData.updateMEVarsLength(vvs);
		return new VariablesValueInfo(vars, varsLength, vvs);
	}

}
