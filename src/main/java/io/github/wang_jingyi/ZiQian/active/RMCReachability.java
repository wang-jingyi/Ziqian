package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.run.PlatformDependent;
import io.github.wang_jingyi.ZiQian.utils.ExternalCaller;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.PrismUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

public class RMCReachability {
	
	private RandomMarkovChain rmc;
	private List<Double> reachProbs;
	private String filePath;
	private int boundedStep;
	
	public RMCReachability(RandomMarkovChain rmc, int boundedStep) throws FileNotFoundException {
		this.rmc = rmc;
		this.reachProbs = new ArrayList<Double>();
		this.boundedStep = boundedStep;
		this.filePath = PlatformDependent.MODEL_ROOT + "/active/rmc/" + rmc.getRmcName();
		generatePrismFiles();
	}
	
	private void generatePrismFiles() throws FileNotFoundException{
		FileUtil.createDir(filePath);
		PrismUtil.MCToPrism(rmc.getTransitionMatrix().getData(), rmc.getRmcName(), filePath);
		rmc.WriteRMCPropertyList(filePath, boundedStep);
	}
	
	public void computeRMCReachability(){
		String pmPath = PlatformDependent.MODEL_ROOT + "/active/rmc/" + rmc.getRmcName() +"/" + rmc.getRmcName() + ".pm";
		String propPath = PlatformDependent.MODEL_ROOT + "/active/rmc/" + rmc.getRmcName() +"/" + rmc.getRmcName() + ".pctl";
		
		for(int i=rmc.getNumOfState()/2+1; i<=rmc.getNumOfState(); i++){
			reachProbs.add(PrismUtil.extractResultFromCommandOutput(ExternalCaller.executeCommand(new String[]{Config.PRISM_PATH
					, pmPath, propPath, "-prop", String.valueOf(i)})));
		}
	}
	
	public List<Double> getReachProbs() {
		return reachProbs;
	}

	public List<Double> computeEstReachability(RealMatrix estTransitionMatrix) throws FileNotFoundException{
		List<Double> estReachProbs = new ArrayList<Double>();
		PrismUtil.MCToPrism(estTransitionMatrix.getData(), rmc.getRmcName()+"_learn", filePath);

		String pmPath = PlatformDependent.MODEL_ROOT + "/active/rmc/" + rmc.getRmcName() +"/" + rmc.getRmcName() + "_learn.pm";
		String propPath = PlatformDependent.MODEL_ROOT + "/active/rmc/" + rmc.getRmcName() +"/" + rmc.getRmcName() + ".pctl";
		for(int i=rmc.getNumOfState()/2+1; i<=rmc.getNumOfState(); i++){
			estReachProbs.add(PrismUtil.extractResultFromCommandOutput(ExternalCaller.executeCommand(new String[]{Config.PRISM_PATH
					, pmPath, propPath, "-prop", String.valueOf(i)})));
		}
		return estReachProbs;
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		RandomMarkovChain rmc = new RandomMarkovChain(10, 0.8, "rmc1");
		rmc.generateRMC();
		
		RMCReachability rmcr = new RMCReachability(rmc,10);
		rmcr.generatePrismFiles();
		String op = ExternalCaller.executeCommand(new String[]{Config.PRISM_PATH,"/Users/jingyi/ziqian/active/rmc/rmc1/rmc1.pm","/Users/jingyi/ziqian/active/rmc/rmc1/rmc1.pctl","-prop","5"});
		System.out.println("result: " + PrismUtil.extractResultFromCommandOutput(op));
	}
	
	
	
}
