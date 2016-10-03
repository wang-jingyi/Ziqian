package io.github.wang_jingyi.ZiQian.active;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class SwatTrace {
	
	private List<SwatState> st;
	private String tracePath;
	
	public SwatTrace(String tracePath) {
		this.tracePath = tracePath;
	}

	// trace collection
	public void collectTraceFromPath(SwatSensorAbstraction ssa) throws FileNotFoundException{
		File file = new File(tracePath);
		Scanner sc = new Scanner(file);
		sc.nextLine();
		while(sc.hasNextLine()){
			double[] sensorValues = new double[ssa.getSensors().size()];
			String str = sc.nextLine();
			String[] ss = str.split(",");
			for(int i=0; i<sensorValues.length; i++){
				sensorValues[i] = Double.valueOf(ss[i]);
			}
			st.add(ssa.swatAbstraction(sensorValues));
		}
		sc.close();
	}
	
	public List<SwatState> getTrace() {
		return st;
	}
	

}
