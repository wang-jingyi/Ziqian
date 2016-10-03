package io.github.wang_jingyi.ZiQian;


import java.io.FileNotFoundException;

import io.github.wang_jingyi.ZiQian.profile.TimeProfile;
import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.sample.ShellInteraction;
import io.github.wang_jingyi.ZiQian.utils.StringUtil;

public class CheckLearned {
	
	private String prismFilePath;
	private String propertyFilePath;
	private int propertyIndex;
	
	
	public CheckLearned(String pfp, String propf, int pi){
		this.prismFilePath = pfp;
		this.propertyFilePath = propf;
		this.propertyIndex = pi;
	}
	
	public void check() throws FileNotFoundException{
		
		// first check if the given property holds
		String[] prismCommandParas = new String[]{Config.PRISM_PATH,
				prismFilePath, propertyFilePath, "-prop", String.valueOf(propertyIndex)};
		System.out.println("prism commands: " + StringUtil.arrayToString(prismCommandParas));
		String result = ShellInteraction.executeCommand(prismCommandParas);
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
			System.exit(0);
		}
		
	}
	
}
