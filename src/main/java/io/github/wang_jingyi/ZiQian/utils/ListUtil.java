package io.github.wang_jingyi.ZiQian.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
	
	public static double[] listToArray(List<Double> lis){
		double[] arr = new double[lis.size()];
		for(int i=0; i<lis.size();i++){
			arr[i] = lis.get(i);
		}
		return arr;
	}
	
	public static List<Double> listABSDiff(List<Double> list1, List<Double> list2){
		int len = list1.size();
		List<Double> res = new ArrayList<Double>();
		for(int i=0; i<len; i++){
			res.add(Math.abs(list1.get(i)-list2.get(i)));
		}
		return res;
	}
	
	public static double listMean(List<Double> list){
		int len = list.size();
		double sum = 0.0;
		for(int i=0; i<len; i++){
			sum += list.get(i);
		}
		return sum/len;
	}

}
