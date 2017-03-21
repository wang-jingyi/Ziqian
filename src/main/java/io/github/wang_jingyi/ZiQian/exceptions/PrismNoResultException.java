package io.github.wang_jingyi.ZiQian.exceptions;

public class PrismNoResultException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2986174436221026599L;
	
	public PrismNoResultException() {
		messaging();
	}
	
	private void messaging(){
		System.out.println("- PRISM is not generating correct results");
	}
}
