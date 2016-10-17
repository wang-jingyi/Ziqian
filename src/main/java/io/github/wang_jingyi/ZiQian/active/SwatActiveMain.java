package io.github.wang_jingyi.ZiQian.active;

import gurobi.GRBException;
import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.run.GlobalVars;
import io.github.wang_jingyi.ZiQian.run.PlatformDependent;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.NumberUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

public class SwatActiveMain {
	
	public static void main(String[] args) throws ClassNotFoundException, IOException{
		
		System.out.println("Add sensors, define abstractions...");
		
		SwatSensorAbstraction ssa = (SwatSensorAbstraction) FileUtil.readObject(Config.PROJECT_ROOT + "/tmp/swat_ssa");
		RealMatrix estExactMatrix = (RealMatrix) FileUtil.readObject(Config.PROJECT_ROOT + "/tmp/swat_est_matrix");
		
		ALConfig.stateNumber = ssa.getStateNumber(); // update state number according to sensor abstraction
		
		String swatTrainPaths= PlatformDependent.MODEL_ROOT + "/model/" + Config.modelPath;
		 
		System.out.println("number of states: " + ALConfig.stateNumber);
		if(ALConfig.stateNumber>500){
			System.out.println("building sparse matrix...");
			ALConfig.sparse = true;
		}
		
		System.out.println("collecting initial training traces");
		// original traces
		List<SwatTrace> sts = new ArrayList<SwatTrace>();
		for(String s : FileUtil.filesInDir(swatTrainPaths)){
			SwatTrace st = new SwatTrace(s);
			st.collectTraceFromPath(ssa);
			sts.add(st);
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
		
		List<Integer> validInitStates = new ArrayList<>();
		List<Double> validInitDist = new ArrayList<Double>();
		for(int i=0; i<ALConfig.stateNumber; i++){
			validInitStates.add(i);
			validInitDist.add((double)1/ALConfig.stateNumber);
		}
		Estimator estimator = new LaplaceEstimator();
		Sampler sampler = new SwatSampler(new SwatBridge(ssa), PlatformDependent.MODEL_ROOT + "/model" + 
				Config.modelPath + "/new_sample", ALConfig.pathLength);
		InitialDistGetter idoidg = new InitialDistributionOptimizer(ALConfig.stateNumber, validInitStates, ALConfig.pathLength);
		InitialDistGetter uniformidg = new UniformInitialDistribution(validInitStates);
		
		try {
			Samples idosample = IterSampling(frequencyMatrix, ALConfig.newSampleNumber, estimator, sampler, idoidg);
			
			ALConfig.ido = false;
			
			Samples uniformsample = IterSampling(frequencyMatrix, ALConfig.newSampleNumber, estimator, sampler, uniformidg);
			
			System.out.println("ido mse: " + MetricComputing.calculateMSE(estExactMatrix, idosample.getEstimatedTransitionMatrix()));
			System.out.println("rs mse: " + MetricComputing.calculateMSE(estExactMatrix, uniformsample.getEstimatedTransitionMatrix()));
			
		} catch (GRBException e) {
			e.printStackTrace();
		}
		
	
	} 
	
	public static Samples IterSampling(RealMatrix currentFrequencyMatrix, int numSample, Estimator estimator, 
			Sampler sampler, InitialDistGetter idg) throws GRBException{
		Samples sample = new Samples(currentFrequencyMatrix, estimator, sampler, idg);
		for(int i=1; i<=numSample; i++){
			System.out.println("getting a new sample, number: " + i);
			sample.newSample();
		}
		return sample;

	}
}
