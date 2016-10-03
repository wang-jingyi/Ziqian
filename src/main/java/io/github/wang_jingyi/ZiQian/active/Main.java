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
		
		int[] stateNumber = new int[]{8,16,32,64,128};
		int[] sampleSize = new int[]{5000,10000,50000,100000,200000};
		double density = 0.8;
		String estimator = "lp";
		
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
			MarkovChain mc = new MarkovChain(matrix);
			int sampleLength = sn;
			
			RMCReachability rmcr = new RMCReachability(rmc,boundedStep);
			rmcr.computeRMCReachability();
			
			for(int numSample : sampleSize){
				
				System.out.println("current sample size: " + numSample);
				
				Samples idoSample = IDOSampling(mc, validInitStates, sampleLength, numSample, estimator);
				Samples randomSample = randomSampling(mc, validInitStates, validInitDist, sampleLength, numSample, estimator);
				
				idomse.add(idoSample.getMSE());
				rsmse.add(randomSample.getMSE());
				
				List<Double> idoReachProbs = rmcr.computeEstReachability(idoSample.getEstimatedTransitionMatrix().getData());
				List<Double> randomReachProbs = rmcr.computeEstReachability(randomSample.getEstimatedTransitionMatrix().getData());
				
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
	
	public static Samples IDOSampling(MarkovChain mc, List<Integer> validInitStates, int sampleLength, int numSample, String estimator) throws GRBException{
		Samples sample = new Samples(mc, sampleLength, estimator);
		for(int i=0; i<numSample; i++){
			InitialDistributionOptimizer ido = new InitialDistributionOptimizer(sample);
			double[] opmu = ido.calculateOptimalInitDistribution(validInitStates);
			sample.newSample(sample.calculateSampleStartPoint(opmu));
		}
		return sample;
		
	}
	
	public static Samples randomSampling(MarkovChain mc, List<Integer> validInitStates, List<Double> validInitDist, int sampleLength, int numSample, String estimator){
		Samples sample = new Samples(mc, sampleLength, estimator);
		for(int i=0; i<numSample; i++){
			sample.newSample(sample.calculateSampleStartPoint(ListUtil.listToArray(validInitDist)));
		}
		return sample;
	}
}
