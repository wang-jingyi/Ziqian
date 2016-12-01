package io.github.wang_jingyi.ZiQian.active;

import io.github.wang_jingyi.ZiQian.run.Config;
import io.github.wang_jingyi.ZiQian.utils.ExternalCaller;
import io.github.wang_jingyi.ZiQian.utils.FileUtil;
import io.github.wang_jingyi.ZiQian.utils.PrismUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

public class Reachability {

	private RealMatrix transitionMatrix;
	private List<Integer> initialStates;
	private List<Double> initDist;
	private List<Integer> targetStates;
	private String dirPath;
	private String fileName;
	private int boundedStep;

	public Reachability(RealMatrix tm, List<Integer> is, List<Double> id, List<Integer> targetStates, 
			String dirPath, String fileName, int boundedStep) throws FileNotFoundException {
		this.transitionMatrix = tm;
		this.initialStates = is;
		this.initDist = id;
		this.targetStates = targetStates;
		this.dirPath= dirPath;
		this.fileName = fileName;
		this.boundedStep = boundedStep;
		generatePrismFiles();
	}

	private void generatePrismFiles() throws FileNotFoundException{
		FileUtil.createDir(dirPath);
		PrismUtil.MCToPrism(transitionMatrix.getData(), initialStates, initDist, fileName, dirPath);
		PrismUtil.WritePropertyList(targetStates, dirPath, fileName, transitionMatrix.getRowDimension(), boundedStep);
	}

	public double computeReachability(int i){
		String pmPath = dirPath + "/" + fileName +".pm";
		String propPath = dirPath +"/" + fileName + ".pctl";
		
		double reachp = PrismUtil.extractResultFromCommandOutput(ExternalCaller.executeCommand(new String[]{Config.PRISM_PATH
				, pmPath, propPath, "-prop", String.valueOf(i)}));
		return reachp;
	}
	
	public List<Double> computeReachability(){
		List<Double> reachProbs = new ArrayList<Double>();
		String pmPath = dirPath + "/" + fileName +".pm";
		String propPath = dirPath + "/" + fileName +".pctl";
		for(int i=1; i<=targetStates.size(); i++){
			reachProbs.add(PrismUtil.extractResultFromCommandOutput(ExternalCaller.executeCommand(new String[]{Config.PRISM_PATH
					, pmPath, propPath, "-prop", String.valueOf(i)})));
		}
		return reachProbs;
	}
	
	
	public static List<Double> computeReachability(String pm, String pctl, int propcount){
		List<Double> result = new ArrayList<Double>();
		for(int i=1; i<=propcount; i++){
			result.add(PrismUtil.extractResultFromCommandOutput(ExternalCaller.executeCommand(new String[]{Config.PRISM_PATH
				, pm, pctl, "-prop", String.valueOf(i)})));
		}
		return result;
			
	}

}
