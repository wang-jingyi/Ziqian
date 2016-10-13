package io.github.wang_jingyi.ZiQian.active;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;




public class LaplaceEstimator implements Estimator {


	@Override
	public RealMatrix estimate(RealMatrix frequencyMatrix) {
		
		int nodeNumber = frequencyMatrix.getRowDimension();
		
		RealMatrix estrm = ALConfig.sparse? new OpenMapRealMatrix(nodeNumber, nodeNumber) : 
			MatrixUtils.createRealMatrix(nodeNumber, nodeNumber);
		
		double[] rowsums = new double[nodeNumber];
		for(int i=0; i<nodeNumber; i++){
			int rowsum = 0;
			double[] row = frequencyMatrix.getRow(i);
			for(int j=0; j<nodeNumber; j++){
				rowsum += row[j];
			}
			rowsums[i] = rowsum;
		}

		for(int i=0; i<nodeNumber; i++){
			for(int j=0; j<nodeNumber; j++){
				double fre = frequencyMatrix.getEntry(i, j);
				if(fre==0){continue;}
				double p = (1+fre) / (nodeNumber+rowsums[i]);
				estrm.setEntry(i, j, p);
			}
		}
		return estrm;
	}

}
