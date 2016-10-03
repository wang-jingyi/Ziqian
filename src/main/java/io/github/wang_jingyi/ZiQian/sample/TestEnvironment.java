package io.github.wang_jingyi.ZiQian.sample;

import io.github.wang_jingyi.ZiQian.PredicateAbstraction;
import io.github.wang_jingyi.ZiQian.PredicateSet;
import io.github.wang_jingyi.ZiQian.VariablesValue;
import io.github.wang_jingyi.ZiQian.prism.PrismPathData;
import io.github.wang_jingyi.ZiQian.profile.AlgoProfile;
import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.swat.SwatSimulation;

import java.io.IOException;
import java.util.List;

public class TestEnvironment {
	
	public static final TestEnvironment te = new TestEnvironment();
	private PredicateSet ps;
	private String modelPath;
	private String modelConfig;

	private String outputFilePath;
	private int simulationCount = 0; // total number of sampling
	
	private TestEnvironment(){
		
	}
	
	public void init(PredicateSet ps, String modelPath, String modelConfig, String outputFilePath){
		this.ps = ps;
		this.modelPath = modelPath;
		this.modelConfig = modelConfig;
		this.outputFilePath = outputFilePath;
	}
	
	public void sample(){
		if(Config.SWAT){
//			int time = 2 + new Random().nextInt(3);
//			TimeProfile.sampleStartTime = System.nanoTime();
			SwatSimulation.simulate(Config.SWAT_SAMPLE_STEP, Config.SWAT_RECORD_STEP, 1, outputFilePath,simulationCount);
			simulationCount ++;
//			TimeProfile.sampleEndTime = System.nanoTime();
//			System.out.println("time for one sample : " + TimeProfile.nanoToSeconds(TimeProfile.sampleEndTime-TimeProfile.sampleStartTime));
//			System.exit(0);
		}
		else{
			Simulation sim = new Simulation(modelPath, outputFilePath, "path_"+simulationCount, modelConfig);
			sim.run();
			simulationCount++;
		}
		
	}
	
	public boolean test(Counterexample ce) throws IOException, ClassNotFoundException{
		List<VariablesValue> vvs = PrismPathData.extractSEData(outputFilePath+"/path_"+(simulationCount-1)+".txt", 
				AlgoProfile.vars,Integer.MAX_VALUE); // variables values of last simulation
		PredicateAbstraction pa = new PredicateAbstraction(ps.getPredicates());
		List<String> absExs = pa.abstractList(vvs);
		return ce.checkMembership(absExs);
	}

	public String getModelPath() {
		return modelPath;
	}

	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}

	public String getModelConfig() {
		return modelConfig;
	}

	public void setModelConfig(String modelConfig) {
		this.modelConfig = modelConfig;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	
	public PredicateSet getPredicateSet() {
		return ps;
	}
	
	
}
