package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.run.GlobalVars;
import io.github.wang_jingyi.ZiQian.run.PlatformDependent;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.NumberUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

public class SwatApproMatrix {
	
	public static void main(String[] args){
		System.out.println("Add sensors, define abstractions...");
		SwatSensorAbstraction ssa = new SwatSensorAbstraction();
//		ssa.addSensor("LIT101", new Interval(200, 1100), 500);
		ssa.addSensor("LIT301", new Interval(200, 1000), 500);
		ssa.addSensor("LIT401", new Interval(200, 1000), 500);
//		ssa.addSensor("LS601", new Interval(200, 8000), 5000);
		ssa.addSensor("LS602", new Interval(200, 1000), 500);
		
		ALConfig.stateNumber = ssa.getStateNumber(); // update state number according to sensor abstraction
		List<String> swatPathRoot = new ArrayList<String>();
		swatPathRoot.add(PlatformDependent.SWAT_SIMULATE_PATH + "/Jingyi_Data/10_5");
		swatPathRoot.add(PlatformDependent.SWAT_SIMULATE_PATH + "/Jingyi_Data_1/10_5");
		swatPathRoot.add(PlatformDependent.SWAT_SIMULATE_PATH + "/Jingyi_Data_2/10_5");
		swatPathRoot.add(PlatformDependent.SWAT_SIMULATE_PATH + "/Jingyi_Data_3/10_5");
		
		 
		System.out.println("number of states: " + ALConfig.stateNumber);
		if(ALConfig.stateNumber>500){
			System.out.println("building sparse matrix...");
			ALConfig.sparse = true;
		}
		
		System.out.println("collecting initial training traces");
		// original traces
		List<SwatTrace> sts = new ArrayList<SwatTrace>();
		for(String swatPaths : swatPathRoot){
			for(String s : FileUtil.filesInDir(swatPaths)){
				SwatTrace st = new SwatTrace(s);
				try {
					st.collectTraceFromPath(ssa);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				sts.add(st);
			}
		}
		
		System.out.println("max sensor values in the trace: " + NumberUtil.ArrayToString(GlobalVars.maxSensorValues));
		System.out.println("min sensor values in the trace: " + NumberUtil.ArrayToString(GlobalVars.minSensorValues));
		
		
		List<List<Integer>> abstractTraces = new ArrayList<List<Integer>>();
		for(int i=0; i<sts.size(); i++){
			List<SwatState> ssl = sts.get(i).getTrace();
			List<Integer> at = new ArrayList<Integer>();
			for(SwatState ss : ssl){
				List<Integer> sensorValues = ss.getSensorValues();
				at.add(ssa.swatStateIndex(sensorValues));
			}
			abstractTraces.add(at);
		}
		
		RealMatrix frequencyMatrix = Samples.getFrequencyMatrix(abstractTraces, ALConfig.stateNumber);
		Estimator estimator = new LaplaceEstimator();
//		estimator estimator = new EmpiricalFrequencyEstimator();
//		estimator estimator = new GoodTuringEstimator();
		RealMatrix estTransMatrix = estimator.estimate(frequencyMatrix);
		try {
			FileUtil.writeObject(Config.PROJECT_ROOT + "/tmp/swat_ssa", ssa);
			FileUtil.writeObject(Config.PROJECT_ROOT + "/tmp/swat_fre_matrix", frequencyMatrix);
			FileUtil.writeObject(Config.PROJECT_ROOT + "/tmp/swat_est_matrix", estTransMatrix);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
