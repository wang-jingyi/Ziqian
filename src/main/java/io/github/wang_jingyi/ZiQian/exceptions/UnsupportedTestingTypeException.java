package io.github.wang_jingyi.ZiQian.exceptions;

public class UnsupportedTestingTypeException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3786963841955748500L;
	
	public UnsupportedTestingTypeException() {
		messaging();
	}

	private void messaging() {
		System.out.println("- The set learning algorithm is not supported, supported testing approaches are sst, sprt");
	}

}
