package io.github.wang_jingyi.ZiQian.active;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import io.github.wang_jingyi.ZiQian.utils.IntegerUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class InitialDistributionOptimizer {

	private double[][] origEstimation;
	private int nodeNumber;
	private Samples sample;


	public InitialDistributionOptimizer(Samples sample) {
		this.origEstimation = sample.getEstimatedTransitionMatrix().getData();
		this.nodeNumber = origEstimation.length;
		this.sample = sample;
	}

	public double[] calculateOptimalInitDistribution(List<Integer> validInitalStates) throws GRBException{
		RealMatrix A = accumulatedMatrix(sample.getPathLength());
		RealMatrix AT = A.transpose();

		int mini = sample.getMinStartVertex(); // get i: arg min mi
		double[] ATI = AT.getRow(mini); // the i-th row

		GRBEnv    env   = new GRBEnv("optimize.initial.distribution.log");
		GRBModel  model = new GRBModel(env);

		// Create variables
		List<GRBVar> vars = new ArrayList<>();
		for(int i=0; i<nodeNumber; i++){
			vars.add(model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "x"+i));
		}
		model.update();

		GRBLinExpr obj = new GRBLinExpr();
		GRBLinExpr cst = new GRBLinExpr();



		for(int i=0; i<nodeNumber; i++){
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
			if(!IntegerUtil.isInList(i, validInitalStates)){
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

		double[] optimalDistribution = new double[nodeNumber];
		for(int i=0; i<nodeNumber; i++){
			optimalDistribution[i] = vars.get(i).get(GRB.DoubleAttr.X);
//			System.out.println(vars.get(i).get(GRB.StringAttr.VarName) + ": " + vars.get(i).get(GRB.DoubleAttr.X));
		}

		//		System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

		// Dispose of model and environment
		model.dispose();
		env.dispose();

		return optimalDistribution;
	}

	// l is the length of the path samples, calculate accumulated matrix A
	private  RealMatrix accumulatedMatrix(int l){
		RealMatrix identityMatrix = MatrixUtils.createRealIdentityMatrix(nodeNumber); // I
		RealMatrix estimationMatrix = MatrixUtils.createRealMatrix(origEstimation); // P

		RealMatrix multipliedMatrix = MatrixUtils.createRealMatrix(origEstimation); // P
		RealMatrix accumulatedMatrix = identityMatrix.add(estimationMatrix);
		for(int i=2; i<=l-1; i++){
			multipliedMatrix = multipliedMatrix.multiply(estimationMatrix);
			accumulatedMatrix = accumulatedMatrix.add(multipliedMatrix); 
		}
		return accumulatedMatrix;
	}

}
