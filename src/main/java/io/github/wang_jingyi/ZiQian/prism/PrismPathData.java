package io.github.wang_jingyi.ZiQian.prism;

import io.github.wang_jingyi.ZiQian.Value;
import io.github.wang_jingyi.ZiQian.VariablesValue;
import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrismPathData {


	/*
	 * @ para: dataPath is a single file path
	 * @ para: variables to extract
	 * */
	public static List<VariablesValue>  extractSEData(String dataPath, List<String> vars, int ds) throws IOException{
		List<VariablesValue> values = new ArrayList<VariablesValue>();
		FileReader reader = new FileReader(dataPath);
		BufferedReader br = new BufferedReader(reader);
		String str = br.readLine(); // first line of file
		String[] firstLine = str.split(" ");

		int[] varInd = new int[vars.size()]; // store the index of each variable
		for(int j=0; j<vars.size(); j++){
			for(int i=0; i<firstLine.length; i++){
				if(firstLine[i].equals(vars.get(j))){
					varInd[j] = i;
				}
			}
		}

		int cds = 0;
		while((str = br.readLine()) != null){ // read each line of data from file
			String[] strs = str.split(" ");
			VariablesValue rivv = new VariablesValue(vars);
			for(int i=0; i<varInd.length; i++){
				if(strs.length < vars.size()){ // make sure each variable has a value
					break;
				}
				rivv.getValues().add(new Value(strs[varInd[i]]));
			}
			values.add(rivv);
			cds ++;
			if(cds>ds){
				break;
			}
		}
		br.close();
		return values;
	}

	public static List<List<VariablesValue>> extractMEData(String dirPath, List<String> vars, int dataSize) throws IOException{
		List<List<VariablesValue>> mv = new ArrayList<List<VariablesValue>>();
		int totalSize = 0;
		List<String> fus = FileUtil.filesInDir(dirPath);
		for(String s : fus){
			
			int ds = Integer.MAX_VALUE;
			
			if(!Config.TERMINATE_SAMPLE){
				ds = new Random().nextInt(2*dataSize/fus.size());
			}
			
			List<VariablesValue> v = extractSEData(s, vars, ds);
			mv.add(v);
			totalSize = totalSize + v.size();
			if(totalSize>=dataSize){
				break;
			}
		}
		return mv;
	}

	public static String variablesValueString(VariablesValue vv, List<String> vars, List<Integer> varsLength){
		String r = "";
		for(int i=0; i<vars.size(); i++){
			String s = extendString(vv.getVarValue(vars.get(i)).getIntValue(), varsLength.get(i));
			r += s;
		}
		return r;
	}
	
	public static String variablesValueString(VariablesValue vv, List<Integer> varsLength){
		String r = "";
		for(int i=0; i<vv.getValues().size(); i++){
			String s = extendString(vv.getValues().get(i).getIntValue(), varsLength.get(i));
			r += s;
		}
		return r;
	}

	public static List<String> BuildSEAlphabet(List<VariablesValue> data, List<Integer> varsLength){
		List<String> alpha = new ArrayList<String>();
		for(VariablesValue vv : data){
			String s = variablesValueString(vv, varsLength);
			if(StringUtil.getStringIndex(s, alpha)==-1){
				alpha.add(s);
			}
		}
		return alpha;
	}


	public static List<String> BuildMEAlphabet(List<List<VariablesValue>> data, List<Integer> varsLength)
			throws IOException {
		List<String> alpha = new ArrayList<String>();
		for(List<VariablesValue> lv : data){
			for(VariablesValue vv : lv){
				String s = variablesValueString(vv, varsLength);
				if(StringUtil.getStringIndex(s, alpha)==-1){
					alpha.add(s);
				}
			}
		}
		return alpha;
	}


	public static List<Integer> updateVarsLength(VariablesValue vv){
		List<Integer> varsLength = new ArrayList<>();
		for(int i=0; i<vv.getValues().size(); i++){
			varsLength.add(0);
		}
		for(int i=0; i<vv.getValues().size(); i++){
			String varIntValue = vv.getValues().get(i).getIntValue();
			if(varIntValue.length()>varsLength.get(i)){
				varsLength.set(i, varIntValue.length());
			}
		}
		return varsLength;
	}

	public static List<Integer> updateSEVarsLength(List<VariablesValue> sv){
		List<Integer> varsLength = new ArrayList<>();
		for(VariablesValue vv : sv){
			varsLength = updateVarsLength(vv);
		}
		return varsLength;
	}

	public static List<Integer> updateMEVarsLength(List<List<VariablesValue>> mv){
		List<Integer> varsLength = new ArrayList<>();
		for(List<VariablesValue> sv : mv){
			varsLength = updateSEVarsLength(sv);
		}
		return varsLength;
	}

	public static String extendString(String s, int newLen){
		assert newLen>=s.length() : "no need to extend string";
		if(newLen==s.length()){
			return s;
		}
		String as = "";
		for(int i=0; i<newLen-s.length(); i++){
			as += "0";
		}
		as += s;
		return as;

	}

	public static List<String> BuildSEExecution(List<VariablesValue> data, List<Integer> varsLength){
		List<String> execution = new ArrayList<String>();
		for(VariablesValue vv : data){
			String s = variablesValueString(vv, varsLength);
			execution.add(s);
		}
		return execution;
	}

	public static List<List<String>> BuildMEExecutions(
			List<List<VariablesValue>> data, List<Integer> varsLength, int dataSize) {
		List<List<String>> executions = new ArrayList<List<String>>();
		int f = 0;
		for(List<VariablesValue> lv : data){
			List<String> execution = new ArrayList<String>();
			for(VariablesValue vv : lv){
				String s = variablesValueString(vv, varsLength);
				execution.add(s);
				f++;
				if(f>dataSize){
					break;
				}
			}
			executions.add(execution);
			if(f>dataSize){
				break;
			}
		}
		return executions;
	}
	
	public static List<String> extractPathVars(String dirPath) throws IOException{
		List<String> vars = new ArrayList<>();
		for(String s : FileUtil.filesInDir(dirPath)){
			FileReader reader = new FileReader(s);
			BufferedReader br = new BufferedReader(reader);
			String str = br.readLine(); // first line of file
			String str2 = br.readLine(); // second line of file
			String[] firstLine = str.split(" "); // take care of the delimiter here
			String[] secondLine = str2.split(" ");
			for(int i=2; i<firstLine.length; i++){ // extract variable 'action' and 'step' as they're irrelevant
				if(Character.isDigit(secondLine[i].charAt(0)) || secondLine[i].equalsIgnoreCase("true") || 
						secondLine[i].equalsIgnoreCase("false")){ // only add variables who are numbers, true or false
					vars.add(firstLine[i]);
				}
			}
			br.close();
			break;
		}
		return vars;
	}
	
	public static void main(String[] args) throws IOException{
		String dirPath = "/Users/jingyi/ziqian/model/swat/5,1,30/paths";
		for(String s : FileUtil.filesInDir(dirPath)){
			StringBuilder sb = new StringBuilder();
			
			FileReader reader = new FileReader(s);
			BufferedReader br = new BufferedReader(reader);
			String str = br.readLine(); // first line of file
			System.out.println(str);
			String[] firstLine = str.split("\\s+"); // take care of the delimiter here
			sb.append(getString(firstLine) + "\n");
			
			String line = null;
			while((line=br.readLine())!=null){
				String[] ld = line.split("\\s+");
				sb.append(getString(ld) + "\n");
			}
			br.close();
			FileUtil.writeStringToFile(s, sb.toString());
//			break;
		}
	}
	
	public static String getString(String[] strs){
		String s = "";
		for(int i=1; i<strs.length; i++){
			if(i==strs.length-1){
				s += strs[i];
				break;
			}
			s += strs[i] + " ";
		}
		return s;
	}
	
}
