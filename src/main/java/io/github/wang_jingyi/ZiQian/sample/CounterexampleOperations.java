package io.github.wang_jingyi.ZiQian.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// extract counterexample from generated counterexample file

public class CounterexampleOperations {
	
	public static List<CounterexamplePath> extractCounterexample(String cpFilePath){
		List<CounterexamplePath> cps = new ArrayList<>();
		try {
			File file = new File(cpFilePath);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if(Character.isDigit(line.charAt(0))){
					Pattern p = Pattern.compile("-?\\d+|-?\\d+.\\d+");
					Matcher m = p.matcher(line);
					List<String> pathstr = new ArrayList<>();
					while (m.find()) {
						pathstr.add(m.group());
					}
					List<Integer> path = new ArrayList<>();
					for(int i=0; i<pathstr.size()-1; i++){
						path.add(Integer.valueOf(pathstr.get(i)));
					}
					System.out.println("a path in counterexample: " + path);
					CounterexamplePath cp = new CounterexamplePath(path);
					cps.add(cp);
				}
			}
			fileReader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cps;
	}
	
}
