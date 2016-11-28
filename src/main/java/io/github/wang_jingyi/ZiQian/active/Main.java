package io.github.wang_jingyi.ZiQian.active;

import gurobi.GRBException;
import io.github.wang_jingyi.ZiQian.run.PlatformDependent;
import io.github.wang_jingyi.ZiQian.utils.ListUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

public class Main {

	public static void main(String[] args) throws GRBException, FileNotFoundException{
		//						double[][] matrix = new double[][]{{0.9, 0.1, 0, 0, 0, 0, 0},
		//															{0, 0, 0.5, 0.1, 0, 0, 0.4},
		//															{0, 0, 0, 0, 0.9, 0.1, 0},
		//															{0, 0, 0, 0, 0.8, 0.2, 0},
		//															{0.9, 0.1, 0, 0, 0, 0, 0},
		//															{0.5, 0.1, 0, 0, 0, 0, 0.4},
		//															{0, 0, 0, 0, 0, 0, 1}};
		//				RandomMarkovChain rmc = new RandomMarkovChain(16, 0.8, "test");

		int[] stateNumber = new int[]{ 
//				48,
				8,
				16,
				32,
//				48
		};

		int[] sampleSize = new int[]{  
//				5000,
//				1000,2000,3000,4000,5000,
//				6000,7000,8000,9000,
//				10000,
//				20000, 30000, 40000,
//				,
				50000,
				100000
//				,200000
		};
		double density = 0.5;
		int repeatTime = 2;
		int initialTrainPathNumber = 5000;
		
		
		List<List<Double>> idoMFResults = new ArrayList<List<Double>>();
		List<List<Double>> idoMFResultsr = new ArrayList<List<Double>>();
		List<List<Double>> rsMFResults = new ArrayList<List<Double>>();
		List<List<Double>> idoTFResults = new ArrayList<List<Double>>();
		List<List<Double>> idoTFResultsr = new ArrayList<List<Double>>();
		List<List<Double>> rsTFResults = new ArrayList<List<Double>>();
		List<List<Double>> idoMSEResults = new ArrayList<List<Double>>();
		List<List<Double>> idoMSEResultsr = new ArrayList<List<Double>>();
		List<List<Double>> rsMSEResults = new ArrayList<List<Double>>();
		List<List<Double>> idoReachResults = new ArrayList<List<Double>>();
		List<List<Double>> idoReachResultsr = new ArrayList<List<Double>>();
		List<List<Double>> rsReachResults = new ArrayList<List<Double>>();


		for(int sn : stateNumber){
			
			ALConfig.stateNumber = sn;
			ALConfig.pathLength = sn/2;
			ALConfig.boundedSteps = ALConfig.pathLength;

			List<Double> idotf = new ArrayList<Double>();
			List<Double> idotfr = new ArrayList<Double>();
			List<Double> rstf = new ArrayList<Double>();
			
			List<Double> idomf = new ArrayList<Double>();
			List<Double> idomfr = new ArrayList<Double>();
			List<Double> rsmf = new ArrayList<Double>();
			
			List<Double> idomse = new ArrayList<Double>();
			List<Double> idomser = new ArrayList<Double>();
			List<Double> rsmse = new ArrayList<Double>();
			
			List<Double> idoreach = new ArrayList<Double>();
			List<Double> idoreachr = new ArrayList<Double>();
			List<Double> rsreach = new ArrayList<Double>();
			for(int numSample : sampleSize){
				
				double imse = 0, imser = 0, rmse = 0, ireach = 0, ireachr = 0, rreach = 0;
				double idoTargetFrequency = 0, idoTargetFrequencyReach = 0, rsTargetFrequency = 0; 
				double idoMinFrequency = 0, idoReachMinFrequency = 0, rsMinFrequency = 0;
				
				for(int time=0; time<repeatTime; time++){
					
					System.out.println("---current number of states: " + sn);
					System.out.println("---model count in the set: " + (time+1));
					
					RandomMarkovChain rmc = new RandomMarkovChain(sn, density, "rmc_test_" + sn); 
					rmc.generateRMC();
					System.out.println("-current random model: " +  rmc.toString());
					
					// set initial states
					List<Integer> validInitStates = new ArrayList<>();
					List<Double> validInitDist = new ArrayList<Double>();
					for(int i=0; i<rmc.getNumOfState()/2; i++){ // add first half set as initial states
						validInitStates.add(i);
						validInitDist.add((double)1/rmc.getNumOfState());
					}
					rmc.setInitStates(validInitStates);
					rmc.getValidRMC();
					
					List<Integer> targetStates = new ArrayList<Integer>();
					for(int i=rmc.getNumOfState()/2 ; i<rmc.getNumOfState(); i++){
						targetStates.add(i);
					}
					
					RealMatrix matrix = rmc.getTransitionMatrix();
					MarkovChain mc = new MarkovChain(matrix, validInitStates, validInitDist);

					Reachability rmcr = new Reachability(rmc.getTransitionMatrix(), validInitStates, validInitDist, targetStates,
							PlatformDependent.MODEL_ROOT+"/active/rmc", rmc.getRmcName(), ALConfig.boundedSteps);
					List<Double> actualReach = rmcr.computeReachability();

					// define estimator, initial distribution getter
					Estimator estimator = new LaplaceEstimator();
					
					System.out.println("generating ");
					MCInitialTrain mit = new MCInitialTrain(mc, ALConfig.pathLength, initialTrainPathNumber);
					
					Sampler sampler = new MarkovChainSampler(mc);
					InitialDistGetter idoidg = new InitialDistributionOptimizer(mc.getNodeNumber(), validInitStates, ALConfig.pathLength);
					InitialDistGetter idoidgReach = new ReachabilityOptimizer(mc.getNodeNumber(), validInitStates, targetStates, ALConfig.pathLength);
					InitialDistGetter uniformidg = new OriginalInitialDistribution(validInitStates,validInitDist);
					

					System.out.println("current sample size: " + numSample);
					
					Samples idoSample = IterSampling(mc, mit.getInitialFrequencyMatrix(), validInitStates, ALConfig.pathLength, numSample, estimator, sampler, idoidg);
					Samples idoSampleReach = IterSampling(mc, mit.getInitialFrequencyMatrix(), validInitStates, ALConfig.pathLength, numSample, estimator, sampler, idoidgReach);
					Samples randomSample = IterSampling(mc, mit.getInitialFrequencyMatrix(), validInitStates, ALConfig.pathLength, numSample, estimator, sampler, uniformidg);
					
					
					idoMinFrequency += MetricComputing.calculateMinFreq(idoSample.getFrequencyMatrix());
					idoReachMinFrequency += MetricComputing.calculateMinFreq(idoSampleReach.getFrequencyMatrix());
					rsMinFrequency += MetricComputing.calculateMinFreq(randomSample.getFrequencyMatrix());
					
					idoTargetFrequency += ListUtil.listSum(MetricComputing.calculateTargetStateFreq(idoSample.getFrequencyMatrix(), targetStates));
					idoTargetFrequencyReach += ListUtil.listSum(MetricComputing.calculateTargetStateFreq(idoSampleReach.getFrequencyMatrix(), targetStates));
					rsTargetFrequency += ListUtil.listSum(MetricComputing.calculateTargetStateFreq(randomSample.getFrequencyMatrix(), targetStates));
					
					imse += MetricComputing.calculateMSE(mc.getTransitionMatrix(), idoSample.getEstimatedTransitionMatrix());
					imser += MetricComputing.calculateMSE(mc.getTransitionMatrix(), idoSampleReach.getEstimatedTransitionMatrix());
					rmse += MetricComputing.calculateMSE(mc.getTransitionMatrix(),randomSample.getEstimatedTransitionMatrix());
					
					Reachability idormcr = new Reachability(idoSample.getEstimatedTransitionMatrix(), validInitStates, validInitDist, targetStates,
							PlatformDependent.MODEL_ROOT + "/active/rmc", rmc.getRmcName()+"_ido", ALConfig.boundedSteps);
					List<Double> idoReachProbs = idormcr.computeReachability();
					Reachability idormcrr = new Reachability(idoSampleReach.getEstimatedTransitionMatrix(), validInitStates, validInitDist, targetStates,
							PlatformDependent.MODEL_ROOT + "/active/rmc", rmc.getRmcName()+"_ido_reach", ALConfig.boundedSteps);
					List<Double> idoReachProbsr = idormcrr.computeReachability();
					Reachability rsrmcr = new Reachability(randomSample.getEstimatedTransitionMatrix(), validInitStates, validInitDist, targetStates,
							PlatformDependent.MODEL_ROOT + "/active/rmc", rmc.getRmcName()+"_rs", ALConfig.boundedSteps);
					List<Double> randomReachProbs = rsrmcr.computeReachability();
					

					List<Double> idoDiff = ListUtil.listABSDiff(actualReach, idoReachProbs);
					List<Double> idoDiffReach = ListUtil.listABSDiff(actualReach, idoReachProbsr);
					List<Double> randomDiff = ListUtil.listABSDiff(actualReach, randomReachProbs);
					
					ireach += ListUtil.listMean(idoDiff);
					ireachr += ListUtil.listMean(idoDiffReach);
					rreach += ListUtil.listMean(randomDiff);
				}
				idomf.add(idoMinFrequency/repeatTime);
				idomfr.add(idoReachMinFrequency/repeatTime);
				rsmf.add(rsMinFrequency/repeatTime);
				idotf.add(idoTargetFrequency/repeatTime);
				idotfr.add(idoTargetFrequencyReach/repeatTime);
				rstf.add(rsTargetFrequency/repeatTime);
				idomse.add(imse/repeatTime);
				idomser.add(imser/repeatTime);
				rsmse.add(rmse/repeatTime);
				idoreach.add(ireach/repeatTime);
				idoreachr.add(ireachr/repeatTime);
				rsreach.add(rreach/repeatTime);
			}
			
			idoMFResults.add(idomf);
			idoMFResultsr.add(idomfr);
			rsMFResults.add(rsmf);
			idoTFResults.add(idotf);
			idoTFResultsr.add(idotfr);
			rsTFResults.add(rstf);
			idoMSEResults.add(idomse);
			idoMSEResultsr.add(idomser);
			rsMSEResults.add(rsmse);
			idoReachResults.add(idoreach);
			idoReachResultsr.add(idoreachr);
			rsReachResults.add(rsreach);
			
			System.out.println("ido min frequency progress: " + idoMFResults);
			System.out.println("ido reach min frequency progress: " + idoMFResultsr);
			System.out.println("rs min frequency progress: " + rsMFResults);
			System.out.println("ido targets frequency progress: " + idoTFResults);
			System.out.println("ido reach targets frequency progress: " + idoTFResultsr);
			System.out.println("rs targets frequency progress: " + rsTFResults);
			System.out.println("idoMSE current progress: " + idoMSEResults);
			System.out.println("idoMSE reach current progress: " + idoMSEResultsr);
			System.out.println("rsMSE current progress: " + rsMSEResults);
			System.out.println("idoReach current progress: " + idoReachResults);
			System.out.println("idoReach reach current progress: " + idoReachResultsr);
			System.out.println("rsReach current progress: " + rsReachResults);

		}

	}

	public static Samples IterSampling(MarkovChain mc, RealMatrix currentFM, List<Integer> validInitStates, int pathLength, int numSample, Estimator estimator, 
			Sampler sampler, InitialDistGetter idg) throws GRBException{
		Samples sample = new Samples(pathLength, currentFM, estimator, sampler, idg);
		for(int i=0; i<numSample; i++){
			sample.newSample();
		}
		return sample;

	}
}
