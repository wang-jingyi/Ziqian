package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.run.GlobalVars;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SwatTrace {
	
	private List<SwatState> st;
	private String tracePath;
	
	public SwatTrace(String tracePath) {
		this.tracePath = tracePath;
		this.st = new ArrayList<SwatState>();
	}

	// trace collection
	public void collectTraceFromPath(SwatSensorAbstraction ssa) throws FileNotFoundException{
		File file = new File(tracePath);
		try{
			Scanner sc = new Scanner(file);
			sc.nextLine();
			while(sc.hasNextLine()){
				double[] sensorValues = new double[ssa.getSensors().size()];
				String str = sc.nextLine();
				String[] ss = str.split(" ");
				if(ss.length!=sensorValues.length){
					continue;
				}
				for(int i=0; i<sensorValues.length; i++){
					sensorValues[i] = Double.valueOf(ss[i]);
					if(sensorValues[i]>GlobalVars.maxSensorValues[i]){
						GlobalVars.maxSensorValues[i] = sensorValues[i];
					}
					
					if(sensorValues[i]<GlobalVars.minSensorValues[i]){
						GlobalVars.minSensorValues[i] = sensorValues[i];
					}
				}
				st.add(ssa.swatAbstraction(sensorValues));
			}
			sc.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public List<SwatState> getTrace() {
		return st;
	}
	
	@Override
	public String toString() {
		return "SwatTrace [st=" + st + "]";
	}

}
