package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.run.GlobalVars;
import io.github.wang_jingyi.ZiQian.swat.SwatSimulation;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SwatSampler implements Sampler {
	
	
	private SwatBridge bridge;
	private String tracePath;
	private int fileCount = 0;
	
	public SwatSampler(SwatBridge bridge, String tracePath) {
		this.bridge = bridge;
		this.tracePath = tracePath;
	}

	@Override
	public List<Integer> newSample(double[] initDistribution, int sampleLength) {
		List<Integer> newTrace = new ArrayList<Integer>();
		int startState = MarkovChain.nextState(initDistribution);
		double[] initConfig = bridge.generateInput(startState);
		
		// pass initial configuration of sensors to python simulator and generate a trace
		String initConfigFile = Config.TMP_PATH + "/init_config.txt";
		String ic = "";
		for(double d : initConfig){
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
		SwatSimulation.simulate(Config.SWAT_SAMPLE_STEP, Config.SWAT_RECORD_STEP, Config.SWAT_RUNNING_TIME, tracePath, fileCount);
		fileCount ++;
		String newTracePath = tracePath + "/path_" + fileCount +".txt";
		
		SwatTrace st = new SwatTrace(newTracePath);
		try {
			st.collectTraceFromPath(bridge.getSsa());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(SwatState ss : st.getTrace()){
			if(bridge.getSsp().getSwatStateMap().containsKey(ss)){
				newTrace.add(bridge.getSsp().getSwatStateMap().get(ss));
			}
			else{ // new state occurs, update state pool
				System.out.println("new state, updatistate pool...");
				GlobalVars.newStateNumber ++;
				int stateIndex = bridge.getSsp().getStateNumber();
				bridge.getSsp().getSwatStateMap().put(ss, stateIndex);
				bridge.getSsp().getReSwatStateMap().put(stateIndex, ss);
				bridge.getSsp().setStateNumber(++stateIndex);
			}
		}
 		return newTrace;
	}

}
