package io.github.wang_jingyi.ZiQian.profile;

public class Profiler {
	
	public static void printShortMessage(String msg){
		
		int msglength = msg.length();
		if(msglength>100){
			printLongMessage(msg);
		}
		else{
			makeLength(msg, 100);
		}
		
	}
	
	private static void makeLength(String msg, int i) {
		
		
	}

	public static void printLongMessage(String msg){
		
	}

}
