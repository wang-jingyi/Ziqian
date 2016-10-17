package io.github.wang_jingyi.ZiQian.active;

import gurobi.GRBException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

public class RMCRareReachMain {

	public static void main(String[] args) {

		ALConfig.stateNumber = 32;
		ALConfig.pathLength = ALConfig.stateNumber;
		ALConfig.newSampleNumber = 200000;
		int boundedStep = ALConfig.pathLength;

		RandomMarkovChain rmc = new RandomMarkovChain(ALConfig.stateNumber,
				0.8, "rmc_reach_" + ALConfig.stateNumber, true, 1E-4);
		rmc.generateRMC();

		List<Integer> validInitStates = new ArrayList<>();
		List<Double> validInitDist = new ArrayList<Double>();

		for(int i=0; i<rmc.getNumOfState()/2; i++){ // add first half set as initial states
			validInitStates.add(i);
			validInitDist.add((double)1/rmc.getNumOfState());
		}

		rmc.setInitStates(validInitStates);
		rmc.getValidRMC();



		RealMatrix matrix = rmc.getTransitionMatrix();
		MarkovChain mc = new MarkovChain(matrix);

		// define estimator, initial distribution getter
		Estimator estimator = new EmpiricalFrequencyEstimator();
		Sampler sampler = new MarkovChainSampler(new MarkovChain(rmc.getTransitionMatrix()));
		InitialDistGetter idoidg = new InitialDistributionOptimizer(mc.getNodeNumber(), validInitStates, ALConfig.stateNumber);
		InitialDistGetter uniformidg = new UniformInitialDistribution(validInitStates);

		try {
			Samples idoSample = Main.IterSampling(mc, validInitStates, ALConfig.pathLength, ALConfig.newSampleNumber, estimator, sampler, idoidg);
			Samples randomSample = Main.IterSampling(mc, validInitStates, ALConfig.pathLength, ALConfig.newSampleNumber, estimator, sampler, uniformidg);
			RMCReachability rmcr = new RMCReachability(rmc,boundedStep);
			double realRareReach = rmcr.computeRMCReachability(ALConfig.stateNumber);
			double idoestRareReach = rmcr.computeEstReachability(idoSample.getEstimatedTransitionMatrix(), "ido", ALConfig.stateNumber);
			double rsestRareReach = rmcr.computeEstReachability(randomSample.getEstimatedTransitionMatrix(), "rs", ALConfig.stateNumber);
			
			System.out.println("real rare state reachability: " + realRareReach);
			System.out.println("ido rare state reachability: " + idoestRareReach);
			System.out.println("rs rare state reachability: " + rsestRareReach);
			
		} catch (GRBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
