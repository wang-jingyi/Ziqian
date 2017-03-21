package io.github.wang_jingyi.ZiQian;

import java.io.IOException;

import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.profile.TimeProfile;
import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.run.PlatformDependent;
import io.github.wang_jingyi.ZiQian.sample.ShellInteraction;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

public class CheckModel {
	
	private String prismFilePath;
	private String propertyFilePath;
	private int propertyIndex;
	
	
	public CheckModel(String pfp, String propf, int pi){
		this.prismFilePath = pfp;
		this.propertyFilePath = propf;
		this.propertyIndex = pi;
	}
	
	public void check() throws IOException{
		
		// first check if the given property holds
//		String[] prismCommandParas = new String[]{"cd", PlatformDependent.PRISM_PATH, "&&", "prism.bat",
//				prismFilePath, propertyFilePath, "-prop", String.valueOf(propertyIndex)};
		String winCommand = "cmd /c cd " + PlatformDependent.PRISM_PATH + " && prism.bat " + prismFilePath + " " + propertyFilePath + " -prop " + String.valueOf(propertyIndex);
//		System.out.println("prism commands: " + StringUtil.arrayToString(prismCommandParas));
		System.out.println("prism commands: " + winCommand);
		String result = ShellInteraction.executeCommand(winCommand);
		System.out.println(result);
		
		if(result.contains("Result: false")){ // if prism returns
			// if not, get the counterexample
			System.out.println("Generating counterexample...");
		}
		else{
			System.out.println("The property has been verified.");
			TimeProfile.mainEndTime = System.nanoTime();
			TimeProfile.outputTimeProfile();
			TimeProfile.outputTimeProfile(Config.OUTPUT_MODEL_PATH + "/time.txt");
			FileUtil.writeObject(Config.OUTPUT_MODEL_PATH + "/predicates", AlgoProfile.predicates);
			System.exit(0);
		}
		
	}
	

}
