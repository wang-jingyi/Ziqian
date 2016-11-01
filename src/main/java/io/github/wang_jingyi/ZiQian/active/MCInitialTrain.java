package io.github.wang_jingyi.ZiQian.active;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class MCInitialTrain implements InitialTrainPhase {
	
	private MarkovChain mc;
	private int pathLength;
	private int pathNumber;
	private RealMatrix fm;
	
	public MCInitialTrain(MarkovChain mc, int pl, int pn) {
		this.mc = mc;
		this.pathLength = pl;
		this.pathNumber = pn;
		this.fm = MatrixUtils.createRealMatrix(mc.getNodeNumber(), mc.getNodeNumber());
		generateTraces();
	}
	
	
	private void generateTraces(){
		List<List<Integer>> paths = new ArrayList<List<Integer>>();
		for(int i=0; i<pathNumber; i++){
			List<Integer> path = mc.simulate(pathLength);
			paths.add(path);
		}
		fm = Samples.getFrequencyMatrix(paths, mc.getNodeNumber());
	}

	@Override
	public RealMatrix getInitialFrequencyMatrix() {
		return fm;
	}

}
