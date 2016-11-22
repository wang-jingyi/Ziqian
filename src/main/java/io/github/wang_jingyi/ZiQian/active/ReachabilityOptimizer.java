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

import org.apache.commons.math3.linear.RealMatrix;

public class ReachabilityOptimizer implements InitialDistGetter{

	private int nodeNumber;
	private List<Integer> validInitialStates;
	private List<Integer> targetStates;
	private int pathLength;

	public ReachabilityOptimizer(int nodeNumber, List<Integer> validInitialStates, List<Integer> targetStates, int pathLength) {
		this.nodeNumber = nodeNumber;
		this.validInitialStates = validInitialStates;
		this.targetStates = targetStates;
		this.pathLength = pathLength;
	}


	@Override
	public double[] getInitialDistribution(RealMatrix frequencyMatrix,
			RealMatrix origEstimation) {
		
		RealMatrix tm = origEstimation.copy();
		tm = adaptEstimation(tm);
		RealMatrix A = InitialDistributionOptimizer.accumulatedMatrix(pathLength, tm);
		RealMatrix AT = A.transpose();
		
//		HashSet<Integer> ops = MetricComputing.oneStepToTargetStates(frequencyMatrix, targetStates);
//		int min = MetricComputing.calculateTargetStateMinFreq(frequencyMatrix, targetStates);
		double[] ATI = 
//				AT.getRow(min);
				new double[frequencyMatrix.getRowDimension()];
		
		
		for(int t : targetStates){
//		for(int t : ops){	
			for(int i=0; i<ATI.length; i++){
				ATI[i] += AT.getRow(t)[i]; // accumulate over all target states
			}
		}

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

			for(int i : validInitialStates){ // only optimize over initial states
				obj.addTerm(ATI[i], vars.get(i));
				cst.addTerm(1.0, vars.get(i));
			}

			// set objective
			model.setObjective(obj, GRB.MAXIMIZE);
			System.out.println(model.getObjective());

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

//			System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

			// Dispose of model and environment
			model.dispose();
			env.dispose();
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return optimalDistribution;
	}

	private RealMatrix adaptEstimation(RealMatrix tm) { // adapt to calculate reachability
		for(int ts : targetStates){
			for(int i=0; i<tm.getColumnDimension(); i++){
				if(i!=ts){
					tm.setEntry(ts, i, 0);
					continue;
				}
				tm.setEntry(ts, i, 1);
				
			}
		}
		return tm;
	}


	@Override
	public void setValidInitialStates(List<Integer> validInitialStates) {
		this.validInitialStates = validInitialStates;
	}




}
