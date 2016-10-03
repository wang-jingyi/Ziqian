package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwatSensorAbstraction {
	
	private List<String> sensors; // sensor name
	private List<Interval> ranges; // sensor value range
	private List<Integer> denominators; // denominator of range
	private List<Map<Interval, Integer>> sensorEncodings;
	private List<Map<Integer, Interval>> sensorDecodings;
	
	public SwatSensorAbstraction() {
		this.sensors = new ArrayList<String>();
		this.sensorEncodings = new ArrayList<Map<Interval,Integer>>();
		this.sensorDecodings = new ArrayList<Map<Integer,Interval>>();
	}
	
	public void addSensor(String sensorName, Interval range, int divideNumber){
		sensors.add(sensorName);
		ranges.add(range);
		denominators.add(divideNumber);
		
		Map<Interval, Integer> sensorEncode = new HashMap<Interval, Integer>();
		Map<Integer, Interval> sensorDecode = new HashMap<Integer, Interval>();
		
		int start = range.start;
		int intvLength = (range.end-range.start)/divideNumber;
		for(int i=1; i<=divideNumber; i++){
			int end = start + intvLength;
			Interval currentIntv = new Interval(start, end);
			sensorEncode.put(currentIntv, i);
			sensorDecode.put(i, currentIntv);
			start = end;
		}
		sensorEncodings.add(sensorEncode);
		sensorDecodings.add(sensorDecode);
	}
	
	public SwatState swatAbstraction(double[] sensorValues){
		List<Integer> ss = new ArrayList<Integer>();
		for(int i=0; i<sensorValues.length; i++){
			ss.add((int) ((sensorValues[i]-ranges.get(i).start)/denominators.get(i)));
		}
		return new SwatState(ss);
	}

	public List<String> getSensors() {
		return sensors;
	}
	
	public List<Map<Interval, Integer>> getSensorEncodings() {
		return sensorEncodings;
	}

	public List<Map<Integer, Interval>> getSensorDecodings() {
		return sensorDecodings;
	}
	

}
