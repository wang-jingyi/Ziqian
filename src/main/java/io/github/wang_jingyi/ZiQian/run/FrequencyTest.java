package io.github.wang_jingyi.ZiQian.run;

import java.io.IOException;
import java.util.List;

import io.github.wang_jingyi.ZiQian.VariablesValue;
import io.github.wang_jingyi.ZiQian.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.prism.ExtractPrismData;

public class FrequencyTest { 
	
	// calculate frequency of underflow for swat system
	public static void main(String[] args) throws IOException{
		ExtractPrismData epd = new ExtractPrismData(Config.DATA_PATH, Config.DATA_SIZE, 1, Config.DELIMTER);
		VariablesValueInfo vvl = epd.getVariablesValueInfo();
		
		int count = 0;
		
		for(List<VariablesValue> vv : vvl.getVarsValues()){
			for(VariablesValue v : vv){
				if(v.getVarValue("LIT101").getRawIntValue()<250){
					count ++;
					break;
				}
			}
		}
		System.out.println("total number of traces: " + vvl.getVarsValues().size());
		System.out.println("count of underflow: " + count);
		System.out.println("probability of underflow: " + (double)count/vvl.getVarsValues().size());
		
	}
	
}
