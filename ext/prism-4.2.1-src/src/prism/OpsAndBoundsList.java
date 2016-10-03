//==============================================================================
//	
//	Copyright (c) 2002-
//	Authors:
//	* Vojtech Forejt <vojtech.forejt@cs.ox.ac.uk> (University of Oxford)
//	
//------------------------------------------------------------------------------
//	
//	This file is part of PRISM.
//	
//	PRISM is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//	
//	PRISM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with PRISM; if not, write to the Free Software Foundation,
//	Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//	
//==============================================================================

package prism;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * This class keeps lists of operators and bounds used in multi-objective
 * verification. 
 *
 * The instance keeps an ordered instance of (operator,bound) values.
 * These are currently held in two separate lists internally. A tuple
 * is added using {@link add(Operator,bound)} method, and retrieved using
 * {@link getOperator(int)} and {@link getBound(double} methods.
 * 
 * The class also provides methods for accessing i-th elements in the
 * subsequence containing only the tuples in which operator is a probabilistic
 * operator, and in the subsequence containing only the tuples in which operator
 * is a reward operator.
 */
public class OpsAndBoundsList
{
 
	/**
	 * Used when printing info to user.
	 */
	private BitSet probNegated;
	
	protected List<Operator> relOps, relOpsProb, relOpsReward;
	protected List<Double> bounds,  boundsProb, boundsReward;
	protected List<Integer> stepBounds,  stepBoundsProb, stepBoundsReward;
	
	/**
	 * The default constructor which allocates the lists with size 1.
	 */
	public OpsAndBoundsList()
	{
		this(1);
	}
	
	/**
	 * Creates an instance of the class in which the "big" lists
	 * are allocated with size numTargets.
	 * @param numTargets The expected number of elements that would be added to the list. 
	 */
	public OpsAndBoundsList(int numTargets)
	{
		probNegated = new BitSet();
		relOps = new ArrayList<Operator>(numTargets);
		bounds = new ArrayList<Double>(numTargets);
		stepBounds = new ArrayList<Integer>(numTargets);
		relOpsProb = new ArrayList<Operator>();
		boundsProb = new ArrayList<Double>();
		stepBoundsProb = new ArrayList<Integer>(numTargets);
		relOpsReward = new ArrayList<Operator>();
		boundsReward = new ArrayList<Double>();
		stepBoundsReward = new ArrayList<Integer>(numTargets);
	}
	
	/**
	 * Adds a new tuple (op, bound) to the list. 
	 * @param op
	 * @param quantityBound
	 * @param stepBound
	 */
	public void add(Operator op, double quantityBound, int stepBound)
	{
		relOps.add(op);
		bounds.add(quantityBound);
		stepBounds.add(stepBound);
		
		switch (op)
		{
			case P_MAX:
			case P_MIN:
			case P_GE:
			case P_LE:
				relOpsProb.add(op);
				boundsProb.add(quantityBound);
				stepBoundsProb.add(stepBound);
				break;
			case R_MAX:
			case R_MIN:
			case R_GE:
			case R_LE:
				relOpsReward.add(op);
				boundsReward.add(quantityBound);
				stepBoundsReward.add(stepBound);
				break;
			default:
				throw new UnsupportedOperationException("Don't know how to add" +
						" operator " + op + ", the handling code does not exist.");
		}
	}
	
	/**
	 * Returns the operator at i-th position.
	 */
	public Operator getOperator(int i)
	{
		return relOps.get(i);
	}
	
	/**
	 * Returns the bound on quantity at i-th position.
	 */
	public double getBound(int i)
	{
		return bounds.get(i);
	}
	
	/**
	 * Returns the number-of-steps step bound at i-th position.
	 */
	public int getStepBound(int i)
	{
		return stepBounds.get(i);
	}
	
	/**
	 * Returns the operator at i-th position in the subsequence containing only probabilistic
	 * operators.
	 */
	public Operator getProbOperator(int i)
	{
		return relOpsProb.get(i);
	}

	/**
	 * Returns the probability bound at i-th position in the subsequence containing only probabilistic
	 * operators.
	 */
	public double getProbBound(int i)
	{
		return boundsProb.get(i);
	}

	/**
	 * Returns the number-of-steps bound at i-th position in the subsequence containing only probabilistic
	 * operators.
	 */
	public int getProbStepBound(int i)
	{
		return stepBoundsProb.get(i);
	}
	
	/**
	 * Returns the operator at i-th position in the subsequence containing only reward
	 * operators.
	 */
	public Operator getRewardOperator(int i)
	{
		return relOpsReward.get(i);
	}
	
	/**
	 * Returns the bound on reward at i-th position in the subsequence containing only reward
	 * operators.
	 */
	public double getRewardBound(int i)
	{
		return boundsReward.get(i);
	}
	
	/**
	 * Returns the number-of-steps bound at i-th position in the subsequence containing only reward
	 * operators.
	 */
	public int getRewardStepBound(int i)
	{
		return stepBoundsReward.get(i);
	}
	
	/**
	 * Returns true iff the i-th objective is probability objective.
	 */
	public boolean isProbabilityObjective(int i)
	{
		switch (relOps.get(i))
		{
			case P_MAX:
			case P_MIN:
			case P_GE:
			case P_LE:
				return true;
			default:
				return false;
		}
	}

	/**
	 * True if the ith probabilistic objective is negation of what the user required
	 * (i.e. formula is negated and we use >= instead <= or max instead of min).
	 * Used to determine what values to display to the user.
	 * @param i
	 * @return
	 */
	public boolean isProbNegated(int i)
	{
		return this.probNegated.get(i);
	}
	
	/**
	 *  Replace min by max and <= by >= in prob.
	 */
	//TODO: why not do prob also in main list?
	public void makeAllProbUp()
	{
		for (int i = 0; i < relOpsProb.size(); i++) {
			if (relOpsProb.get(i) == Operator.P_MIN) {
				relOpsProb.remove(i);
				relOpsProb.add(i, Operator.P_MAX);
			    probNegated.set(i);
			} else if (relOpsProb.get(i) == Operator.P_LE) {
				relOpsProb.remove(i);
				relOpsProb.add(i, Operator.P_GE);
			    probNegated.set(i);
			}
		}
	}
	
	/**
	 * Returns number of reward operators added so far
	 */
	public int rewardSize()
	{
		return this.relOpsReward.size();
	}
	
	/**
	 * Returns number of probabilistic operators added so far.
	 * @return
	 */
	public int probSize()
	{
		return this.relOpsProb.size();
	}
	
	/**
	 * Returns true if the list contains the operator op
	 */
	public boolean contains(Operator op)
	{
		return relOps.contains(op);
	}
	
	/**
	 * returns the number of min/max operators.
	 * @return
	 */
	public int numberOfNumerical()
	{
		int num = 0;
		for(Operator op : relOps) {
			if (op == Operator.P_MAX
				|| op == Operator.P_MIN
				|| op == Operator.R_MAX
				|| op == Operator.R_MIN) {
				num++;
			}
		}
		return num;
	}
	
	@Override
	public String toString() {
		return "Quantity bounds: " + this.bounds + "; Step bounds: " + this.stepBounds + "; Operators" + this.relOps;
	}
}
