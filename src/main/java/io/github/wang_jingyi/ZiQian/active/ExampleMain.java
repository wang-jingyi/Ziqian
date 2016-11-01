package io.github.wang_jingyi.ZiQian.active;

import gurobi.GRBException;
import io.github.wang_jingyi.ZiQian.run.PlatformDependent;
import io.github.wang_jingyi.ZiQian.utils.ListUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.MatrixUtils;

public class ExampleMain {

	public static void main(String[] args) throws DimensionMismatchException, NullArgumentException, NoDataException, FileNotFoundException{

		//		double[][] matrix = new double[][]{{0.9, 0.1, 0, 0, 0, 0, 0},
		//				{0, 0, 0.5, 0.1, 0, 0, 0.4},
		//				{0, 0, 0, 0, 0.9, 0.1, 0},
		//				{0, 0, 0, 0, 0.8, 0.2, 0},
		//				{0.9, 0.1, 0, 0, 0, 0, 0},
		//				{0.5, 0.1, 0, 0, 0, 0, 0.4},
		//				{0, 0, 0, 0, 0, 0, 1}};

		//		double[][] matrix = new double[][]{
		//				{0.9,0.09,0.01,0},
		//				{0,0,0.05,0.95},
		//				{0,0,0,1},
		//				{0,0,0,1}
		//		};
		
		
		// hollow matrix
//		double[][] matrix = new double[][]{
//				{0,0.22,0.33,0.45},
//				{0.38,0,0.06,0.56},
//				{0.4,0.13,0,0.47},
//				{0.42,0.2,0.38,0}
//		};
		
		// queuing model
		double[][] matrix = new double[][]{
				{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0.53 , 0 , 0.47 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0},
				{0 , 0.65 , 0 , 0.35 , 0 , 0 , 0 , 0 , 0 , 0 , 0},
				{0 , 0 , 0.45 , 0 , 0.55 , 0 , 0 , 0 , 0 , 0 , 0},
				{0 , 0 , 0 , 0.30 , 0 , 0.70 , 0 , 0 , 0 , 0 , 0},
				{0 , 0 , 0 , 0 , 0.62 , 0 , 0.38 , 0 , 0 , 0 , 0},
				{0 , 0 , 0 , 0 , 0 , 0.68 , 0 , 0.32 , 0 , 0 , 0},
				{0 , 0 , 0 , 0 , 0 , 0 , 0.64 , 0 , 0.36 , 0 , 0},
				{0 , 0 , 0 , 0 , 0 , 0 , 0 , 0.52 , 0 , 0.48 , 0},
				{0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0.61 , 0 , 0.39},
				{0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0}
		};

		
		ALConfig.stateNumber = 11;
		ALConfig.pathLength = 11;
		String modelName = "queue_model";
		int itPathNumber = 5000;


		// add initial states
		List<Integer> validInitStates = new ArrayList<>();
		List<Double> validInitDist = new ArrayList<Double>();

		for(int i=0; i<ALConfig.stateNumber/2; i++){
			validInitStates.add(i);
			validInitDist.add((double)1/ALConfig.stateNumber);
		}
		
		List<Integer> targetStates = new ArrayList<Integer>();
		for(int i=ALConfig.stateNumber/2; i<ALConfig.stateNumber; i++){
			targetStates.add(i);
		}
		
		MarkovChain mc = new MarkovChain(MatrixUtils.createRealMatrix(matrix), validInitStates, validInitDist);
		MCInitialTrain it = new MCInitialTrain(mc, ALConfig.pathLength, itPathNumber);

		// define estimator, initial distribution getter
		Estimator estimator = new LaplaceEstimator();
		Sampler sampler = new MarkovChainSampler(mc);
		InitialDistGetter idoidg = new InitialDistributionOptimizer(mc.getNodeNumber(), validInitStates, ALConfig.stateNumber);
		InitialDistGetter uniformidg = new UniformInitialDistribution(validInitStates);

		int[] sampleSize = new int[]{  5000,
//								1000,2000,3000,4000,5000,
//								6000,7000,8000,9000,
				10000
				,50000,100000,200000
		};

		List<Double> idomse = new ArrayList<Double>();
		List<Double> rsmse = new ArrayList<Double>();
		List<Double> idoreach = new ArrayList<Double>();
		List<Double> rsreach = new ArrayList<Double>();
		
		Reachability reach = new Reachability(MatrixUtils.createRealMatrix(matrix), validInitStates, targetStates,
				PlatformDependent.MODEL_ROOT + "/active/" + modelName, modelName , ALConfig.stateNumber);
		List<Double> actualReach = reach.computeReachability();

		for(int ss : sampleSize){
			ALConfig.newSampleNumber = ss;

			double im = 0.0;
			double rm = 0.0;
			double ir = 0.0;
			double rr = 0.0;

			for(int i=0; i<10; i++){
				try {
					Samples idosample = Main.IterSampling(mc, it.getInitialFrequencyMatrix(), validInitStates, ALConfig.pathLength, 
							ALConfig.newSampleNumber, estimator, sampler, idoidg);
					Samples rdsample = Main.IterSampling(mc, it.getInitialFrequencyMatrix(), validInitStates, ALConfig.pathLength, 
							ALConfig.newSampleNumber, estimator, sampler, uniformidg);
					im += MetricComputing.calculateMSE(MatrixUtils.createRealMatrix(matrix), idosample.getEstimatedTransitionMatrix());
					rm += MetricComputing.calculateMSE(MatrixUtils.createRealMatrix(matrix), rdsample.getEstimatedTransitionMatrix());
					
					Reachability ircompute = new Reachability(idosample.getEstimatedTransitionMatrix(), validInitStates, targetStates,
							PlatformDependent.MODEL_ROOT + "/active/"+ modelName, modelName + "_ido", ALConfig.stateNumber);
					List<Double> idoReach = ircompute.computeReachability();
					Reachability rscompute = new Reachability(rdsample.getEstimatedTransitionMatrix(), validInitStates, targetStates,
							PlatformDependent.MODEL_ROOT + "/active/" + modelName, modelName + "_rs", ALConfig.stateNumber);
					List<Double> rsReach= rscompute.computeReachability();
					
					System.out.println("actual reach: " + actualReach);
					System.out.println("ido reach: " + idoReach);
					System.out.println("rs reach: " + rsReach);
					
					List<Double> idoDiff = ListUtil.listABSDiff(actualReach, idoReach);
					List<Double> randomDiff = ListUtil.listABSDiff(actualReach, rsReach);
					
					ir += ListUtil.listMean(idoDiff);
					rr += ListUtil.listMean(randomDiff);

				} catch (GRBException e) {
					e.printStackTrace();
				}
			}

			idomse.add(im/10);
			rsmse.add(rm/10);
			idoreach.add(ir/10);
			rsreach.add(rr/10);
		}
		
		System.out.println("ido mse: " + idomse);
		System.out.println("rs mse: " + rsmse);
		System.out.println("ido reach: " + idoreach);
		System.out.println("rs reach: " + rsreach);

	}

}
