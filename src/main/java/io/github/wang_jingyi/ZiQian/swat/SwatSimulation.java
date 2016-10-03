package io.github.wang_jingyi.ZiQian.swat;

import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.run.PlatformDependent;
import io.github.wang_jingyi.ZiQian.sample.ShellInteraction;

import java.io.IOException;

public class SwatSimulation {
	
	public static void simulate(int sampleSize, int recordStep, int runningTime, String dp, int sc ){
		String[] simulateCommand = new String[]{"python",PlatformDependent.SWAT_SIMULATE_PATH+"/Jingyi.py",String.valueOf(sampleSize)
			, String.valueOf(recordStep), String.valueOf(runningTime), dp, String.valueOf(sc)};
		ShellInteraction.executeCommand(simulateCommand);
	}
	
	public static void main(String[] args) throws IOException{
		Config.initConfig();
		for(int i=51; i<=100; i++){
			System.out.println("swat simulation: " + i);
			SwatSimulation.simulate(5, 1, 30,Config.DATA_PATH, i);
		}
	}
	
}
