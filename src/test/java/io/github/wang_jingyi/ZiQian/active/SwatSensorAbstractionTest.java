package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SwatSensorAbstractionTest {
	
	@Test
	public void testSensorAbstraction(){
		SwatSensorAbstraction ssa = new SwatSensorAbstraction();
		ssa.addSensor("FIT501", new Interval(10, 100), 10);
		System.out.println(ssa.toString());
	}
	
	@Test
	public void testIndexToValue(){
		int index = 4;
		System.out.println(swatStateAbstractValue(index));
	}
	
	public List<Integer> swatStateAbstractValue(int stateIndex){ // to debug
		
		
		int pd = stateIndex;
		List<Integer> abstractValues = new ArrayList<Integer>();
		
		int sensorSize = 2;
		List<Integer> splitStateNumber = new ArrayList<Integer>();
		splitStateNumber.add(2);
		splitStateNumber.add(3);
		
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

}
