package io.github.wang_jingyi.ZiQian.exceptions;

public class UnsupportedLearningTypeException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8049867664257275303L;
	
	public UnsupportedLearningTypeException() {
		messaging();
	}

	private void messaging(){
		System.out.println("- The set learning algorithm is not supported, supported learning algorithms are AA, GA");
	}
	
}
