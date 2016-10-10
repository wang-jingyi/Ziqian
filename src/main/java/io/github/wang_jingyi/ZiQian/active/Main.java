package io.github.wang_jingyi.ZiQian.active;

import gurobi.GRBException;
import io.github.wang_jingyi.ZiQian.utils.ListUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) throws GRBException, FileNotFoundException{
		//				double[][] matrix = new double[][]{{0.9, 0.1, 0, 0, 0, 0, 0},
		//													{0, 0, 0.5, 0.1, 0, 0, 0.4},
		//													{0, 0, 0, 0, 0.9, 0.1, 0},
		//													{0, 0, 0, 0, 0.8, 0.2, 0},
		//													{0.9, 0.1, 0, 0, 0, 0, 0},
		//													{0.5, 0.1, 0, 0, 0, 0, 0.4},
		//													{0, 0, 0, 0, 0, 0, 1}};
		//		RandomMarkovChain rmc = new RandomMarkovChain(16, 0.8, "test");

		int[] stateNumber = new int[]{8,16,32};
		int[] sampleSize = new int[]{5000,10000,50000,100000,200000};
		double density = 0.8;


		List<List<Double>> idoMSEResults = new ArrayList<List<Double>>();
		List<List<Double>> rsMSEResults = new ArrayList<List<Double>>();
		List<List<Double>> idoReachResults = new ArrayList<List<Double>>();
		List<List<Double>> rsReachResults = new ArrayList<List<Double>>();


		for(int sn : stateNumber){
			int boundedStep = 10;
			RandomMarkovChain rmc = new RandomMarkovChain(sn, density, "rmc_" + sn); 
			rmc.generateRMC();
			System.out.println("current random model: " + rmc.toString());
			System.out.println("current number of states: " + sn);

			List<Double> idomse = new ArrayList<Double>();
			List<Double> rsmse = new ArrayList<Double>();
			List<Double> idoreach = new ArrayList<Double>();
			List<Double> rsreach = new ArrayList<Double>();

			List<Integer> validInitStates = new ArrayList<>();
			List<Double> validInitDist = new ArrayList<Double>();

			for(int i=0; i<rmc.getNumOfState()/2; i++){
				validInitStates.add(i);
				validInitDist.add((double)1/rmc.getNumOfState());
			}

			rmc.setInitStates(validInitStates);
			rmc.getValidRMC();



			double[][] matrix = rmc.getTransitionMatrix();
			MarkovChain mc = new MarkovChain(ListUtil.TwoDDoubleArrayToList(matrix));
			int sampleLength = sn;

			RMCReachability rmcr = new RMCReachability(rmc,boundedStep);
			rmcr.computeRMCReachability();
			
			

			// define estimator, initial distribution getter
			Estimator estimator = new LaplaceEstimator();
			Sampler sampler = new MarkovChainSampler(new MarkovChain(ListUtil.TwoDDoubleArrayToList(rmc.getTransitionMatrix())));
			InitialDistGetter idoidg = new InitialDistributionOptimizer(mc.getNodeNumber(), validInitStates, sn);
			InitialDistGetter uniformidg = new UniformInitialDistribution(validInitStates);

			for(int numSample : sampleSize){

				System.out.println("current sample size: " + numSample);
				Samples idoSample = IterSampling(mc, validInitStates, sampleLength, numSample, estimator, sampler, idoidg);
				Samples randomSample = IterSampling(mc, validInitStates, sampleLength, numSample, estimator, sampler, uniformidg);

				idomse.add(MetricComputing.calculateMSE(ListUtil.TwoDDoublelistToArray(mc.getTransitionMatrix()), 
						ListUtil.TwoDDoublelistToArray(idoSample.getEstimatedTransitionMatrix())));
				rsmse.add(MetricComputing.calculateMSE(ListUtil.TwoDDoublelistToArray(mc.getTransitionMatrix()),
						ListUtil.TwoDDoublelistToArray(randomSample.getEstimatedTransitionMatrix())));

				List<Double> idoReachProbs = rmcr.computeEstReachability(ListUtil.TwoDDoublelistToArray(idoSample.getEstimatedTransitionMatrix()));
				List<Double> randomReachProbs = rmcr.computeEstReachability(ListUtil.TwoDDoublelistToArray(randomSample.getEstimatedTransitionMatrix()));

				List<Double> idoDiff = ListUtil.listABSDiff(rmcr.getReachProbs(), idoReachProbs);
				List<Double> randomDiff = ListUtil.listABSDiff(rmcr.getReachProbs(), randomReachProbs);

				idoreach.add(ListUtil.listMean(idoDiff));
				rsreach.add(ListUtil.listMean(randomDiff));
			}
			idoMSEResults.add(idomse);
			rsMSEResults.add(rsmse);
			idoReachResults.add(idoreach);
			rsReachResults.add(rsreach);

			System.out.println("idoMSE current progress: " + idoMSEResults);
			System.out.println("rsMSE current progress: " + rsMSEResults);
			System.out.println("idoReach current progress: " + idoReachResults);
			System.out.println("rsReach current progress: " + rsReachResults);
			
		}

	}

	public static Samples IterSampling(MarkovChain mc, List<Integer> validInitStates, int sampleLength, int numSample, Estimator estimator, 
			Sampler sampler, InitialDistGetter idg) throws GRBException{
		Samples sample = new Samples(mc.getNodeNumber(), sampleLength, estimator, sampler, idg);
		for(int i=0; i<numSample; i++){
			sample.newSample();
		}
		return sample;

	}
}
