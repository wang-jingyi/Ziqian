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
	
	public static List<Double> arrayToList(double[] arr){
		List<Double> ls = new ArrayList<Double>();
		for(int i=0; i<arr.length; i++){
			ls.add(arr[i]);
		}
		return ls;
	}
	
	public static List<Double> listABSDiff(List<Double> list1, List<Double> list2){
		int len = list1.size();
		List<Double> res = new ArrayList<Double>();
		for(int i=0; i<len; i++){
			res.add(Math.abs(list1.get(i)-list2.get(i)));
		}
		return res;
	}
	
	public static double listMeanNonZero(List<Double> list){ // only consider non-zero values
		int len = list.size();
		double sum = 0.0;
		for(int i=0; i<len; i++){
			if(list.get(i)==0){
				len--;
			}
			sum += list.get(i);
		}
		if(len==0){
			return 0;
		}
		return sum/len;
	}
	
	public static double listSum(List<Double> ls){
		double sum = 0;
		for(double d : ls){
			sum += d;
		}
		return sum;
	}
	
	public static double listMean(List<Double> list){ // only consider non-zero values
		int len = list.size();
		double sum = 0.0;
		for(int i=0; i<len; i++){
			sum += list.get(i);
		}
		return sum/len;
	}
	
	public static List<List<Double>> TwoDDoubleArrayToList(double[][] arr){
		List<List<Double>> ls = new ArrayList<List<Double>>();
		for(int i=0; i<arr.length; i++){
			List<Double> l = new ArrayList<Double>();
			for(int j=0; j<arr[0].length; j++){
				l.add(arr[i][j]);
			}
			ls.add(l);
		}
		return ls;
	}
	
	public static double[][] TwoDDoublelistToArray(List<List<Double>> ls){
		int rm = ls.size();
		int cm = ls.get(0).size();
		double[][] arr = new double[rm][cm];
		for(int i=0; i<ls.size(); i++){
			for(int j=0; j<ls.get(0).size(); j++){
				arr[i][j] = ls.get(i).get(j);
			}
		}
		return arr;
	}
	
	public static int[][] TwoDIntlistToArray(List<List<Integer>> ls){
		int rm = ls.size();
		int cm = ls.get(0).size();
		int[][] arr = new int[rm][cm];
		for(int i=0; i<ls.size(); i++){
			for(int j=0; j<ls.get(0).size(); j++){
				arr[i][j] = ls.get(i).get(j);
			}
		}
		return arr;
	}
	
	public static List<List<Integer>> TwoDArrayToList(int[][] arr){
		List<List<Integer>> ls = new ArrayList<List<Integer>>();
		for(int i=0; i<arr.length; i++){
			List<Integer> l = new ArrayList<Integer>();
			for(int j=0; j<arr[0].length; j++){
				l.add(arr[i][j]);
			}
			ls.add(l);
		}
		return ls;
	}

}
