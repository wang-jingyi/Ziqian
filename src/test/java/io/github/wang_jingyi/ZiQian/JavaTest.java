package io.github.wang_jingyi.ZiQian;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class JavaTest {
	
	@Test
	public void testListEquals(){
		List<Integer> list1 = new ArrayList<Integer>();
		list1.add(1);
		List<Integer> list2 = new ArrayList<Integer>();
		list2.add(1);
		System.out.println("test: " + list1.equals(list2));
	}

}
