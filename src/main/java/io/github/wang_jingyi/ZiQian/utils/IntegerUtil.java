package io.github.wang_jingyi.ZiQian.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class IntegerUtil {
	
	public static List<Integer> removeLastElement(List<Integer> alist){
		int tmp = alist.size();
		alist.remove(tmp-1);
		return alist;
	}
	
	public static List<Integer> cloneList(List<Integer> orig){
		List<Integer> rlist = new ArrayList<Integer>();
		rlist.addAll(orig);
		return rlist;
	}
	
	public static int getLastElement(List<Integer> il){
		return il.get(il.size()-1);
	}

	public static String intArrayToString(int[] intArr){
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for(int i=0; i<intArr.length; i++){
			sb.append(intArr[i] + " ");
		}
		sb.append(" ]");
		return sb.toString();
	}

	public static int indexInList(int i, List<Integer> alist){
		for(int j=0; j<alist.size(); j++){
			if(alist.get(j)==i){
				return j;
			}
		}
		return -1;
	}

	public static boolean isInList(int i, List<Integer> alist){
		for(int j : alist){
			if(j==i){
				return true;
			}
		}
		return false;
	}

	public static List<List<Integer>> cloneListOfList(List<List<Integer>> aalist){
		List<List<Integer>> all = new ArrayList<List<Integer>>();
		for(List<Integer> al : aalist){
			all.add(IntegerUtil.cloneList(al));
		}
		return all;
	}

	// string of 0,1
	public static List<String> stringOf01(int n){
		List<String> alphabet = new ArrayList<>();
		int maxVl = (int) (Math.pow(2, n) - 1);
		for(int i=0; i<=maxVl; i++){
			String str = Integer.toBinaryString(i);
			if(str.length()!=n){
				str = makeLengthN(str, n);
			}
			alphabet.add(str);
		}
		return alphabet;
	}

	public static String makeLengthN(String str, int n) {
		int dif = n - str.length();
		String newStr = "";
		for(int j=0; j<dif; j++){
			newStr = newStr + "0";
		}
		return newStr + str;
	}
	
	public static double BinCumulativeProb(int n, double p, int c){
		double culProb = 0;
		for(int i=0; i<c; i++){
			culProb += CombinatoricsUtils.binomialCoefficient(n, i) * Math.pow(p, i) * Math.pow((1-p), n-i);
		}
		return culProb;
	}
	
	public static boolean equalList(List<Integer> l1, List<Integer> l2){
		if(l1.size()!=l2.size()){
			return false;
		}
		for(int i=0; i<l1.size(); i++){
			if(l1.get(i)!=l2.get(i)){
				return false;
			}
		}
		return true;
	}
	
}
