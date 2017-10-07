package io.github.wang_jingyi.ZiQian.evaluation;

import java.util.ArrayList;
import java.util.List;

import io.github.wang_jingyi.ZiQian.main.PlatformDependent;
import io.github.wang_jingyi.ZiQian.refine.ShellInteraction;
import io.github.wang_jingyi.ZiQian.utils.PrismUtil;

public class JournalLSE {
	
	static String model_root = "/Users/jingyi/Ziqian_evaluation/model/leadersync";
	static String actual_prism_model = model_root + "/leadersync.pm";
	static String actual_prism_prop = model_root + "/leadersync.pctl";
	static String learn_prop = model_root + "/leadersync_learn.pctl";
	static String AA_model = "/Users/jingyi/Ziqian_evaluation/model/leadersync/N=4,K=6/s1.s2.s3.s4.s5.s6./learned_model/prefix/leadersync_100000.pm";
	static String GA_model = "/Users/jingyi/Ziqian_evaluation/model/leadersync/N=4,K=6/s1.s2.s3.s4.s5.s6./learned_model/genetic/leadersync_100000.pm";
	
	public static void main(String[] args){
		
		List<Double> actual_result = new ArrayList<>();
		List<Double> aa_result = new ArrayList<>();
		List<Double> ga_result = new ArrayList<>();
		
//		for(int i=1; i<=15; i++){
//
//			String[] command = new String[]{PlatformDependent.PRISM_PATH, actual_prism_model, 
//					"-const", "N=4,K=6", actual_prism_prop, "-prop", Integer.toString(i)};
//			String output = ShellInteraction.executeCommand(command);
//			double result = PrismUtil.extractResultFromCommandOutput(output);
//			actual_result.add(result);
//		}
//		System.out.println("actual results : " + actual_result);
		
		for(int i=1; i<=15; i++){

			String[] command = new String[]{PlatformDependent.PRISM_PATH, AA_model, 
					learn_prop, "-prop", Integer.toString(i)};
			String output = ShellInteraction.executeCommand(command);
			double result = PrismUtil.extractResultFromCommandOutput(output);
			aa_result.add(result);
		}
		System.out.println("AA results : " + aa_result);
		
		for(int i=1; i<=15; i++){

			String[] command = new String[]{PlatformDependent.PRISM_PATH, GA_model, 
					learn_prop, "-prop", Integer.toString(i)};
			String output = ShellInteraction.executeCommand(command);
			double result = PrismUtil.extractResultFromCommandOutput(output);
			ga_result.add(result);
		}
		System.out.println("GA results : " + ga_result);
		
	}

}
