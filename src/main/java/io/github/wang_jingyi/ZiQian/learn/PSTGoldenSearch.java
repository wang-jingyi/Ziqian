package io.github.wang_jingyi.ZiQian.learn;

import io.github.wang_jingyi.ZiQian.Input;


/*
 * PSA learning model selection using golden search
 * Input: left and right bound for search
 * */

public class PSTGoldenSearch extends GoldenSectionSearch implements ModelSelection {

	private double epsilon;
	private double highestSelectionCriterion;

	public PSTGoldenSearch(double start, double end) {
		this.leftBound = start;
		this.rightBound = end;
	}

	public LearningDTMC selectCriterion(Input data) {

		double left = calNewLeft(leftBound, rightBound);
		double right = calNewRight(leftBound, rightBound);
		double leftScore = Double.MAX_VALUE;
		double rightScore = 0;

		epsilon = 0;
		highestSelectionCriterion = 0;
		int numOfIteration = 0;
		LearnPST leftLA = new LearnPST();
		LearnPST rightLA = new LearnPST();
		boolean leftWins = false;
		double LRdistance = Double.MAX_VALUE;
		
		while(LRdistance > 0.05 ){ // @@@@@@@@@@@@@@@@ to check
//			System.out.println("Iteration : " + (numOfIteration+1));
//			System.out.println("-----------Left epsilon-----------");
//			System.out.println("Current left epsilon: " + left);

			
			leftLA.setEpsilon(left);
			leftLA.learn(data);
			System.out.println("Number of state of left PSA: " + leftLA.getNumOfStates());
			leftScore = leftLA.getSelectionCriterion();
			System.out.println("Left bic score: " + leftScore);
//	
			
			rightLA.setEpsilon(right);
			rightLA.learn(data);
			System.out.println("Number of state of right PSA: " + rightLA.getNumOfStates());
			rightScore = rightLA.getSelectionCriterion();
			System.out.println("Right bic score: " + rightScore);

			if(leftScore<rightScore){
				leftBound = left;
				epsilon = right;
				highestSelectionCriterion = rightScore;
			}
			else{
				leftWins = true;
				rightBound = right;
				epsilon = left;
				highestSelectionCriterion = leftScore;
			}
			left = calNewLeft(leftBound, rightBound);
			right = calNewRight(leftBound, rightBound);
			numOfIteration ++;
//			System.out.println("========================================");
			LRdistance = Math.abs(leftScore-rightScore)/Math.max(leftScore, rightScore);
		}
		
//		System.out.println("Distance between left and right selection score in percentage: " + LRdistance);
		System.out.println("Epsilon with highest BIC score: " + epsilon);
		System.out.println("Highest BIC score: " + highestSelectionCriterion);
		System.out.println("Total number of iteration: " + numOfIteration);
//		System.out.println("========================================");
		if(leftWins){
			return leftLA;
		}
		return rightLA;
	}

}
