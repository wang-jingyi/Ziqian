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
		int learn_counter = 0;
		System.out.println("------ Learn from single trace, search for the best parameter ------");
		while(LRdistance > 0.05 ){ // @@@@@@@@@@@@@@@@ to check
			leftLA.setEpsilon(left);
			System.out.println("--- Learn from parameter " + learn_counter);
			learn_counter++;
			leftLA.learn(data);
			leftScore = leftLA.getSelectionCriterion();
			
			rightLA.setEpsilon(right);
			System.out.println("--- Learn from parameter " + learn_counter);
			learn_counter++;
			rightLA.learn(data);
			rightScore = rightLA.getSelectionCriterion();

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
			LRdistance = Math.abs(leftScore-rightScore)/Math.max(leftScore, rightScore);
		}
		
		System.out.println("- Highest BIC score: " + highestSelectionCriterion);
		System.out.println("- Best epsilon: " + epsilon);
		System.out.println("- Total number of iteration: " + numOfIteration);
		if(leftWins){
			return leftLA;
		}
		return rightLA;
	}

}
