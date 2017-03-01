package io.github.wang_jingyi.ZiQian.exceptions;

public class SimulationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9194282253709907089L;
	
	public SimulationException() {
		messaging();
	}
	
	private void messaging(){
		System.out.println("Simulation error, no output files...");
	}

}
