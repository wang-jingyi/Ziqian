package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwatSensorAbstraction {
	
	private List<String> sensors; // sensor name
	private List<Interval> ranges; // sensor value range
	private List<Integer> denominators; // denominator of range
	private List<Integer> splitStateNumber;
	private List<Map<Interval, Integer>> sensorEncodings;
	private List<Map<Integer, Interval>> sensorDecodings;
	private int stateNumber;
	
	public SwatSensorAbstraction() {
		this.sensors = new ArrayList<String>();
		this.sensorEncodings = new ArrayList<Map<Interval,Integer>>();
		this.sensorDecodings = new ArrayList<Map<Integer,Interval>>();
		this.ranges = new ArrayList<Interval>();
		this.denominators = new ArrayList<Integer>();
		this.splitStateNumber = new ArrayList<Integer>();
		this.stateNumber = 1;
	}
	
	@Override
	public String toString() {
		return "SwatSensorAbstraction [sensors=" + sensors + ", ranges="
				+ ranges + ", denominators=" + denominators
				+ ", sensorEncodings=" + sensorEncodings + ", sensorDecodings="
				+ sensorDecodings + "]";
	}

	public void addSensor(String sensorName, Interval range, int denominator){
		sensors.add(sensorName);
		ranges.add(range);
		denominators.add(denominator);
		
		Map<Interval, Integer> sensorEncode = new HashMap<Interval, Integer>();
		Map<Integer, Interval> sensorDecode = new HashMap<Integer, Interval>();
		
		int start = range.start;
		int partitionNumber = (range.end-range.start)/denominator;
		for(int i=1; i<=partitionNumber; i++){
			int end = start + denominator;
			Interval currentIntv = new Interval(start, end);
			sensorEncode.put(currentIntv, i);
			sensorDecode.put(i, currentIntv);
			start = end;
		}
		
		Interval startIntv = new Interval(Integer.MIN_VALUE, range.start);
		sensorEncode.put(startIntv, 0);
		sensorDecode.put(0, startIntv);
		Interval endIntv = new Interval(range.end, Integer.MAX_VALUE);
		sensorEncode.put(endIntv, partitionNumber+1);
		sensorDecode.put(partitionNumber+1, endIntv);
		
		sensorEncodings.add(sensorEncode);
		sensorDecodings.add(sensorDecode);
		splitStateNumber.add(partitionNumber+2);
		stateNumber = stateNumber * (partitionNumber + 2);
	}
	
	public int getStateNumber() {
		return stateNumber;
	}
	
	public int swatStateIndex(List<Integer> abstractValue){ // index starts from 0
		int index = 1;
		for(int i=0; i<abstractValue.size(); i++){
			index = index * (abstractValue.get(i)+1);
		}
		return index - 1;
	}
	
	public List<Integer> swatStateAbstractValue(int stateIndex){
		int pd = stateIndex + 1;
		List<Integer> abstractValues = new ArrayList<Integer>();
		List<Integer> divs = new ArrayList<Integer>();
		int div = 1;
		for(int i=0; i<sensors.size()-1; i++){
			div = div * splitStateNumber.get(i);
			divs.add(div);
		}
		
		int divInd = divs.size()-1;
		for(int i=0; i<sensors.size(); i++){
			if(i==sensors.size()-1){ // if last one
				abstractValues.add(0, pd);
				continue;
			}
			int divNum = divs.get(divInd);
			abstractValues.add(0, pd%divNum);
			pd = pd/divNum;
			divInd--;
		}
		return abstractValues;
	}

	public SwatState swatAbstraction(double[] sensorValues){
		List<Integer> ss = new ArrayList<Integer>();
		for(int i=0; i<sensorValues.length; i++){
			if(sensorValues[i]<ranges.get(i).start){ // start interval
				ss.add(0);
				continue;
			}
			if(sensorValues[i]>ranges.get(i).end){ // end interval
				ss.add((int) ((sensorValues[i]-ranges.get(i).start)/denominators.get(i))+1);
				continue;
			}
			ss.add((int) ((sensorValues[i]-ranges.get(i).start)/denominators.get(i))+1);
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
