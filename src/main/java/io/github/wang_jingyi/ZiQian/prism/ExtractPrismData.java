package io.github.wang_jingyi.ZiQian.prism;

import io.github.wang_jingyi.ZiQian.FormatVariablesValue;
import io.github.wang_jingyi.ZiQian.VariablesValue;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;

import java.io.IOException;
import java.util.List;

public class ExtractPrismData implements FormatVariablesValue{
	
	private String dataPath;
	private int dataSize;
	
	public ExtractPrismData(String datapath, int datasize) {
		this.dataPath = datapath;
		this.dataSize = datasize;
	}

	@Override
	public VariablesValueInfo getVariablesValueInfo() throws IOException {
		List<String> vars = PrismPathData.extractPathVars(dataPath);
		List<List<VariablesValue>> vvs = PrismPathData.extractMEData(dataPath, vars, dataSize);
		List<Integer> varsLength = PrismPathData.updateMEVarsLength(vvs);
		return new VariablesValueInfo(vars, varsLength, vvs);
	}
	
	
	// extract specific variables information
	public VariablesValueInfo getVariablesValueInfo(List<String> vars) throws IOException {
		List<List<VariablesValue>> vvs = PrismPathData.extractMEData(dataPath, vars, dataSize);
		List<Integer> varsLength = PrismPathData.updateMEVarsLength(vvs);
		return new VariablesValueInfo(vars, varsLength, vvs);
	}

}
