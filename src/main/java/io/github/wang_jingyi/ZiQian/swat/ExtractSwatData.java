package io.github.wang_jingyi.ZiQian.swat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.FormatVariablesValue;
import io.github.wang_jingyi.ZiQian.Value;
import io.github.wang_jingyi.ZiQian.VariablesValue;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.prism.PrismPathData;

public class ExtractSwatData implements FormatVariablesValue{
	
	private String dataPath;
	private int dataLength = Integer.MAX_VALUE;
	
	public ExtractSwatData(String path) {
		this.dataPath = path;
	}
	
	public ExtractSwatData(String path, int dl) {
		this.dataPath = path;
		this.dataLength = dl;
	}
	
	
	public static void main(String[] args) throws IOException{
		ExtractSwatData sd = new ExtractSwatData("/Users/Dongxia/Documents/jingyi/csv_data/part2_r.csv", 1000);
		VariablesValueInfo vvi = sd.getVariablesValueInfo();
		System.out.println("varslength: " + vvi.getVarsLength());
		
	}

	@Override
	public VariablesValueInfo getVariablesValueInfo() throws IOException {
		List<String> vars = new ArrayList<>();
		List<List<VariablesValue>> mvalues = new ArrayList<>();
		List<VariablesValue> values = new ArrayList<VariablesValue>();
		List<Integer> varsLength = new ArrayList<>();
		
		FileReader reader = new FileReader(dataPath);
		BufferedReader br = new BufferedReader(reader);
		String str = br.readLine(); // first line of file
		String[] firstLine = str.split(",");
		for(int i=0; i<firstLine.length; i++){
			vars.add(firstLine[i]);
		}
		
		int count =0;
		while((str=br.readLine())!=null && count<=dataLength){
			String[] strs = str.split(",");
			VariablesValue rivv = new VariablesValue(vars);
			for(int i=0; i<vars.size(); i++){
				rivv.getValues().add(new Value(strs[i]));
			}
			values.add(rivv);
		}
		br.close();
		mvalues.add(values);
		
		varsLength = PrismPathData.updateSEVarsLength(values);
		
		return new VariablesValueInfo(vars, varsLength, mvalues);
	}
	
}
