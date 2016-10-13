package io.github.wang_jingyi.ZiQian.active;

import java.util.Random;

public class SwatBridge {
	
	private SwatSensorAbstraction ssa;
	
	
	public SwatBridge(SwatSensorAbstraction ssa) {
		this.ssa = ssa;
	}
	
	// give state index, generate a set of sensor values as input
	public double[] generateInput(int stateIndex){
		
		int sensorNum = ssa.getSensors().size();
		double[] input = new double[sensorNum];
		
		SwatState ss = new SwatState(ssa.swatStateAbstractValue(stateIndex));
		for(int i=0; i<sensorNum; i++){
			int sensorIndex = ss.getSensorValues().get(i);
			Random rnd = new Random();
			Interval intv = ssa.getSensorDecodings().get(i).get(sensorIndex);
			if(intv==null){
				System.out.println("tag");
			}
			double sv = intv.start + rnd.nextDouble() * (intv.end-intv.start);
			input[i] = sv;
		}
		return input;
	}

	public SwatSensorAbstraction getSsa() {
		return ssa;
	}

	
}
