package io.github.wang_jingyi.ZiQian.active;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SwatSensorAbstraction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5123348284829960146L;
	/**
	 * 
	 */
	private List<String> sensors; // sensor name
	private List<Interval> ranges; // sensor value range
	private List<Integer> denominators; // denominator of range
	private List<Integer> initialStates;
	private List<Double> initDist;
	private List<Integer> targetStates;

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
		this.initialStates = new ArrayList<Integer>();
		this.initDist = new ArrayList<Double>();
		this.targetStates = new ArrayList<Integer>();
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
		if((range.end-range.start)%denominator!=0){
			partitionNumber ++;
		}
		for(int i=1; i<=partitionNumber; i++){
			int end = start + denominator;
			if(end>range.end){
				end = range.end;
			}
			Interval currentIntv = new Interval(start, end);
			sensorEncode.put(currentIntv, i);
			sensorDecode.put(i, currentIntv);
			start = end;
		}

		Interval startIntv = new Interval(0, range.start);
		sensorEncode.put(startIntv, 0);
		sensorDecode.put(0, startIntv);
		Interval endIntv = new Interval(range.end, 10000);
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

	public void computeInitialStates(){
		for(int i=0; i<stateNumber; i++){
			List<Integer> li = swatStateAbstractValue(i);
			boolean flag = false;
			for(int j=0; j<li.size(); j++){
				if(li.get(j)==0 || li.get(j)==splitStateNumber.get(j)-1){ // underflow or overflow
					flag = true;
				}
			}
			if(!flag){
				initialStates.add(i);
			}
		}
		
		double p = (double)1/initialStates.size();
		for(int i=0; i<initialStates.size(); i++){
			initDist.add(p);
		}
		
		System.out.println("initial states: " + initialStates);
		System.out.println("initial states number: " + initialStates.size());
	}

	public List<Double> getInitDist() {
		return initDist;
	}

	public void setInitDist(List<Double> initDist) {
		this.initDist = initDist;
	}

	public void computeTargetStates(){
		for(int i=0; i<stateNumber; i++){
			List<Integer> li = swatStateAbstractValue(i);
			for(int j=0; j<li.size(); j++){
				if(li.get(j)==0 || li.get(j)==splitStateNumber.get(j)-1){ // underflow or overflow
					targetStates.add(i);
					break;
				}
			}
		}

		System.out.println("target states: " + targetStates);
		System.out.println("targest states size: " + targetStates.size());
	}

	public List<Integer> getInitialStates() {
		return initialStates;
	}

	public List<Integer> getTargetStates() {
		return targetStates;
	}

	public int swatStateIndex(List<Integer> abstractValue){ // index starts from 0
		int index = 0;
		int po = sensors.size()-1;
		for(int i=0; i<abstractValue.size()-1; i++){
			index = (int) (index + abstractValue.get(i) * Math.pow(splitStateNumber.get(i+1), po));
			po--;
		}
		return index + abstractValue.get(sensors.size()-1);
	}

	public List<Integer> swatStateAbstractValue(int stateIndex){ // to debug

		int pd = stateIndex;
		List<Integer> abstractValues = new ArrayList<Integer>();

		int sensorSize = sensors.size();
		int divIndex = sensorSize-1; // start from the last digit

		for(int i=0; i<sensorSize; i++){
			if(i==sensorSize-1){ // if last one
				abstractValues.add(0, pd);
				continue;
			}
			int div = splitStateNumber.get(divIndex);
			abstractValues.add(0, pd%div);
			divIndex--;
			pd = pd/div;
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
				ss.add(splitStateNumber.get(i)-1);
				continue;
			}
			ss.add((int) ((sensorValues[i]-ranges.get(i).start)/denominators.get(i))+1);
		}
		return new SwatState(ss);
	}

	// give state index, generate a set of sensor values as input
	public double[] generateInput(int stateIndex){

		int sensorNum = sensors.size();
		double[] input = new double[sensorNum];

		SwatState ss = new SwatState(swatStateAbstractValue(stateIndex));
		for(int i=0; i<sensorNum; i++){
			int sensorIndex = ss.getSensorValues().get(i);
			Random rnd = new Random();
			Interval intv = sensorDecodings.get(i).get(sensorIndex);
			if(intv==null){
				System.out.println("tag");
			}
			double sv = intv.start + rnd.nextDouble() * (intv.end-intv.start);
			input[i] = sv;
		}
		return input;
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
