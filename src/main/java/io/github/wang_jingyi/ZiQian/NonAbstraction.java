package io.github.wang_jingyi.ZiQian;

import io.github.wang_jingyi.ZiQian.data.PrismPathData;
import io.github.wang_jingyi.ZiQian.data.Value;
import io.github.wang_jingyi.ZiQian.data.VariableInfoExtraction;
import io.github.wang_jingyi.ZiQian.data.VariablesValue;
import io.github.wang_jingyi.ZiQian.data.VariablesValueInfo;
import io.github.wang_jingyi.ZiQian.main.AlgoProfile;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class NonAbstraction implements VariableInfoExtraction{
	
	
	List<String> vars = new ArrayList<>();
	

	public NonAbstraction(List<String> vars) {
		this.vars = vars;
	}

	@Override
	public Input extractVariableInfo(VariablesValueInfo vvi) {
		
		List<List<String>> obss = new ArrayList<>();
		
		for(List<VariablesValue> vvl : vvi.getVarsValues()){
			List<String> obs = extractPathVarsValues(vvl, vars, AlgoProfile.varsLength);
			obss.add(obs);
		}
		
		List<String> alpha = new ArrayList<>();
		for(List<String> obs : obss){
			for(String s : obs){
				if(StringUtil.getStringIndex(s, alpha)==-1){
					alpha.add(s);
				}
			}
		}
		return new Input(alpha, obss);
	}
	
	public static String extractVarsValues(VariablesValue vv, List<String> vars, List<Integer> varsLength){
		String result = "";
		for(int i=0; i<vars.size(); i++){
			String var = vars.get(i);
			Value v = vv.getVarValue(var);
			String varVaule = v.getIntValue();
			if(varVaule.length()<varsLength.get(i)){
				varVaule = PrismPathData.extendString(varVaule, varsLength.get(i));
			}
			result = result + varVaule;
			
		}
		return result;
	}
	
	public static List<String> extractPathVarsValues(List<VariablesValue> vvs, List<String> vars, List<Integer> varsLength){
		List<String> result = new ArrayList<>();
		for(VariablesValue vv : vvs){
			result.add(extractVarsValues(vv, vars, varsLength));
		}
		return result;
	}

	@Override
	public List<Integer> updateVarsLength(VariablesValueInfo vvi) {
		List<Integer> varsLength = new ArrayList<>();
		List<Integer> inds = new ArrayList<>();
		for(String str : vars){
			int ind = StringUtil.getStringIndex(str, vvi.getVars());
			assert ind!= -1;
			inds.add(ind);
		}
		for(int ind : inds){
			varsLength.add(vvi.getVarsLength().get(ind));
		}
		return varsLength;
	}
}
