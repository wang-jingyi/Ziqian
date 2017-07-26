package io.github.wang_jingyi.ZiQian.example;

import io.github.wang_jingyi.ZiQian.data.VariablesValue;
import io.github.wang_jingyi.ZiQian.main.Config;

import java.util.ArrayList;
import java.util.List;


public class EglUnfair {

	public static int L = Config.ELG_L;
	public static int N = Config.EGL_N;
	public static int al = 2 * N; 
	
	protected List<String> vars = new ArrayList<>();
	
	public EglUnfair() {
		vars = addVars();
	}
	
	public static List<String> addVars(){
		List<String> interestedVars = new ArrayList<>();
		// add a0 to a2n
		for(int i=0; i<2*N; i++){
			interestedVars.add("a"+Integer.toString(i));
		}
		
		// add b0 to b2n
		for(int i=0; i<2*N; i++){
			interestedVars.add("b"+Integer.toString(i));
		}
		return interestedVars;
	}
	
	protected static boolean checkA(VariablesValue vv, List<String> vars){
		return check(vv,vars,0);
	}
	
	protected static boolean checkB(VariablesValue vv, List<String> vars){
		return check(vv,vars,2*N);
	}
	
	private static boolean check(VariablesValue vv, List<String> vars, int startIndex){
		for(int i=0; i<EglUnfair.N; i++){
			int ab_i = vv.getVarValue(vars.get(startIndex+i)).getRawIntValue();
			int ab_in = vv.getVarValue(vars.get(startIndex+i+N)).getRawIntValue();
			if(ab_i==L && ab_in==L){
				return true;
			}
		}
		return false;
	}
}
