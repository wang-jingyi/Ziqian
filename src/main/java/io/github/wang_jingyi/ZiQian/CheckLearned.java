package io.github.wang_jingyi.ZiQian;


import io.github.wang_jingyi.ZiQian.exceptions.PrismNoResultException;
import io.github.wang_jingyi.ZiQian.main.AlgoProfile;
import io.github.wang_jingyi.ZiQian.main.PlatformDependent;
import io.github.wang_jingyi.ZiQian.main.TimeProfile;
import io.github.wang_jingyi.ZiQian.refine.ShellInteraction;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

import java.io.IOException;

public class CheckLearned {
	
	private String prismFilePath;
	private String propertyFilePath;
	private int propertyIndex;
	
	
	public CheckLearned(String pfp, String propf, int pi){
		this.prismFilePath = pfp;
		this.propertyFilePath = propf;
		this.propertyIndex = pi;
	}
	
	public void check() throws IOException, PrismNoResultException{
		System.out.println("------ PRISM model checking ------");
		// first check if the given property holds
		String os_type = System.getProperty("os.name");
		String[] prismCommandParas = new String[]{};
		String winCommand = "";
		String result = "";
		if(os_type.startsWith("Windows")){
			winCommand = "cmd /c cd " + PlatformDependent.PRISM_PATH + " && prism.bat " + prismFilePath + " " + propertyFilePath + " -prop " + String.valueOf(propertyIndex);
			result = ShellInteraction.executeCommand(winCommand);
		}
		else if(os_type.startsWith("Mac")||os_type.startsWith("Ubuntu")){
			prismCommandParas = new String[]{PlatformDependent.PRISM_PATH,
					prismFilePath, propertyFilePath, "-prop", String.valueOf(propertyIndex)};
			result = ShellInteraction.executeCommand(prismCommandParas);
		}
		
		TimeProfile.pmc_end_time = System.nanoTime();
		TimeProfile.prism_model_check_times.add
		(TimeProfile.nanoToSeconds(TimeProfile.pmc_end_time-TimeProfile.pmc_start_time));
		
		if(result.contains("Result: false")){ // if prism returns
			System.out.println("- PRISM result: false");
			System.out.println("=== Proceed to next step of refinement ===");
		}
		else if(result.contains("Result: true")){
			TimeProfile.iteration_end_time = System.nanoTime();
			TimeProfile.iteration_times.add(TimeProfile.nanoToSeconds
					(TimeProfile.iteration_end_time-TimeProfile.iteration_start_time));
			System.out.println("- PRISM result: true");
			System.out.println("====== The property has been verified ======");
			AlgoProfile.result = "true";
			TimeProfile.main_end_time = System.nanoTime();
			TimeProfile.main_time = TimeProfile.nanoToSeconds(TimeProfile.main_end_time-TimeProfile.main_start_time);
			TimeProfile.outputTimeProfile();
			TimeProfile.outputTimeProfile(AlgoProfile.result_output_path + "/time_profile.txt");
			FileUtil.writeObject(AlgoProfile.result_output_path + "/predicates", AlgoProfile.predicates);
			System.exit(0);
		}
		else{
			throw new PrismNoResultException();
		}
	}
	
	
	public static void main(String[] args){
		System.out.println(System.getProperty("os.name"));
	}
	
	
}
