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
				//				16,
				//				32,
				//				48
		};

		int[] sampleSize = new int[]{  
//								100,200
				//				1000,2000,3000,4000,5000,
				//				6000,7000,8000,9000,
				//				10000,
				//				20000, 30000, 40000,
				//				,
				50000,
				100000,150000
				,200000
		};
		double density = 0.5;
		int repeatTime = 500;
		int initialTrainPathNumber = 5000;
		double reachThres = 0.05;


		List<List<Double>> ido_mvs = new ArrayList<List<Double>>();
		List<List<Double>> ido_mses = new ArrayList<List<Double>>();
		List<List<Double>> ido_rrds = new ArrayList<List<Double>>();
		
		List<List<Double>> rs_mvs = new ArrayList<List<Double>>();
		List<List<Double>> rs_mses = new ArrayList<List<Double>>();
		List<List<Double>> rs_rrds = new ArrayList<List<Double>>();


		for(int sn : stateNumber){

			ALConfig.stateNumber = sn;
			ALConfig.pathLength = sn;
			ALConfig.boundedSteps = ALConfig.pathLength;
			
			List<Double> ido_mv = new ArrayList<Double>(); // each element is a result for one sample size
			List<Double> rs_mv = new ArrayList<Double>();
			List<Double> ido_mse = new ArrayList<Double>();
			List<Double> rs_mse = new ArrayList<Double>();
			List<Double> ido_rrd = new ArrayList<Double>();
			List<Double> rs_rrd = new ArrayList<Double>();
			
			for(int z=0; z<sampleSize.length; z++){
				ido_mv.add(0.0);
				rs_mv.add(0.0);
				ido_mse.add(0.0);
				rs_mse.add(0.0);
				ido_rrd.add(0.0);
				rs_rrd.add(0.0);
			}
			
			int repetition = 0;
			for(int time=0; time<repeatTime; time++){

				System.out.println("---current number of states: " + sn);
				System.out.println("---model count in the set: " + (time+1));

				RandomMarkovChain rmc = new RandomMarkovChain(sn, density, "rmc_"+sn+"_"+time); 
				rmc.generateRMC();
				System.out.println("-current random model: " +  rmc.toString());

				// set initial states
				List<Integer> validInitStates = new ArrayList<>();
				List<Double> validInitDist = new ArrayList<Double>();
				for(int i=0; i<rmc.getNumOfState()/2; i++){ // add first half set as initial states
					validInitStates.add(i);
				}
				int isn = validInitStates.size();
				for(int i=0; i<isn; i++){
					validInitDist.add((double)1/isn);
				}
				rmc.setInitStates(validInitStates);

				List<Integer> targetStates = new ArrayList<Integer>();
				for(int i=rmc.getNumOfState()/2 ; i<rmc.getNumOfState(); i++){
					targetStates.add(i);
				}

				RealMatrix matrix = rmc.getTransitionMatrix();
				MarkovChain mc = new MarkovChain(matrix, validInitStates, validInitDist);

				Reachability rmcr = new Reachability(rmc.getTransitionMatrix(), validInitStates, validInitDist, targetStates,
						PlatformDependent.MODEL_ROOT+"/active/rmc/rmc"+sn, rmc.getRmcName(), ALConfig.boundedSteps);
				List<Double> actualReach = rmcr.computeReachability();
				boolean allLarge = true;
				for(double d : actualReach){
					if(d<reachThres){
						allLarge = false;
						repetition++;
						break;
					}
				}
				if(allLarge){
					continue;
				}
				
				System.out.println("generating initial training samples...");
				MCInitialTrain mit = new MCInitialTrain(mc, ALConfig.pathLength, initialTrainPathNumber);
				
				// define estimator, initial distribution getter
				Estimator estimator = new LaplaceEstimator();
				Sampler sampler = new MarkovChainSampler(mc);
				InitialDistGetter idoidg = new InitialDistributionOptimizer(mc.getNodeNumber(), validInitStates, ALConfig.pathLength);
				InitialDistGetter uniformidg = new OriginalInitialDistribution(validInitStates,validInitDist);
				
				for(int kk=0; kk<sampleSize.length; kk++){
					int numSample = sampleSize[kk];
					System.out.println("current sample size: " + numSample);

					Samples idoSample = IterSampling(mc, mit.getInitialFrequencyMatrix().copy(), validInitStates, ALConfig.pathLength, numSample, estimator, sampler, idoidg);
					Samples randomSample = IterSampling(mc, mit.getInitialFrequencyMatrix().copy(), validInitStates, ALConfig.pathLength, numSample, estimator, sampler, uniformidg);

					double current_ido_mv = MetricComputing.calculateMinFreq(idoSample.getFrequencyMatrix());
					double current_rs_mv = MetricComputing.calculateMinFreq(randomSample.getFrequencyMatrix());

					double current_ido_mse = MetricComputing.calculateMSE(mc.getTransitionMatrix(), idoSample.getEstimatedTransitionMatrix());
					double current_rs_mse = MetricComputing.calculateMSE(mc.getTransitionMatrix(),randomSample.getEstimatedTransitionMatrix());

					Reachability idormcr = new Reachability(idoSample.getEstimatedTransitionMatrix(), validInitStates, validInitDist, targetStates,
							PlatformDependent.MODEL_ROOT + "/active/rmc/rmc"+sn, rmc.getRmcName()+"_ido_"+numSample+"_"+time, ALConfig.boundedSteps);
					List<Double> idoReachProbs = idormcr.computeReachability();
					List<Double> idoDiff = ListUtil.listABSPercThresDiff(actualReach, idoReachProbs,reachThres);
					
					Reachability rsrmcr = new Reachability(randomSample.getEstimatedTransitionMatrix(), validInitStates, validInitDist, targetStates,
							PlatformDependent.MODEL_ROOT + "/active/rmc/rmc"+sn, rmc.getRmcName()+"_rs_"+numSample+"_"+time, ALConfig.boundedSteps);
					List<Double> randomReachProbs = rsrmcr.computeReachability();
					List<Double> randomDiff = ListUtil.listABSPercThresDiff(actualReach, randomReachProbs,reachThres);
					
					System.out.println("small reachability states number: " + idoDiff.size());

					double current_ido_rrd = ListUtil.listMean(idoDiff);
					double current_rs_rrd = ListUtil.listMean(randomDiff);
					
					double tmp = 0;
					tmp = ido_mv.get(kk); ido_mv.set(kk, tmp+current_ido_mv);
					tmp = rs_mv.get(kk); rs_mv.set(kk, tmp+current_rs_mv);
					tmp = ido_mse.get(kk); ido_mse.set(kk, tmp+current_ido_mse);
					tmp = rs_mse.get(kk); rs_mse.set(kk, tmp+current_rs_mse);
					tmp = ido_rrd.get(kk); ido_rrd.set(kk, tmp+current_ido_rrd);
					tmp = rs_rrd.get(kk); rs_rrd.set(kk, tmp+current_rs_rrd);
				}
				System.out.println("current repetition: " + repetition);
				if(repetition==20){break;}
			}
			
			System.out.println("total repetitions of state number " + sn + " : "  + repetition);
			
			for(int cc=0; cc<ido_mv.size(); cc++){ // take the average of all the repititions
				double tmp = 0;
				tmp = ido_mv.get(cc); ido_mv.set(cc, tmp/repetition);
				tmp = rs_mv.get(cc); rs_mv.set(cc, tmp/repetition);
				tmp = ido_mse.get(cc); ido_mse.set(cc, tmp/repetition);
				tmp = rs_mse.get(cc); rs_mse.set(cc, tmp/repetition);
				tmp = ido_rrd.get(cc); ido_rrd.set(cc, tmp/repetition);
				tmp = rs_rrd.get(cc); rs_rrd.set(cc, tmp/repetition);
			}
			ido_mvs.add(ido_mv);
			rs_mvs.add(rs_mv);
			ido_mses.add(ido_mse);
			rs_mses.add(rs_mse);
			ido_rrds.add(ido_rrd);
			rs_rrds.add(rs_rrd);
			
		}
		System.out.println("ido mv current progress: " + ido_mvs);
		System.out.println("rs mv current progress: " + rs_mvs);
		System.out.println("ido mse current progress: " + ido_mses);
		System.out.println("rs mse current progress: " + rs_mses);
		System.out.println("ido rrd current progress: " + ido_rrds);
		System.out.println("rs rrd current progress: " + rs_rrds);

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
