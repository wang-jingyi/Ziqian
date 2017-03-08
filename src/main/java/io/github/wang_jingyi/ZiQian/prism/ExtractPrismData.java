package io.github.wang_jingyi.ZiQian.prism;

import io.github.wang_jingyi.ZiQian.FormatVariablesValue;
import io.github.wang_jingyi.ZiQian.VariablesValue;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.run.Config;

import java.io.IOException;
import java.util.List;

public class ExtractPrismData implements FormatVariablesValue{
	
	private String dataPath;
	private int stepSize;
	private int dataSize;
	
	public ExtractPrismData(String datapath, int datasize, int stepSize) {
		this.dataPath = datapath;
		this.dataSize = datasize;
		this.stepSize = stepSize;
	}

	@Override
	public VariablesValueInfo getVariablesValueInfo() throws IOException {
		List<String> vars = PrismPathData.extractPathVars(dataPath, Config.DELIMTER);
		List<List<VariablesValue>> vvs = PrismPathData.extractMEData(dataPath, vars, dataSize, stepSize);
		List<Integer> varsLength = PrismPathData.updateMEVarsLength(vvs);
		return new VariablesValueInfo(vars, varsLength, vvs);
	}
	
	
	// extract specific variables information
	public VariablesValueInfo getVariablesValueInfo(List<String> vars) throws IOException {
		List<List<VariablesValue>> vvs = PrismPathData.extractMEData(dataPath, vars, dataSize, stepSize);
		List<Integer> varsLength = PrismPathData.updateMEVarsLength(vvs);
		return new VariablesValueInfo(vars, varsLength, vvs);
	}

}
