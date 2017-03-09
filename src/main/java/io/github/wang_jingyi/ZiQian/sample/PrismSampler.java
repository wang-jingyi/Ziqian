package io.github.wang_jingyi.ZiQian.sample;

import io.github.wang_jingyi.ZiQian.run.Config;

public class PrismSampler implements Sampler{
	
	private String modelPath;
	private String simulationConfig; // "none" if unspecified
	private String outputFilePath;
	private String lastestSampleFilePath;
	private int simulationLength = Integer.MAX_VALUE;
	private int simulationCount = 0;
	
	public PrismSampler(String modelPath, String outputFilePath, String simulationConfig) {
		this.modelPath = modelPath;
		this.outputFilePath = outputFilePath;
		this.simulationConfig = simulationConfig;
	}
	
	@Override
	public void sample(){
		String[] commandParas = new String[]{};
		lastestSampleFilePath = outputFilePath+"/"+"path_" + simulationCount + ".txt";
		if(simulationConfig.equals("none")){
			commandParas = new String[]{Config.PRISM_PATH,
					modelPath,
					"-simpath",Integer.toString(simulationLength), lastestSampleFilePath};
		}
		else{
			commandParas = new String[]{Config.PRISM_PATH,
					modelPath,"-const", simulationConfig,
					"-simpath",Integer.toString(simulationLength), lastestSampleFilePath};
		}
		ShellInteraction.executeCommand(commandParas);
		
	}

	@Override
	public String getLastestSample() {
		return lastestSampleFilePath;
	}

	@Override
	public String getOutputFilePath() {
		return outputFilePath;
	}

	@Override
	public boolean isObtainingNewSample() {
		return true;
	}
	
}
