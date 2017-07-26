package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.main.Config;
import io.github.wang_jingyi.ZiQian.refine.Sampler;
import io.github.wang_jingyi.ZiQian.refine.SwatSampler;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SwatActiveSampler implements ActiveSampler{


	private SwatSensorAbstraction ssa;
	private String tracePath;
	private int sampleTime; // in minute

	public SwatActiveSampler(SwatSensorAbstraction ssa, String tracePath, int sampleTime) {
		this.ssa = ssa;
		this.tracePath = tracePath;
		this.sampleTime = sampleTime;
	}

	@Override
	public List<Integer> newSample(double[] initDistribution, int sampleLength) {
		List<Integer> newTrace = new ArrayList<Integer>();
		int startState = MarkovChain.nextState(initDistribution);
		double[] initConfig = ssa.generateInput(startState);

		double[] cpInitConfig = new double[ALConfig.totalSensorNumber];
		for(int i=0; i<ALConfig.sensorIndex.size(); i++){
			int j = ALConfig.sensorIndex.get(i);
			cpInitConfig[j] = initConfig[i];
		}
		for(int i=0; i<cpInitConfig.length; i++){
			if(cpInitConfig[i]==0){
				cpInitConfig[i] = 200 + new Random().nextDouble() * 800;
			}
		}

		// pass initial configuration of sensors to python simulator and generate a trace
		String initConfigFile = Config.TMP_PATH + "/init_config.txt";
		String ic = "";
		for(double d : cpInitConfig){
			ic += String.valueOf(d);
			ic += " ";
		}
		try {
			FileUtil.writeStringToFile(initConfigFile, ic);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// acquire abstract trace using SwatAbstraction
		assert sampleTime>0 : "no sample taken";
		Sampler sampler = new SwatSampler(false, tracePath, Config.SWAT_SAMPLE_STEP, Config.SWAT_RECORD_STEP, Config.SWAT_RUNNING_TIME);
		sampler.sample();
		String newTracePath = sampler.getLatestSample();
		SwatTrace st = new SwatTrace(newTracePath);
		try {
			st.collectTraceFromPath(ssa);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(SwatState ss : st.getTrace()){
			newTrace.add(ssa.swatStateIndex(ss.getSensorValues()));
		}


		try {
			if(ALConfig.ido){
				FileUtil.appendStringToFile(Config.TMP_PATH + "/ido_new_sample.txt", newTrace.toString());
			}
			else{
				FileUtil.appendStringToFile(Config.TMP_PATH + "/rs_new_sample.txt", newTrace.toString());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newTrace;
	}

}
