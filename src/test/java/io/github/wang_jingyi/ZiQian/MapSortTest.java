package io.github.wang_jingyi.ZiQian;

import io.github.wang_jingyi.ZiQian.utils.MapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

public class MapSortTest {
	
	@Test
	public void testSortingMap(){
		HashMap<Integer,Double> map = new HashMap<Integer,Double>();
		map.put(1,0.8);
		map.put(2,0.23);
		map.put(3,0.1);
		map.put(4,0.3);
		map.put(5,0.4);
		List<Integer> ints = new ArrayList<>();
		for(int i : MapUtil.sortByValue(map).keySet()){
			ints.add(i);
		}
		System.out.println(ints);
	}
}
