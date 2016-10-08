package io.github.wang_jingyi.ZiQian.active;



public class EmpiricalFrequencyEstimator implements Estimator {

	@Override
	public MarkovChain estimate(int[][] frequencyMatrix) {
		int nodeNumber = frequencyMatrix.length;
		double[][] estimatedTransitionMatrix = new double[nodeNumber][nodeNumber];

		int[] rowsums = new int[nodeNumber];
		for(int i=0; i<nodeNumber; i++){
			int rowsum = 0;
			int[] row = frequencyMatrix[i];
			for(int j=0; j<nodeNumber; j++){
				rowsum += row[j];
			}
			rowsums[i] = rowsum;
		}

		for(int i=0; i<nodeNumber; i++){
			for(int j=0; j<nodeNumber; j++){
				double fre = frequencyMatrix[i][j];
				double p = 1;
				p = fre / rowsums[i];
				estimatedTransitionMatrix[i][j] = p;
			}
		}

		return new MarkovChain(estimatedTransitionMatrix);
	}

}
