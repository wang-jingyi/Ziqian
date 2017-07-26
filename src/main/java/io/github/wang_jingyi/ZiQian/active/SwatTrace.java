package io.github.wang_jingyi.ZiQian.active;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.github.wang_jingyi.ZiQian.main.GlobalConfigs;

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
			String sensors = sc.nextLine();
			String[] ss = sensors.split(" ");
			
			
			if(!ALConfig.sensorIndexAdded){
				for(String s : ssa.getSensors()){
					for(int i=0; i<ss.length; i++){
						if(ss[i].equals(s)){
							ALConfig.sensorIndex.add(i);
						}
					}
				}
				ALConfig.sensorIndexAdded = true;
			}
			
			assert ALConfig.sensorIndex.size()==ssa.getSensors().size() : "unknown sensors added";
			
			while(sc.hasNextLine()){
				double[] sensorValues = new double[ssa.getSensors().size()];
				String str = sc.nextLine();
				String[] svs = str.split(" ");
				if(svs.length!=ss.length){ // incomplete data
					continue;
				}
				for(int i=0; i<sensorValues.length; i++){
					sensorValues[i] = Double.valueOf(svs[ALConfig.sensorIndex.get(i)]);
					if(sensorValues[i]>GlobalConfigs.maxSensorValues[i]){
						GlobalConfigs.maxSensorValues[i] = sensorValues[i];
					}
					
					if(sensorValues[i]<GlobalConfigs.minSensorValues[i]){
						GlobalConfigs.minSensorValues[i] = sensorValues[i];
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
