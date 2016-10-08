package io.github.wang_jingyi.ZiQian.active;

import org.junit.Test;

public class SwatSensorAbstractionTest {
	
	@Test
	public void testSensorAbstraction(){
		SwatSensorAbstraction ssa = new SwatSensorAbstraction();
		ssa.addSensor("FIT501", new Interval(10, 100), 10);
		System.out.println(ssa.toString());
	}

}
