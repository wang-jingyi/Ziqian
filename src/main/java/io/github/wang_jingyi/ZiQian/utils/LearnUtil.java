package io.github.wang_jingyi.ZiQian.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LearnUtil {
	
	
	/**
	 * @param vars_path the path to store the variables to learn from, the variables are delimtered by ,
	 * @return the set of variables to learn from
	 * @throws IOException
	 */
	public static List<String> extractVarsFromFile(String vars_path) throws IOException{
		
		String line = null;
		BufferedReader stream = null;
		try {
			stream = new BufferedReader(new FileReader(vars_path));
			while ((line = stream.readLine()) != null) {
				String[] lines = line.split(","); // reserve the part with threshold
				List<String> vars = new ArrayList<String>();
				for(String s : lines){
					vars.add(s);
				}
				return vars;
			}
		} finally {
			if (stream != null)
				stream.close();
		}
		return null;
	}
	
	public static String formatTime(Date time, String format) {
		   SimpleDateFormat form = new SimpleDateFormat(format);  
		   return form.format(time);
		  }  

}
