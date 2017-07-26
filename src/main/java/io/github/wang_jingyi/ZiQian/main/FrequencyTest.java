package io.github.wang_jingyi.ZiQian.main;

import java.io.IOException;
import java.util.List;

import io.github.wang_jingyi.ZiQian.data.ExtractPrismData;
import io.github.wang_jingyi.ZiQian.data.VariablesValue;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;

public class FrequencyTest { 
	
	// calculate frequency of underflow for swat system
	public static void main(String[] args) throws IOException{
		
		ExtractPrismData epd = new ExtractPrismData(Config.DATA_PATH, Config.DATA_SIZE, Config.STEP_SIZE, Config.DELIMITER);
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
		System.out.println("count of swat error: " + count);
		System.out.println("probability of swat error: " + (double)count/vvl.getVarsValues().size());
		
	}
	
}
