package io.github.wang_jingyi.ZiQian.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
	
	public static String corresVarString(String var, String letter, List<String> vars, List<Integer> varLength){
		int start = 0;
		for(int i=0; i<vars.size(); i++){
			if(vars.get(i).equals(var)){
				return letter.substring(start, start+varLength.get(i));
			}
			start += varLength.get(i);
		}
		return null;
	}
	
	public static String stringListToString(List<String> sl){
		String s = "";
		for(int i=0; i<sl.size(); i++){
			s += sl.get(i);
		}
		return s;
	}
	
	public static int getStringListIndex(List<String> sl, List<List<String>> sll){
		for(int i=0; i<sll.size(); i++){
			if(StringUtil.equals(sl, sll.get(i))){
				return i;
			}
		}
		return -1;
	}
	
	public static List<String> cloneList(List<String> origList){
		List<String> copyList = new ArrayList<String>();
		for(String t : origList){
			copyList.add(t);
		}
		return copyList;
	}

	// calculate the occurrence probability of a string in a list of strings events
	public static double calOccProb(List<String> subEvent, List<String> events){
		return (double)countSubEvents(subEvent, events)/(events.size()- subEvent.size()+1);
	}
	
	
	public static boolean isEqual(List<String> lista, List<String> listb){
		if(lista.size()!=listb.size()){
			return false;
		}
		for(int i=0; i<lista.size(); i++){
			if(!lista.get(i).equals(listb.get(i))){
				return false;
			}
		}
		return true;
	}

	public static int countSubEvents(List<String> subEvents, List<String> events){ // count occurrence of sub-events in event sequence
		int count = 0;
		int eventsSize = events.size();
		int subEventsSize = subEvents.size();

		for(int i=subEventsSize-1; i<eventsSize; i++){
			boolean isEqual = true;
			int jj = 0;
			for(int j=i-subEventsSize+1; j<=i; j++){
				if(!events.get(j).equals(subEvents.get(jj))){
					isEqual = false;
					break;
				}
				jj++;
			}
			if(isEqual==true){
				count ++;
			}
		}
		return count;
	}

	// given string s, and sigma, calculate the transition probability from s to s-sigma
	// special case: s is right at the end of the event
	// the count of s should be subtracted by 1 to make sure the sum of the transition probability equals 1 
	public static double calNextSymbolTransProb(List<String> currentCand, String sigma, List<String> events) {
		List<String> copiedCand = cloneList(currentCand);
		int currentOcc = 0;
		if(copiedCand.size()==0){ // if start from empty
			currentOcc = events.size();
		}
		else{
			currentOcc = countSubEvents(copiedCand, events);
			assert currentOcc > 0 : "No such sub-event in the observation";
			// if the end of the event contains current candidate, then we should subtract its occurrence by 1
			int csize = copiedCand.size();
			int j = 0;
			boolean isEnd = true;
			for(int i=events.size()-csize; i<events.size(); i++){ // check if it is in the end
				if(!events.get(i).equals(copiedCand.get(j))){
					isEnd = false;
					break;
				}
				j++;
			}
			if(isEnd==true){
				currentOcc--;
			}
		}

		// count string-sigma occurrence
		copiedCand.add(sigma);
		int occ = countSubEvents(copiedCand, events);
		return (double)occ/currentOcc;
	}

	public static List<String> getLongestPrefix(List<String> strList) {
		List<String> prefixList = new ArrayList<String>();
		if(strList.size()>1){
			for(int i=0; i<strList.size()-1; i++){
				prefixList.add(strList.get(i));
			}
		}
		return prefixList;
	}
	
	public static boolean equals(List<String> toCompare, List<String> compared){
		if(toCompare.size()!=compared.size()){
			return false;
		}
		else{
			for(int i=0; i<toCompare.size(); i++){
				if(!toCompare.get(i).equals(compared.get(i))){
					return false;
				}
			}
			return true;
		}
	}

	public static boolean isSuffix(List<String> suffix, List<String> slist) {
		int len = slist.size();
		for(int j=len-1; j>=0; j--){
			if(equals(suffix, slist.subList(j, len))){ // sublist method include j, exclude len
				return true;
			}
		}
		return false;
	}
	
	// get all suffixes of a list, empty list exclusive, itself inclusive
	public static List<List<String>> getSuffixes(List<String> sl){
		List<List<String>> allSuffixes = new ArrayList<List<String>>();
		int len = sl.size();
		for(int i=0; i<len; i++){
			allSuffixes.add(sl.subList(i, len));
		}
		return allSuffixes;
	}
	
	public static int getStringIndex(String s, List<String> slist){
		for(int i=0; i<slist.size(); i++){
			if(s.equals(slist.get(i))){
				return i;
			}
		}
		return -1;
	}
	
	public static String getLastString(List<String> slist){
		assert slist.size()!=0 : "list is empty";
		int ss = slist.size();
		return slist.get(ss-1);
	}
	
	public static String arrayToString(String[] strs){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i=0; i<strs.length; i++){
			sb.append(strs[i] + ",");
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static String stringArrayToString(String[] arr){
		String s = "";
		for(String str : arr){
			s += " ";
			s += str;
		}
		return s;
	}
	
}
