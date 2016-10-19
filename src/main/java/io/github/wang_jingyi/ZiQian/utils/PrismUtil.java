package io.github.wang_jingyi.ZiQian.utils;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrismUtil {


	// translate generated random markov chain to prism model format for model checking
	public static void MCToPrism(double[][] tm, String fileName, String filePath) throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
		sb.append("dtmc" + " \n \n");
		int numOfState = tm.length;

		// module
		sb.append("module rmc" + "\n");
		sb.append("s:[1.." + numOfState + "] init 1" + "; \n"); //

		// state transitions
		for(int i=0; i<numOfState; i++){
			sb.append("[]s=" + (i+1) + " -> ");
			boolean first = true;
			for(int j=0; j<numOfState; j++){
				if(tm[i][j]!=0.0 && first){
					sb.append(tm[i][j]+ " :(s'=" + (j+1) + ")");
					first = false;
					continue;
				}
				if(tm[i][j]!=0.0 && !first){
					sb.append(" + " + tm[i][j] + " :(s'=" + (j+1) + ")");
					continue;
				}
			}
			sb.append(";\n");
		}
		sb.append("endmodule \n\n");
		FileUtil.writeStringToFile(filePath+"/"+fileName+".pm", sb.toString());
	}

	public static void MCToPrism(double[][] tm, List<Integer> initialStates, String fileName, String dirPath) throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
		sb.append("dtmc" + " \n \n");
		int numOfState = tm.length;

		// module
		sb.append("module rmc" + "\n");
		sb.append("s:[0.." + numOfState + "] init 0; \n"); //

		sb.append("[]s=0 -> ");
		double up = (double)1/initialStates.size();
		boolean flag = true;
		for(int is : initialStates){
			if(flag){
				sb.append( up +":(s'=" + (is+1) + ")");
				flag = false;
				continue;
			}
			sb.append(" + " + up + " :(s'=" + (is+1) + ")");
		}
		sb.append(";\n");

		// state transitions
		for(int i=0; i<numOfState; i++){
			sb.append("[]s=" + (i+1) + " -> ");
			boolean first = true;
			boolean noNext = true;
			for(int j=0; j<numOfState; j++){
				if(tm[i][j]!=0.0 && first){
					sb.append(tm[i][j]+ " :(s'=" + (j+1) + ")");
					first = false;
					noNext = false;
					continue;
				}
				if(tm[i][j]!=0.0 && !first){
					noNext = false;
					sb.append(" + " + tm[i][j] + " :(s'=" + (j+1) + ")");
				}
			}
			if(noNext){
				sb.append(1.0 + " :(s'=" + (i+1) + ")");
			}
			sb.append(";\n");
		}
		sb.append("endmodule \n\n");
		FileUtil.writeStringToFile(dirPath+"/"+fileName+".pm", sb.toString());
	}


	// write property list to check
	public static void WriteRMCPropertyList(String dirPath, String fileName, int stateNumber, int boundedStep) throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
		for(int i=1; i<=stateNumber; i++){
			sb.append("P=? [ true U<="+ boundedStep +" s=" + i + " ];\n");
		}
		FileUtil.writeStringToFile(dirPath+"/"+fileName+".pctl", sb.toString());
	}

	public static void writeLearnPropertyList(String dirPath, String fileName, int stateNumber, int boundedStep) throws FileNotFoundException{
		StringBuilder sb = new StringBuilder();
		for(int i=1; i<=stateNumber; i++){
			sb.append("P=? [ true U<=" + boundedStep + " \"rmc" + i + "\" ]\n");
		}
		FileUtil.writeStringToFile(dirPath+"/"+fileName+"_learn.pctl", sb.toString());
	}

	public static double extractResultFromCommandOutput(String output){
		try{
			Pattern pattern = Pattern.compile("(Result: \\d.\\d+)");
			Matcher matcher = pattern.matcher(output);

			Pattern pattern1 = Pattern.compile("(Result: \\d.\\d+E-\\d+)");
			Matcher matcher1 = pattern1.matcher(output);

			if(matcher1.find()){
				String greped = matcher1.group(0);
				String[] strs = greped.split(" ");
				double result = Double.valueOf(strs[1]);
				return result;
			}

			if (matcher.find())
			{
				System.out.println(matcher.group(0));
				String greped = matcher.group(0);
				String[] strs = greped.split(" ");
				double result = Double.valueOf(strs[1]);
				return result;
			}
		}catch(Exception e){e.printStackTrace();}

		return 0.0;
	}

	public static void main(String[] args){
		String a ="Result: 2.6E-8";
		System.out.println(extractResultFromCommandOutput(a));
	}
}
