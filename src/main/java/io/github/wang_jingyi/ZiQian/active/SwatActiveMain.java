package io.github.wang_jingyi.ZiQian.active;

import gurobi.GRBException;
import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.run.GlobalVars;
import io.github.wang_jingyi.ZiQian.run.PlatformDependent;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.ListUtil;
import io.github.wang_jingyi.ZiQian.utils.NumberUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

public class SwatActiveMain {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ClassNotFoundException, IOException{
		
		System.out.println("Add sensors, define abstractions...");
		
		StringBuilder logInfo = new StringBuilder();
		
		SwatSensorAbstraction ssa = (SwatSensorAbstraction) FileUtil.readObject(Config.PROJECT_ROOT + "/tmp/swat_ssa");
		
		logInfo.append("\n\n");
		logInfo.append(ssa.toString());
		logInfo.append("\n\n");
		
		logInfo.append("sample length: " + ALConfig.pathLength + " minutes \n");
		logInfo.append("number of new sample: " + ALConfig.newSampleNumber + "\n");
		
		RealMatrix estExactMatrix = (RealMatrix) FileUtil.readObject(Config.PROJECT_ROOT + "/tmp/swat_est_matrix");
		List<Double> swatReachProbs = (List<Double>) FileUtil.readObject(Config.PROJECT_ROOT + "/tmp/swat_reach");
		
		logInfo.append("reachability probs: " + swatReachProbs + "\n");
		
		List<Double> nonZeroSwatReachProbs = NumberUtil.nonZeroList(swatReachProbs);
		System.out.println("non zero reachability probabilities: " + nonZeroSwatReachProbs);
		
		List<Integer> targetStates = new ArrayList<Integer>();
		for(int i=0; i<swatReachProbs.size(); i++){
			if(swatReachProbs.get(i)!=0){
				targetStates.add(i);
			}
		}
		
		ALConfig.stateNumber = ssa.getStateNumber(); // update state number according to sensor abstraction
		
		String swatTrainPaths= PlatformDependent.SWAT_SIMULATE_PATH + "/Jingyi_Data/10_5";
		 
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
		
		List<Integer> validInitStates = ssa.getInitialStates();
		List<Double> validInitDist = new ArrayList<Double>();
		for(int i=0; i<ALConfig.stateNumber; i++){
			validInitDist.add((double)1/validInitStates.size());
		}
		
		FileUtil.cleanDirectory(PlatformDependent.MODEL_ROOT + "/active/swat/new_sample_ido");//
		FileUtil.cleanDirectory(PlatformDependent.MODEL_ROOT + "/active/swat/new_sample_rs");
		
		Estimator estimator = new EmpiricalFrequencyEstimator();
		Sampler idosampler = new SwatSampler(new SwatBridge(ssa), 
				PlatformDependent.MODEL_ROOT + "/active/swat/new_sample_ido", ALConfig.pathLength);
		InitialDistGetter idoidg = new InitialDistributionOptimizer(ALConfig.stateNumber, validInitStates, ALConfig.pathLength);
		Sampler rssampler = new SwatSampler(new SwatBridge(ssa), 
				PlatformDependent.MODEL_ROOT + "/active/swat/new_sample_rs", ALConfig.pathLength);
		InitialDistGetter uniformidg = new UniformInitialDistribution(validInitStates);
		
		try {
			
			Samples idosample = IterSampling(frequencyMatrix, ALConfig.newSampleNumber, estimator, idosampler, idoidg);
			Reachability idoReach = new Reachability(idosample.getEstimatedTransitionMatrix(), validInitStates, targetStates,
					PlatformDependent.MODEL_ROOT + "/active/swat", "swat_10_5_ido", ALConfig.boundedSteps);
			List<Double> idoReachProbs = idoReach.computeReachability();
			double idominfre = MetricComputing.calculateNonZeroMinFreq(idosample.getFrequencyMatrix());
			double idomse = MetricComputing.calculateMSE(estExactMatrix, idosample.getEstimatedTransitionMatrix());
			List<Double> idoDiff = ListUtil.listABSDiff(nonZeroSwatReachProbs, idoReachProbs);
			double idoMeanDiff = ListUtil.listMean(idoDiff);
			FileUtil.writeObject(Config.TMP_PATH + "/swat_ido_reach_probs", idoReachProbs);
			
			logInfo.append("ido min frequency: " + idominfre + "\n");
			logInfo.append("ido mse: " + idomse + "\n");
			logInfo.append("ido reachability probs: " + idoReachProbs + "\n");
			logInfo.append("ido reachability average diff: " + idoMeanDiff + "\n");
			
			ALConfig.ido = false;
			
			Samples uniformsample = IterSampling(frequencyMatrix, ALConfig.newSampleNumber, estimator, rssampler, uniformidg);
			Reachability rsReach = new Reachability(uniformsample.getEstimatedTransitionMatrix(), validInitStates, targetStates,
					PlatformDependent.MODEL_ROOT + "/active/swat", "swat_10_5_rs", ALConfig.boundedSteps);
			List<Double> rsReachProbs = rsReach.computeReachability();
			FileUtil.writeObject(Config.TMP_PATH + "/swat_rs_reach_probs", rsReachProbs);
			double rsminfre = MetricComputing.calculateNonZeroMinFreq(uniformsample.getFrequencyMatrix());
			double rsmse = MetricComputing.calculateMSE(estExactMatrix, uniformsample.getEstimatedTransitionMatrix());
			List<Double> rsDiff = ListUtil.listABSDiff(nonZeroSwatReachProbs, rsReachProbs);
			double rsMeanDiff = ListUtil.listMean(rsDiff);
			logInfo.append("random sampling min frequency: " + rsminfre + "\n");
			logInfo.append("random sampling mse: " + rsmse + "\n");
			logInfo.append("random sampling reachability probs: " + rsReachProbs + "\n");
			logInfo.append("random sampling reachability average diff: " + rsMeanDiff + "\n");
			
			
			System.out.println("ido min fre: " + idominfre + "\n");
			System.out.println("rs min fre: " + rsminfre + "\n");
			System.out.println("ido mse: " + idomse + "\n");
			System.out.println("rs mse: " + rsmse + "\n");
			System.out.println("ido reach: " + idoMeanDiff + "\n"); // only average on non-zero differences
			System.out.println("rs reach: " + rsMeanDiff + "\n");
			
			FileUtil.appendStringToFile(Config.TMP_PATH + "/swat_logs", logInfo.toString());
			
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
