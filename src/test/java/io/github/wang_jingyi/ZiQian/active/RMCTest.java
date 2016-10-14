package io.github.wang_jingyi.ZiQian.active;

import org.junit.Test;

public class RMCTest {

	@Test
	public void testRMCRareState(){
		RandomMarkovChain rmc = new RandomMarkovChain(4, 0.8, "test", true, 1E-4);
		rmc.generateRMC();
		System.out.println(rmc);
	}
	
	
}
