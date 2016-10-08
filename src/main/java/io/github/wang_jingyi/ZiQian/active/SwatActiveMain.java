package io.github.wang_jingyi.ZiQian.active;

import gurobi.GRBException;
import io.github.wang_jingyi.ZiQian.run.GlobalVars;
import io.github.wang_jingyi.ZiQian.run.PlatformDependent;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.NumberUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SwatActiveMain {
	
	
	public static void main(String[] args) throws FileNotFoundException{
		
		System.out.println("Add sensors, define abstractions...");
		SwatSensorAbstraction ssa = new SwatSensorAbstraction();
		ssa.addSensor("LIT101", new Interval(200, 1100), 100);
		ssa.addSensor("LIT301", new Interval(200, 1000), 100);
		ssa.addSensor("LIT401", new Interval(200, 1100), 100);
		ssa.addSensor("LS601", new Interval(200, 8000), 1000);
		ssa.addSensor("LS602", new Interval(200, 1200), 100);
		
		System.out.println("collecting traces, build state pool...");
		// original traces
		List<SwatTrace> sts = new ArrayList<SwatTrace>();
		for(String s : FileUtil.filesInDir(PlatformDependent.MODEL_ROOT + "/swat/10,5/paths")){
			SwatTrace st = new SwatTrace(s);
			st.collectTraceFromPath(ssa);
			sts.add(st);
		}
		
		System.out.println("max sensor values in the trace: " + NumberUtil.ArrayToString(GlobalVars.maxSensorValues));
		System.out.println("min sensor values in the trace: " + NumberUtil.ArrayToString(GlobalVars.minSensorValues));
		
		SwatStatePool ssp = new SwatStatePool(sts);
		ssp.buildPool();
		
		List<List<Integer>> abstractTraces = new ArrayList<List<Integer>>();
		for(int i=0; i<sts.size(); i++){
			List<SwatState> ssl = sts.get(i).getTrace();
			List<Integer> at = new ArrayList<Integer>();
			for(SwatState ss : ssl){
				at.add(ssp.getSwatStateMap().get(ss));
			}
			abstractTraces.add(at);
		}
		
		int[][] frequencyMatrix = SampleStatisticGetter.getFrequencyMatrix(abstractTraces, ssp.getStateNumber());
		int pathLength = ssp.getStateNumber();
		int sampleNumber = 10;
		
		List<Integer> validInitStates = new ArrayList<>();
		List<Double> validInitDist = new ArrayList<Double>();
		for(int i=0; i<ssp.getStateNumber(); i++){
			validInitStates.add(i);
			validInitDist.add((double)1/ssp.getStateNumber());
		}
		Estimator estimator = new LaplaceEstimator();
		Sampler sampler = new SwatSampler(new SwatBridge(ssp, ssa), PlatformDependent.MODEL_ROOT + "/new_sample");
		InitialDistGetter idoidg = new InitialDistributionOptimizer(ssp.getStateNumber(), validInitStates, pathLength);
//		InitialDistGetter uniformidg = new UniformInitialDistribution(ssp.getStateNumber(), validInitStates);
		
		try {
			Samples idosample = IterSampling(frequencyMatrix, validInitStates, pathLength, sampleNumber, estimator, sampler, idoidg);
//			Samples uniformsample = IterSampling(frequencyMatrix, validInitStates, pathLength, sampleNumber, estimator, sampler, uniformidg);
			System.out.println(idosample.toString());
//			System.out.println(uniformsample.toString());
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	} 
	
	public static Samples IterSampling(int[][] currentFrequencyMatrix, List<Integer> validInitStates, int sampleLength, int numSample, Estimator estimator, 
			Sampler sampler, InitialDistGetter idg) throws GRBException{
		Samples sample = new Samples(sampleLength, currentFrequencyMatrix, estimator, sampler, idg);
		for(int i=1; i<=numSample; i++){
			System.out.println("getting a new sample, number: " + i);
			sample.newSample();
		}
		return sample;

	}
}
