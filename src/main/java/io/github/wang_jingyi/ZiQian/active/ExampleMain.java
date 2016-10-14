package io.github.wang_jingyi.ZiQian.active;

import gurobi.GRBException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;

public class ExampleMain {

	public static void main(String[] args){
		
		double[][] matrix = new double[][]{{0.9, 0.1, 0, 0, 0, 0, 0},
				{0, 0, 0.5, 0.1, 0, 0, 0.4},
				{0, 0, 0, 0, 0.9, 0.1, 0},
				{0, 0, 0, 0, 0.8, 0.2, 0},
				{0.9, 0.1, 0, 0, 0, 0, 0},
				{0.5, 0.1, 0, 0, 0, 0, 0.4},
				{0, 0, 0, 0, 0, 0, 1}};

		MarkovChain mc = new MarkovChain(MatrixUtils.createRealMatrix(matrix));
		ALConfig.stateNumber = 7;
		ALConfig.pathLength = 7;
		ALConfig.newSampleNumber = 1000;
		
		
		// add initial states
		List<Integer> validInitStates = new ArrayList<>();
		List<Double> validInitDist = new ArrayList<Double>();

		validInitStates.add(0);
		validInitDist.add(1.0);

		// define estimator, initial distribution getter
		Estimator estimator = new LaplaceEstimator();
		Sampler sampler = new MarkovChainSampler(new MarkovChain(mc.getTransitionMatrix()));
		InitialDistGetter idoidg = new InitialDistributionOptimizer(mc.getNodeNumber(), validInitStates, ALConfig.stateNumber);
		InitialDistGetter uniformidg = new UniformInitialDistribution(validInitStates);

		try {
			Samples idosample = Main.IterSampling(mc, validInitStates, ALConfig.pathLength, 
					ALConfig.newSampleNumber, estimator, sampler, idoidg);
			Samples rdsample = Main.IterSampling(mc, validInitStates, ALConfig.pathLength, 
					ALConfig.newSampleNumber, estimator, sampler, uniformidg);
			System.out.println("ido mse : " + MetricComputing.calculateMSE(MatrixUtils.createRealMatrix(matrix), idosample.getEstimatedTransitionMatrix()));
			System.out.println("random mse : " + MetricComputing.calculateMSE(MatrixUtils.createRealMatrix(matrix), rdsample.getEstimatedTransitionMatrix()));
			
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

}
