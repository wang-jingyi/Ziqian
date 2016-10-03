package io.github.wang_jingyi.ZiQian.utils;

import java.io.FileNotFoundException;
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


	public static double extractResultFromCommandOutput(String output){
//		int length = output.length();
//		output = output.substring(length*9/10, length);
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
		
		else{
			System.out.println("no result found from terminal output");
			System.exit(0);
		}
		return 0.0;
	}

	public static void main(String[] args){
		String a ="Result: 2.6E-8";
		System.out.println(extractResultFromCommandOutput(a));
	}
}
