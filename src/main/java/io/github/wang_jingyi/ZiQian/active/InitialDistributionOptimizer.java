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
	private  RealMatrix accumulatedMatrix(int l, RealMatrix origEstimation){
		
		RealMatrix identityMatrix = MatrixUtils.createRealIdentityMatrix(nodeNumber); // I
		RealMatrix estimationMatrix = origEstimation.copy(); // P

		RealMatrix multipliedMatrix = origEstimation.copy(); // P
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

		GRBEnv env;
		double[] optimalDistribution = new double[nodeNumber];
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

	@Override
	public void setValidInitialStates(List<Integer> validInitialStates) {
		this.validInitialStates = validInitialStates;
	}

}
