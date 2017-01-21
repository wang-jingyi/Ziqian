package io.github.wang_jingyi.ZiQian.learn;

import io.github.wang_jingyi.ZiQian.Input;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AAlergia extends GoldenSectionSearch implements ModelSelection {

	
	private double epsilon;
	private double highestSelectionCriterion;

	public AAlergia(double start, double end) {
		this.leftBound = start;
		this.rightBound = end;
	}

	@Override
	public LearningDTMC selectCriterion(Input data) throws FileNotFoundException, ClassNotFoundException, IOException {
		
//		System.out.println("Selecting the best parameter for learning...");

		double left = calNewLeft(leftBound, rightBound);
		double right = calNewRight(leftBound, rightBound);
		double leftScore = Double.MAX_VALUE;
		double rightScore = 0;

		epsilon = 0;
		highestSelectionCriterion = 0;
		AAlergiamain leftLA = new AAlergiamain();
		AAlergiamain rightLA = new AAlergiamain();
		boolean leftWins = false;
		double LRdistance = Double.MAX_VALUE;
//		int iteration = 1;
		
		while(LRdistance > 0.001){ // distance percentage is less than 1 percent
			
//			System.out.println("---Iteration : " + iteration);
			
//			System.out.println("--Left parameter : ");
//			System.out.println("Parameter alpha for learning: " + left);
			leftLA.setAlpha(left);
			leftLA.learn(data);
			leftScore = leftLA.getSelectionCriterion();
//			System.out.println("bic score: " + leftScore);
			
//			System.out.println("--Right parameter : ");
//			System.out.println("Parameter alpha for learning: " + right);
			rightLA.setAlpha(right);
			rightLA.learn(data);
			rightScore = rightLA.getSelectionCriterion();
//			System.out.println("bic score: " + rightScore);

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
			LRdistance = Math.abs(leftScore-rightScore)/Math.max(leftScore, rightScore); // difference in percentage
//			iteration++;
		}
		
		if(leftWins){
			return leftLA;
		}
		return rightLA;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public double getHighestSelectionCriterion() {
		return highestSelectionCriterion;
	}
	

}
