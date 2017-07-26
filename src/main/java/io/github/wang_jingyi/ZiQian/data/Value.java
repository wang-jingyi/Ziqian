package io.github.wang_jingyi.ZiQian.data;

import io.github.wang_jingyi.ZiQian.utils.NumberUtil;


/*
 *  value of an observed variable
 *  only support integer and boolean so far
 * */


public class Value {
	
	private String rawValue;
	
	public Value(String s){
		this.rawValue = s;
	}
	
	public String getRawValue() {
		return rawValue;
	}

	public String toString(){
		return rawValue;
	}
	
	public String getIntValue(){
		if(rawValue.equalsIgnoreCase("true")){
			return "1";
		}
		if(rawValue.equalsIgnoreCase("false")){
			return "0";
		}
		
		return String.valueOf((int)Double.parseDouble(rawValue));
	} 
	
	public int getRawIntValue(){
		if(rawValue.equalsIgnoreCase("true")){
			return 1;
		}
		if(rawValue.equalsIgnoreCase("false")){
			return 0;
		}
		
		return (int)Double.parseDouble(rawValue);
	}
	
	public double getRawDoubleValue(){
		if(rawValue.equalsIgnoreCase("true")){
			return 1;
		}
		if(rawValue.equalsIgnoreCase("false")){
			return 0;
		}
		
		if(NumberUtil.isDouble(rawValue)){
			return Double.parseDouble(rawValue);
		}
		
		return 0;
	}
}
