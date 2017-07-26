package io.github.wang_jingyi.ZiQian.active;

import gurobi.GRBException;
import io.github.wang_jingyi.ZiQian.main.Config;
import io.github.wang_jingyi.ZiQian.main.GlobalConfigs;
import io.github.wang_jingyi.ZiQian.main.PlatformDependent;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.ListUtil;
import io.github.wang_jingyi.ZiQian.utils.NumberUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

public class SwatReachabilityTest {

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		System.out.println("Add sensors, define abstractions...");

		SwatSensorAbstraction ssa = (SwatSensorAbstraction) FileUtil.readObject(Config.PROJECT_ROOT + "/tmp/swat_ssa");


		RealMatrix estExactMatrix = (RealMatrix) FileUtil.readObject(Config.PROJECT_ROOT + "/tmp/swat_est_matrix");
		@SuppressWarnings("unchecked")
		List<Double> swatReachProbs = (List<Double>) FileUtil.readObject(Config.PROJECT_ROOT + "/tmp/swat_reach");
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

		FileUtil.cleanDirectory(PlatformDependent.CAV_MODEL_ROOT + "/active/swat/new_sample_ido");//
//		FileUtil.cleanDirectory(PlatformDependent.CAV_MODEL_ROOT + "/active/swat/new_sample_ido_reach");

		Estimator estimator = new EFEstimator();
		ActiveSampler idosampler = new SwatActiveSampler(ssa, 
				PlatformDependent.CAV_MODEL_ROOT + "/active/swat/new_sample_ido", ALConfig.pathLength);
		InitialDistGetter idoidg = new InitialDistributionOptimizer(ALConfig.stateNumber, validInitStates, ALConfig.pathLength);
		ActiveSampler idosamplerReach = new SwatActiveSampler(ssa, 
				PlatformDependent.CAV_MODEL_ROOT + "/active/swat/new_sample_ido_reach", ALConfig.pathLength);
		InitialDistGetter idoidgReach = new ReachabilityOptimizer(ALConfig.stateNumber, validInitStates, targetStates, ALConfig.pathLength);

		try {

			Samples idosample = IterSampling(frequencyMatrix, ALConfig.newSampleNumber, estimator, idosampler, idoidg);
			Reachability idoReach = new Reachability(idosample.getEstimatedTransitionMatrix(), validInitStates, validInitDist, targetStates,
					PlatformDependent.CAV_MODEL_ROOT + "/active/swat", "swat_10_5_ido", ALConfig.boundedSteps);
			List<Double> idoReachProbs = idoReach.computeReachability();
			double idominfre = MetricComputing.calculateNonZeroMinFreq(idosample.getFrequencyMatrix());
			double idomse = MetricComputing.calculateMSE(estExactMatrix, idosample.getEstimatedTransitionMatrix());
			List<Double> idoDiff = ListUtil.listABSDiff(nonZeroSwatReachProbs, idoReachProbs);
			double idoMeanDiff = ListUtil.listMean(idoDiff);
			FileUtil.writeObject(Config.TMP_PATH + "/swat_ido_reach_probs", idoReachProbs);

			ALConfig.ido = false;

			Samples idosampleReach = IterSampling(frequencyMatrix, ALConfig.newSampleNumber, estimator, idosamplerReach, idoidgReach);
			Reachability rsReach = new Reachability(idosampleReach.getEstimatedTransitionMatrix(), validInitStates, validInitDist, targetStates,
					PlatformDependent.CAV_MODEL_ROOT + "/active/swat", "swat_10_5_rs", ALConfig.boundedSteps);
			List<Double> rsReachProbs = rsReach.computeReachability();
			FileUtil.writeObject(Config.TMP_PATH + "/swat_rs_reach_probs", rsReachProbs);
			double rsminfre = MetricComputing.calculateNonZeroMinFreq(idosampleReach.getFrequencyMatrix());
			double rsmse = MetricComputing.calculateMSE(estExactMatrix, idosampleReach.getEstimatedTransitionMatrix());
			List<Double> rsDiff = ListUtil.listABSDiff(nonZeroSwatReachProbs, rsReachProbs);
			double rsMeanDiff = ListUtil.listMean(rsDiff);

			System.out.println("ido min fre: " + idominfre + "\n");
			System.out.println("ido reach min fre: " + rsminfre + "\n");
			System.out.println("ido mse: " + idomse + "\n");
			System.out.println("ido reach mse: " + rsmse + "\n");
			System.out.println("ido reach: " + idoMeanDiff + "\n"); // only average on non-zero differences
			System.out.println("ido reach reach: " + rsMeanDiff + "\n");

//			FileUtil.appendStringToFile(Config.TMP_PATH + "/swat_logs", logInfo.toString());

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
