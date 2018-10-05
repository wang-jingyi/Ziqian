package io.github.wang_jingyi.ZiQian.refine;

import io.github.wang_jingyi.ZiQian.main.PlatformDependent;

public class SwatSampler implements Sampler{
	
	private boolean realWorld = true; // whether we use real data or simulation data
	private int sampleStep;
	private int recordStep;
	private int runningTime;
	private String outputFilePath;
	private String latestFilePath;
	private int simulationCount;
	private String swat_path = PlatformDependent.SWAT_SIMULATE_PATH+"/JingyiRS.py";
	
	public SwatSampler() {
		super();
	}
	
	public SwatSampler(String swat_path, boolean realWorld, String outputFilePath, int sampleStep, int recordStep, int runningTime) {
		this.swat_path = swat_path;
		this.realWorld = realWorld;
		this.sampleStep = sampleStep;
		this.recordStep = recordStep;
		this.runningTime = runningTime;
	}
	
	public SwatSampler(boolean realWorld, String outputFilePath, int sampleStep, int recordStep, int runningTime) {
		this.realWorld = realWorld;
		this.sampleStep = sampleStep;
		this.recordStep = recordStep;
		this.runningTime = runningTime;
	}
	
	
	@Override
	public void sample() {
		if(!realWorld){ // only sample when we obtain data from simulation
			String[] simulateCommand = new String[]{"python", swat_path, String.valueOf(sampleStep)
					, String.valueOf(recordStep), String.valueOf(runningTime), outputFilePath, String.valueOf(simulationCount)};
			ShellInteraction.executeCommand(simulateCommand);
			simulationCount++;
		}
		latestFilePath = outputFilePath + "/path_" + simulationCount + ".txt";
	}

	@Override
	public String getOutputFilePath() {
		return outputFilePath;
	}

	@Override
	public String getLatestSample() {
		return latestFilePath;
	}

	@Override
	public boolean isObtainingNewSample() {
		return false;
	}

	@Override
	public boolean isDecomposed() {
		return false;
	}

	@Override
	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	
}
