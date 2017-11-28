package io.github.wang_jingyi.ZiQian.active;

import gurobi.GRBException;
import io.github.wang_jingyi.ZiQian.main.PlatformDependent;
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

				double[][] matrix = new double[][]{
						{0.999,0.0009,0.0001,0},
						{0,0,0.05,0.95},
						{0,0,0,1},
						{0,0,0,1}
				};
		
		
//		// hollow matrix
//		double[][] matrix = new double[][]{
//				{0,0.992,0.003,0.005},
//				{0.98,0,0.01,0.01},
//				{0.4,0.13,0,0.47},
//				{0.42,0.2,0.38,0}
//		};
		
		// queuing model
//		double[][] matrix = new double[][]{
//				{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//				{0.53 , 0 , 0.47 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0},
//				{0 , 0.65 , 0 , 0.35 , 0 , 0 , 0 , 0 , 0 , 0 , 0},
//				{0 , 0 , 0.45 , 0 , 0.55 , 0 , 0 , 0 , 0 , 0 , 0},
//				{0 , 0 , 0 , 0.30 , 0 , 0.70 , 0 , 0 , 0 , 0 , 0},
//				{0 , 0 , 0 , 0 , 0.62 , 0 , 0.38 , 0 , 0 , 0 , 0},
//				{0 , 0 , 0 , 0 , 0 , 0.68 , 0 , 0.32 , 0 , 0 , 0},
//				{0 , 0 , 0 , 0 , 0 , 0 , 0.64 , 0 , 0.36 , 0 , 0},
//				{0 , 0 , 0 , 0 , 0 , 0 , 0 , 0.52 , 0 , 0.48 , 0},
//				{0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0.61 , 0 , 0.39},
//				{0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0}
//		};

		
		ALConfig.stateNumber = 4;
		ALConfig.pathLength = ALConfig.stateNumber;
		String modelName = "toy_example";
//		String modelName = "queue_model";
//		String modelName = "hollow_matrix";
		
		int itPathNumber = 5000;
		int repeatTime = 20;


		// add initial states	
		List<Integer> validInitStates = new ArrayList<>();
		List<Double> validInitDist = new ArrayList<Double>();
		List<Integer> targetStates = new ArrayList<Integer>();
		
		// example initial states setting
		validInitStates.add(0);
		validInitDist.add(0.99);
		validInitStates.add(1);
		validInitDist.add(0.01);
		targetStates.add(3);
		
		// queue model and hollow matrix
//		for(int i=0; i<ALConfig.stateNumber/2; i++){
//			validInitStates.add(i);
//		}
//		int isn = validInitStates.size();
//		for(int i=0; i<ALConfig.stateNumber/2; i++){
//			validInitDist.add((double)1/isn);	
//		}
		
//		targetStates.add(8);
//		targetStates.add(9);
//		targetStates.add(10);
		
//		for(int i=ALConfig.stateNumber/2; i<ALConfig.stateNumber; i++){
//			targetStates.add(i);
//		}
		
		MarkovChain mc = new MarkovChain(MatrixUtils.createRealMatrix(matrix), validInitStates, validInitDist);
		MCInitialTrain it = new MCInitialTrain(mc, ALConfig.pathLength, itPathNumber);

		// define estimator, initial distribution getter
//		Estimator estimator = new LaplaceEstimator();
		Estimator estimator = new EFEstimator();
		ActiveSampler sampler = new MarkovChainSampler(mc);
		InitialDistGetter idoidg = new InitialDistributionOptimizer(mc.getNodeNumber(), validInitStates, ALConfig.stateNumber);
		InitialDistGetter origidg = new OriginalInitialDistribution(validInitStates, validInitDist);

		int[] sampleSize = new int[]{
				
//				5000, 10000,
				20000, 
//				1000, 1500, 2000
				
//				1000,2000,3000,4000,5000,
//				100000,
//				15000,20000,
//								1000,2000,3000,4000,5000,
//								6000,7000,8000,9000,
//				25000,
//				30000,35000,40000,45000,
//				50000,
//				100000, 150000, 200000
		};
		
		List<Double> idominfre = new ArrayList<Double>();
		List<Double> rsminfre = new ArrayList<Double>();
		List<Double> idofrevar = new ArrayList<Double>();
		List<Double> rsfrevar = new ArrayList<Double>();
		List<Double> idomse = new ArrayList<Double>();
		List<Double> rsmse = new ArrayList<Double>();
		List<Double> idoreach = new ArrayList<>();
		List<Double> rsreach = new ArrayList<>();
		
		Reachability reach = new Reachability(MatrixUtils.createRealMatrix(matrix), validInitStates, validInitDist, targetStates,
				PlatformDependent.CAV_MODEL_ROOT + "/active/" + modelName, modelName , ALConfig.stateNumber);
		List<Double> actualReach = reach.computeReachability();

		for(int ss : sampleSize){
			ALConfig.newSampleNumber = ss;
			
			double imf = 0.0;
			double rmf = 0.0;
			double ifv = 0.0;
			double rfv = 0.0;
			double im = 0.0;
			double rm = 0.0;
//			List<Double> irdd = new ArrayList<Double>();
//			List<Double> rrdd = new ArrayList<Double>();
			double ir = 0.0;
			double rr = 0.0;

			for(int i=0; i<repeatTime; i++){
				try {
					Samples idosample = Main.IterSampling(mc, it.getInitialFrequencyMatrix().copy(), validInitStates, ALConfig.pathLength, 
							ALConfig.newSampleNumber, estimator, sampler, idoidg);
					Samples rdsample = Main.IterSampling(mc, it.getInitialFrequencyMatrix().copy(), validInitStates, ALConfig.pathLength, 
							ALConfig.newSampleNumber, estimator, sampler, origidg);
					imf += MetricComputing.calculateMinFreq(idosample.getFrequencyMatrix());
					rmf += MetricComputing.calculateMinFreq(rdsample.getFrequencyMatrix());
					ifv += MetricComputing.calculateVariance(idosample.getFrequencyMatrix());
					rfv += MetricComputing.calculateVariance(rdsample.getFrequencyMatrix());
					
					im += MetricComputing.calculateMSE(MatrixUtils.createRealMatrix(matrix), idosample.getEstimatedTransitionMatrix());
					rm += MetricComputing.calculateMSE(MatrixUtils.createRealMatrix(matrix), rdsample.getEstimatedTransitionMatrix());
					
					Reachability ircompute = new Reachability(idosample.getEstimatedTransitionMatrix(), validInitStates, validInitDist, targetStates,
							PlatformDependent.CAV_MODEL_ROOT + "/active/"+ modelName, modelName + "_ido_" + ss + "_" + i, ALConfig.stateNumber);
					List<Double> idoReach = ircompute.computeReachability();
					Reachability rscompute = new Reachability(rdsample.getEstimatedTransitionMatrix(), validInitStates, validInitDist, targetStates,
							PlatformDependent.CAV_MODEL_ROOT + "/active/" + modelName, modelName + "_rs_" + ss + "_" + i, ALConfig.stateNumber);
					List<Double> rsReach= rscompute.computeReachability();
					
					System.out.println("actual reach: " + actualReach);
					System.out.println("ido reach: " + idoReach);
					System.out.println("rs reach: " + rsReach);
					
					List<Double> idoDiff = ListUtil.listABSPercThresDiff(actualReach, idoReach,0.1);
					List<Double> randomDiff = ListUtil.listABSPercThresDiff(actualReach, rsReach,0.1);
					
//					for(int ri=0; ri<targetStates.size(); ri++){
//						int r = targetStates.get(ri);
//						if(repeatTime==0){
//							irdd.add(idoDiff.get(r)/actualReach.get(r));
//						}
//						else{
//							double or = irdd.get(ri);
//							or += idoDiff.get(r)/actualReach.get(r);
//							irdd.set(ri, or);
//						}
//					}
//					
//					for(int ri=0; ri<targetStates.size(); ri++){
//						int r = targetStates.get(ri);
//						if(rrdd.isEmpty()){
//							rrdd.add(randomDiff.get(r)/actualReach.get(r));
//						}
//						else{
//							double or = rrdd.get(ri);
//							or += randomDiff.get(r)/actualReach.get(r);
//							rrdd.set(ri, or);
//						}
//					}
					
					ir += ListUtil.listMean(idoDiff);
					rr += ListUtil.listMean(randomDiff);

				} catch (GRBException e) {
					e.printStackTrace();
				}
			}
			idominfre.add(imf/repeatTime);
			rsminfre.add(rmf/repeatTime);
			idofrevar.add(ifv/repeatTime);
			rsfrevar.add(rfv/repeatTime);
			idomse.add(im/repeatTime);
			rsmse.add(rm/repeatTime);
			idoreach.add(ir/repeatTime);
			rsreach.add(rr/repeatTime);
			
			
//			for(int ct=0; ct<irdd.size(); ct++){ // normalize by the repeat time
//				double ord = irdd.get(ct);
//				double ird = rrdd.get(ct);
//				irdd.set(ct, ord/repeatTime);
//				rrdd.set(ct, ird/repeatTime);
//			}
//			idoreach.add(irdd);
//			rsreach.add(rrdd);
		}
		System.out.println("ido min fre: " + idominfre);
		System.out.println("rs min fre: " + rsminfre);
		System.out.println("ido fre var: " + idofrevar);
		System.out.println("rs fre var: " + rsfrevar);
		System.out.println("ido mse: " + idomse);
		System.out.println("rs mse: " + rsmse);
		System.out.println("ido reach percentage distance: " + idoreach);
		System.out.println("rs reach: percentage distance: " + rsreach);

	}

}
