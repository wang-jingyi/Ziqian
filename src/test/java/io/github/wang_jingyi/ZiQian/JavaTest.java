package io.github.wang_jingyi.ZiQian;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

public class JavaTest {
	
	@Test 
	public void testListMap(){
		HashSet<List<String>> mp = new HashSet<>();
		List<String> a = new ArrayList<>();
		a.add("d");
		mp.add(a);
		System.out.println("test a:" + mp.contains(a));
	}
	
	@Test
	public void testListEquals(){
		List<Integer> list1 = new ArrayList<Integer>();
		list1.add(1);
		List<Integer> list2 = new ArrayList<Integer>();
		list2.add(1);
		System.out.println("test integer list: " + list1.equals(list2));
	}

	
	@Test
	public void testArray(){
		int[] a = new int[1];
		a[0] = 1;
		a[0] ++;
		System.out.println("a[0]: " + a[0]);
	}
	
	@Test
	public void testSplit(){
		String a = "a a";
		String[] aa = a.split(" ");
		System.out.println("aa length: " + aa.length);
	}
}
