package io.github.wang_jingyi.ZiQian.sample;

import io.github.wang_jingyi.ZiQian.run.Config;

public class Simulation {
	
	private String modelPath;
	private String outputFilePath;
	private String outputFileName;
	private String simulationConfig; // "none" if unspecified
	private int simulationLength = Integer.MAX_VALUE;
	
	public Simulation(String modelPath, String outputFilePath, String name, String config){
		this.modelPath = modelPath;
		this.outputFilePath = outputFilePath;
		this.outputFileName = name;
		this.simulationConfig = config;
	}
	
	public void run(){
		if(simulationConfig.equals("none")){
			String[] commandParas = new String[]{Config.PRISM_PATH,
					modelPath,
					"-simpath",Integer.toString(simulationLength),outputFilePath+"/"+outputFileName+".txt"};
			ShellInteraction.executeCommand(commandParas);
		}
		else{
			String[] commandParas = new String[]{Config.PRISM_PATH,
					modelPath,"-const", simulationConfig,
					"-simpath",Integer.toString(simulationLength),outputFilePath+"/"+outputFileName+".txt"};
			ShellInteraction.executeCommand(commandParas);
		}
	}
	
}
