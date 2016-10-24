package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SwatStateTest {
	
	@Test
	public void testSwatState(){
		List<Integer> l1 = new ArrayList<Integer>();
		l1.add(1);
		List<Integer> l2 = new ArrayList<Integer>();
		l2.add(1);
		SwatState ss1 = new SwatState(l1);
		SwatState ss2 = new SwatState(l2);
		
		Map<SwatState, Integer> ssm = new HashMap<SwatState, Integer>();
		ssm.put(ss1, 1);
		System.out.println(ssm.containsKey(ss2));
	}
	
	@Test
	public void testTargetState(){
		outBinary(3);
	}
	
	
	public void outBinary(int value){
		   for (int i = 0; i < Math.pow(2, value); i++) {
		       System.out.println(String.format("%4s", Integer.toBinaryString(i)).replace(' ', '0'));
		   }
		}
	
}
