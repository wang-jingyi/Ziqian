package io.github.wang_jingyi.ZiQian.learn;

public class GoldenSectionSearch {
	
	protected double leftBound;
	protected double rightBound;
	
	
	protected double calNewLeft(double left, double right){
		return right - 0.618 * (right - left);
	}

	protected double calNewRight(double left, double right){
		return left + 0.618 * (right - left);
	}

}
