package io.github.wang_jingyi.ZiQian.active;

import java.util.Random;

public class SwatBridge {
	
	private SwatStatePool ssp;
	private SwatSensorAbstraction ssa;
	
	
	public SwatBridge(SwatStatePool ssp, SwatSensorAbstraction ssa) {
		this.ssp = ssp;
		this.ssa = ssa;
	}
	
	
	// give state index, generate a set of sensor values as input
	public double[] generateInput(int stateIndex){
		int sensorNum = ssa.getSensors().size();
		double[] input = new double[sensorNum];
		SwatState ss = ssp.getReSwatStateMap().get(stateIndex);
		for(int i=0; i<sensorNum; i++){
			int sensorIndex = ss.getSensorValues().get(i);
			Random rnd = new Random();
			Interval intv = ssa.getSensorDecodings().get(i).get(sensorIndex);
			double sv = intv.start + rnd.nextDouble() * (intv.end-intv.start);
			input[i] = sv;
		}
		return input;
	}
	
}
