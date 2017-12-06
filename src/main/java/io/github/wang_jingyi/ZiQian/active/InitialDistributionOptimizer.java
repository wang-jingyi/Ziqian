package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import io.github.wang_jingyi.ZiQian.utils.IntegerUtil;

public class InitialDistributionOptimizer implements InitialDistGetter{

	private int nodeNumber;
	private List<Integer> validInitialStates;
	private int pathLength;

	public InitialDistributionOptimizer(int nodeNumber, List<Integer> validInitialStates, int pathLength) {
		this.nodeNumber = nodeNumber;
		this.validInitialStates = validInitialStates;
		this.pathLength = pathLength;
	}

	// l is the length of the path samples, calculate accumulated matrix A
	public static RealMatrix accumulatedMatrix(int l, RealMatrix origEstimation){
		int nodeNumber = origEstimation.getRowDimension();
		RealMatrix identityMatrix = ALConfig.sparse? new OpenMapRealMatrix(nodeNumber, nodeNumber) :
			MatrixUtils.createRealIdentityMatrix(origEstimation.getRowDimension()); // 

		if(ALConfig.sparse){
			assert origEstimation instanceof OpenMapRealMatrix : "too large matrix, use sparse matrix";
		}

		RealMatrix estimationMatrix = origEstimation.copy(); // 
		RealMatrix multipliedMatrix = origEstimation.copy(); //
		RealMatrix accumulatedMatrix = identityMatrix.add(estimationMatrix);
		for(int i=2; i<=l-1; i++){
			multipliedMatrix = multipliedMatrix.multiply(estimationMatrix);
			accumulatedMatrix = accumulatedMatrix.add(multipliedMatrix); 
		}
		return accumulatedMatrix;
	}

	@Override
	public double[] getInitialDistribution(RealMatrix frequencyMatrix, 
			RealMatrix origEstimation){

		RealMatrix A = accumulatedMatrix(pathLength, origEstimation);
		RealMatrix AT = A.transpose();

		int mini = MetricComputing.calculateMinFreqState(frequencyMatrix); // get i: arg min mi
		double[] ATI = AT.getRow(mini); // the i-th row

		double[] optimalDistribution = new double[nodeNumber];

//		LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(ATI, 0);
//		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2*nodeNumber];
//
//		double[] g0 = new double[nodeNumber];
//		double[] g1 = new double[nodeNumber];
//		for(int i=0; i<nodeNumber; i++){
//			if(i!=0){
//				g0[i-1]=0;
//				g1[i-1]=0;
//			}
//			g0[i] = -1;
//			g1[i] = 1;
//			inequalities[i] = new LinearMultivariateRealFunction(g0, 0); // xi >= 0
//			inequalities[i+nodeNumber] = new LinearMultivariateRealFunction(g1, -1); // xi <=1
//		}
//
//		double[][] a = new double[1][nodeNumber];
//		for(int i=0; i<nodeNumber; i++){
//			a[0][i]=1;
//		}
//		double[] b = new double[]{1};
//
//		//optimization problem
//		OptimizationRequest or = new OptimizationRequest();
//		or.setF0(objectiveFunction);
//		or.setFi(inequalities);
//		or.setA(a);
//		or.setB(b);
//		//or.setInitialPoint(new double[] {0.0, 0.0});//initial feasible point, not mandatory
//		or.setToleranceFeas(1.E-2);
//		or.setTolerance(1.E-2);
//
//		//optimization
//		JOptimizer opt = new JOptimizer();
//		opt.setOptimizationRequest(or);
//		try {
//			opt.optimize();
//		} catch (Exception e) {
//			System.out.println("optimization problem");
//			e.printStackTrace();
//		}
//		double[] sol = opt.getOptimizationResponse().getSolution();
//		for(double d : sol){
//			System.out.println("solution: " + d);
//		}


				GRBEnv env;

				try {
					env = new GRBEnv("optimize.initial.distribution.log");
					GRBModel  model = new GRBModel(env);
		
					// Create variables
					List<GRBVar> vars = new ArrayList<>();
					for(int i=0; i<nodeNumber; i++){
						vars.add(model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "x"+i));
					}
					model.update();
		
					GRBLinExpr obj = new GRBLinExpr();
					GRBLinExpr cst = new GRBLinExpr();
		
					for(int i : validInitialStates){ // only optimize over initial states
						obj.addTerm(ATI[i], vars.get(i));
						cst.addTerm(1.0, vars.get(i));
					}
		
					// set objective
					model.setObjective(obj, GRB.MAXIMIZE);
		
					//		System.out.println(model.getObjective());
		
		
					// add constraints
					model.addConstr(cst, GRB.EQUAL, 1.0, "valid distribution");
		
					List<GRBLinExpr> zeros = new ArrayList<>();
					for(int i=0; i<nodeNumber; i++){
						if(!IntegerUtil.isInList(i, validInitialStates)){
							GRBLinExpr newcst = new GRBLinExpr();
							newcst.addTerm(1.0, vars.get(i));
							zeros.add(newcst);
						}
					}
		
					for(GRBLinExpr le : zeros){
						model.addConstr(le, GRB.EQUAL, 0.0, "non initial states");
					}
		
					// Optimize model
					model.optimize();
		
					for(int i=0; i<nodeNumber; i++){
						optimalDistribution[i] = vars.get(i).get(GRB.DoubleAttr.X);
		//				System.out.println(vars.get(i).get(GRB.StringAttr.VarName) + ": " + vars.get(i).get(GRB.DoubleAttr.X));
					}
		
					//		System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));
		
					// Dispose of model and environment
					model.dispose();
					env.dispose();
				} catch (GRBException e) {
					e.printStackTrace();
				}
		return optimalDistribution;
	}
	
	public List<Integer> getValidInitialStates() {
		return validInitialStates;
	}

	@Override
	public void setValidInitialStates(List<Integer> validInitialStates) {
		this.validInitialStates = validInitialStates;
	}

}
