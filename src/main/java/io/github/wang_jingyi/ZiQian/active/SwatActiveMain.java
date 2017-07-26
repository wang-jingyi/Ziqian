package io.github.wang_jingyi.ZiQian.active;

import gurobi.GRBException;
import io.github.wang_jingyi.ZiQian.main.Config;
import io.github.wang_jingyi.ZiQian.main.GlobalConfigs;
import io.github.wang_jingyi.ZiQian.main.PlatformDependent;
import io.github.wang_jingyi.ZiQian.main.TimeProfile;
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
		
		ALConfig.stateNumber = 64;
		String name_suffix = "state_" + ALConfig.stateNumber;
		String actual_result_root = PlatformDependent.CAV_MODEL_ROOT +  "/active/swat/" + "swat_"+Config.SWAT_SAMPLE_STEP+"_"+
				Config.SWAT_RECORD_STEP + "/" + name_suffix;
		String result_root = PlatformDependent.CAV_MODEL_ROOT +  "/active/swat/" + "swat_"+Config.SWAT_SAMPLE_STEP+"_"+
				Config.SWAT_RECORD_STEP + "/" + name_suffix + "/new_" + ALConfig.newSampleNumber;
		String ido_result_root = result_root+"/ido";
		String rs_result_root = result_root+"/rs";
		
		System.out.println("Add sensors, define abstractions...");
		
		StringBuilder logInfo = new StringBuilder();
		
		SwatSensorAbstraction ssa = (SwatSensorAbstraction) FileUtil.readObject(actual_result_root + "/swat_ssa");
		
		logInfo.append("\n\n");
		logInfo.append(ssa.toString());
		logInfo.append("\n\n");
		
		logInfo.append("sample length: " + ALConfig.pathLength + " minutes \n");
		logInfo.append("number of new sample: " + ALConfig.newSampleNumber + "\n");
		
		RealMatrix estExactMatrix = (RealMatrix) FileUtil.readObject(actual_result_root + "/swat_est_matrix");
		List<Double> swatReachProbs = (List<Double>) FileUtil.readObject(actual_result_root + "/swat_reach");
		
		logInfo.append("reachability probs: " + swatReachProbs + "\n");
		
//		List<Double> nonZeroSwatReachProbs = NumberUtil.nonZeroList(swatReachProbs);
//		System.out.println("non zero reachability probabilities: " + nonZeroSwatReachProbs);
		
		double thres = 0.1;
		List<Integer> targetStates = new ArrayList<Integer>();
		List<Double> targetReachProbs = new ArrayList<Double>();
		for(int i=0; i<swatReachProbs.size(); i++){
			if(swatReachProbs.get(i)>0 && swatReachProbs.get(i)<=1){ // only observe target states with small reachability
				targetStates.add(i);
				targetReachProbs.add(swatReachProbs.get(i));
			}
		}
		FileUtil.writeObject(actual_result_root+"/target_states", targetStates);
		System.out.println("number of target states: " + targetStates.size());
		
		ALConfig.stateNumber = ssa.getStateNumber(); // update state number according to sensor abstraction
		ALConfig.boundedSteps = ALConfig.stateNumber;
		
		String swatTrainPaths= PlatformDependent.SWAT_SIMULATE_PATH + "/samples_train_1"; // initial data for base estimation
		 
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
		
		System.out.println("max sensor values in the trace: " + NumberUtil.ArrayToString(GlobalConfigs.maxSensorValues));
		System.out.println("min sensor values in the trace: " + NumberUtil.ArrayToString(GlobalConfigs.minSensorValues));
		
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
		
//		FileUtil.cleanDirectory(PlatformDependent.CAV_MODEL_ROOT + "/active/swat/new_sample_ido" + name_suffix);//
//		FileUtil.cleanDirectory(PlatformDependent.CAV_MODEL_ROOT + "/active/swat/new_sample_rs" + name_suffix);
		
		Estimator estimator = new EFEstimator();
		ActiveSampler idosampler = new SwatActiveSampler(ssa, ido_result_root + "/new_sample", ALConfig.pathLength);
		InitialDistGetter idoidg = new InitialDistributionOptimizer(ALConfig.stateNumber, validInitStates, ALConfig.pathLength);
		
		ActiveSampler rssampler = new SwatActiveSampler(ssa, rs_result_root + "/new_sample", ALConfig.pathLength);
		InitialDistGetter uniformidg = new OriginalInitialDistribution(validInitStates,validInitDist);
		
		try {
			long ido_start_time = System.nanoTime();
			Samples idosample = IterSampling(frequencyMatrix.copy(), ALConfig.newSampleNumber, estimator, idosampler, idoidg);
			long ido_end_time = System.nanoTime();
			double ido_time = TimeProfile.nanoToSeconds(ido_end_time-ido_start_time);
			
			Reachability idoReach = new Reachability(idosample.getEstimatedTransitionMatrix(), validInitStates, validInitDist, targetStates,
					ido_result_root, "ido", ALConfig.boundedSteps);
			List<Double> idoReachProbs = idoReach.computeReachability();
			double ido_mv = MetricComputing.calculateNonZeroMinFreq(idosample.getFrequencyMatrix());
			double ido_mse = MetricComputing.calculateMSE(estExactMatrix, idosample.getEstimatedTransitionMatrix());
			List<Double> ido_abs_per_diff = ListUtil.listABSPercThresDiff(targetReachProbs, idoReachProbs, thres); 
			List<Double> ido_abs_diff = ListUtil.listABSThresDiff(targetReachProbs, idoReachProbs, thres);
			double ido_rrd = ListUtil.listMean(ido_abs_per_diff);
			double ido_rd = ListUtil.listMean(ido_abs_diff);
			FileUtil.writeObject(ido_result_root+"/ido_reach", idoReachProbs);
			Double ido_tv = ListUtil.listSum(MetricComputing.calculateTargetStateFreq(idosample.getFrequencyMatrix(),targetStates));
			
			logInfo.append("ido min frequency: " + ido_mv + "\n");
			logInfo.append("ido target stats frequency: " + ido_tv + "\n");
			logInfo.append("ido mse: " + ido_mse + "\n");
			logInfo.append("ido reachability probs: " + idoReachProbs + "\n");
			logInfo.append("ido reachability abs average diff: " + ido_rd + "\n");
			logInfo.append("ido reachability average diff: " + ido_rrd + "\n");
			logInfo.append("ido time cost: " + ido_time + "\n");
			
			ALConfig.ido = false;
			
			long rs_start_time = System.nanoTime();
			Samples uniformsample = IterSampling(frequencyMatrix.copy(), ALConfig.newSampleNumber, estimator, rssampler, uniformidg);
			long rs_end_time = System.nanoTime();
			double rs_time = TimeProfile.nanoToSeconds(rs_end_time-rs_start_time);
			
			Reachability rsReach = new Reachability(uniformsample.getEstimatedTransitionMatrix(), validInitStates, validInitDist, targetStates,
					rs_result_root, "rs", ALConfig.boundedSteps);
			List<Double> rsReachProbs = rsReach.computeReachability();
			FileUtil.writeObject(rs_result_root+"/rs_reach", rsReachProbs);
			
			double rs_mv = MetricComputing.calculateNonZeroMinFreq(uniformsample.getFrequencyMatrix());
			double rs_mse = MetricComputing.calculateMSE(estExactMatrix, uniformsample.getEstimatedTransitionMatrix());
			List<Double> rs_abs_per_diff = ListUtil.listABSPercThresDiff(targetReachProbs, rsReachProbs, thres);
			List<Double> rs_abs_diff = ListUtil.listABSThresDiff(targetReachProbs, rsReachProbs, thres);
			double rs_rrd = ListUtil.listMean(rs_abs_per_diff);
			double rs_rd = ListUtil.listMean(rs_abs_diff);
			double rs_tv = ListUtil.listSum(MetricComputing.calculateTargetStateFreq(uniformsample.getFrequencyMatrix(), targetStates));
			
			logInfo.append("random sampling min frequency: " + rs_mv + "\n");
			logInfo.append("random target stats frequency: " + rs_tv + "\n");
			logInfo.append("random sampling mse: " + rs_mse + "\n");
			logInfo.append("random sampling reachability probs: " + rsReachProbs + "\n");
			logInfo.append("rs reachability abs average diff: " + rs_rd + "\n");
			logInfo.append("random sampling reachability average diff: " + rs_rrd+ "\n");
			logInfo.append("rs time cost: " + rs_time + "\n");
			
			System.out.println("ido time cost: " + ido_time + " s\n");
			System.out.println("rs time cost: " + rs_time + "s\n");
			System.out.println("ido mv: " + ido_mv + "\n");
			System.out.println("rs mv: " + rs_mv + "\n");
			System.out.println("ido tv: " + ido_tv + "\n");
			System.out.println("rs tv: " + rs_tv + "\n");
			System.out.println("ido mse: " + ido_mse + "\n");
			System.out.println("rs mse: " + rs_mse + "\n");
			System.out.println("ido rd: " + ido_rd + "\n");
			System.out.println("rs rd: " + rs_rd + "\n");
			System.out.println("ido rrd: " + ido_rrd + "\n"); // only average on non-zero differences
			System.out.println("rs rrd: " + rs_rrd + "\n");
			
			FileUtil.appendStringToFile(Config.TMP_PATH + "/swat_logs", logInfo.toString());
			
		} catch (GRBException e) {
			e.printStackTrace();
		}
	
	} 
	
	public static Samples IterSampling(RealMatrix currentFrequencyMatrix, int numSample, Estimator estimator, 
			ActiveSampler sampler, InitialDistGetter idg) throws GRBException{
		Samples sample = new Samples(ALConfig.pathLength, currentFrequencyMatrix, estimator, sampler, idg);
		for(int i=1; i<=numSample; i++){
			System.out.println("getting a new sample, number: " + i);
			sample.newSample();
		}
		return sample;

	}
}
