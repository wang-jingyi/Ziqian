package io.github.wang_jingyi.ZiQian.active;

import gurobi.GRBException;
import io.github.wang_jingyi.ZiQian.main.PlatformDependent;

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
		int itPathNumber = 5000;

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
		
		List<Integer> targetStates = new ArrayList<Integer>();
		for(int i=ALConfig.stateNumber/2; i<ALConfig.stateNumber; i++){
			targetStates.add(i);
		}


		RealMatrix matrix = rmc.getTransitionMatrix();
		MarkovChain mc = new MarkovChain(matrix, validInitStates, validInitDist);
		
		MCInitialTrain it = new MCInitialTrain(mc, ALConfig.pathLength, itPathNumber);
		
		// define estimator, initial distribution getter
		Estimator estimator = new EFEstimator();
		ActiveSampler sampler = new MarkovChainSampler(mc);
		InitialDistGetter idoidg = new InitialDistributionOptimizer(mc.getNodeNumber(), validInitStates, ALConfig.stateNumber);
		InitialDistGetter uniformidg = new OriginalInitialDistribution(validInitStates,validInitDist);

		try {
			Samples idoSample = Main.IterSampling(mc, it.getInitialFrequencyMatrix(), validInitStates, ALConfig.pathLength, ALConfig.newSampleNumber, estimator, sampler, idoidg);
			Samples randomSample = Main.IterSampling(mc, it.getInitialFrequencyMatrix(), validInitStates, ALConfig.pathLength, ALConfig.newSampleNumber, estimator, sampler, uniformidg);
			
			Reachability rmcr = new Reachability(rmc.getTransitionMatrix(), validInitStates, validInitDist, targetStates,
					PlatformDependent.CAV_MODEL_ROOT+"/active/rmc",
					rmc.getRmcName(), boundedStep);
			double realRareReach = rmcr.computeReachability(ALConfig.stateNumber);
			
			Reachability idormcr = new Reachability(idoSample.getEstimatedTransitionMatrix(), validInitStates, validInitDist, targetStates,
					PlatformDependent.CAV_MODEL_ROOT + "/active/rmc", rmc.getRmcName()+"_ido", boundedStep);
			double idoestRareReach = idormcr.computeReachability(ALConfig.stateNumber);
			Reachability rsrmcr = new Reachability(randomSample.getEstimatedTransitionMatrix(), validInitStates, validInitDist, targetStates,
					PlatformDependent.CAV_MODEL_ROOT + "/active/rmc", rmc.getRmcName()+"_rs", boundedStep);
			double rsestRareReach = rsrmcr.computeReachability(ALConfig.stateNumber);
			
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
