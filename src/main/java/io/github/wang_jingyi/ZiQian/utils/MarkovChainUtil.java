package io.github.wang_jingyi.ZiQian.utils;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SparseRealMatrix;

import Jama.Matrix;

/**
 * @author wangjingyi
 *
 */
public class MarkovChainUtil {
	
	
	/**
	 * @param transition_matrix
	 * @return the steady state distribution of the matrix
	 */
	public static double[] computeSteadyStateDistribution(double[][] transition_matrix){
		
		 // the state transition matrix
		Matrix matrix = new Matrix(transition_matrix);
        int N = matrix.getColumnDimension();
        double[][] transition = matrix.getArray();

        // compute using 50 iterations of power method
        Matrix A = new Matrix(transition);
        A = A.transpose();
        Matrix x = new Matrix(N, 1, 1.0 / N); // initial guess for eigenvector
        
//        for (int i = 0; i < 50; i++) {
//            x = A.times(x);
//            x = x.times(1.0 / x.norm1());       // rescale
//        }
//        System.out.println("Stationary distribution using power method:");
//        x.print(9, 6);
//		
		
		// If ergordic, stationary distribution = unique solution to Ax = x
        // up to scaling factor.
        // We solve (A - I) x = 0, but replace row 0 with constraint that
        // says the sum of x coordinates equals one
        Matrix B = A.minus(Matrix.identity(N, N));
        for (int j = 0; j < N; j++)
            B.set(0, j, 1.0);
        Matrix b = new Matrix(N, 1);
        b.set(0, 0, 1.0);
        x = B.solve(b);
        System.out.println("Stationary distribution by solving linear system of equations:");
        x.print(9, 6);
        
     // compute by finding eigenvector corresponding to eigenvalue = 1
//        EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
//        Matrix V = eig.getV();
//        double[] real = eig.getRealEigenvalues();
//        for (int i = 0; i < N; i++) {
//            if (Math.abs(real[i] - 1.0) < 1E-6) {
//                x = V.getMatrix(0, N-1, i, i);
//                x = x.times(1.0 / x.norm1());
////                System.out.println("Stationary distribution using eigenvector:");
////                x.print(9, 6);
//            }
//        }
        double steady_sum = 0;
        for(double d : x.getColumnPackedCopy()){
        	steady_sum = steady_sum + d;
        }
        assert Math.abs(steady_sum-1) < 1e-12: "====== steady distribution summed to " + steady_sum +  ", not 1 ======";
        return x.getColumnPackedCopy();
	}
	
	
	/**
	 * @param frequency_matrix
	 * @return normalized transition matrix given the frequency matrix
	 */
	public static RealMatrix normalizeFrequency(RealMatrix frequency_matrix){
		int matrix_size = frequency_matrix.getRowDimension();
		RealMatrix transition_matrix = null;
		if(frequency_matrix instanceof SparseRealMatrix){
			transition_matrix = new OpenMapRealMatrix(matrix_size, matrix_size);
		}
		else{
			transition_matrix = MatrixUtils.createRealMatrix(matrix_size, matrix_size);
		}
		
		for(int i=0; i<matrix_size; i++){
			double row_sum = 0;
			for(int j=0; j<matrix_size; j++){
				if(frequency_matrix.getEntry(i, j)!=0) // important to reduce running time
					row_sum += frequency_matrix.getEntry(i, j);
			}
			
			for(int j=0; j<matrix_size; j++){
				if(frequency_matrix.getEntry(i, j)!=0){ // important to reduce running time
					double p = frequency_matrix.getEntry(i, j)/row_sum;
					transition_matrix.setEntry(i, j, p);
				}
			}
		}
		return transition_matrix;
	}
	
	// 95% confidence interval for now
	public static RealMatrix normalizeBound(RealMatrix frequency_matrix){
		int matrix_size = frequency_matrix.getRowDimension();
		
		RealMatrix bound_matrix = null;
		if(frequency_matrix instanceof SparseRealMatrix){
			bound_matrix = new OpenMapRealMatrix(matrix_size, matrix_size);
		}
		else{
			bound_matrix = MatrixUtils.createRealMatrix(matrix_size, matrix_size);
		}
		
		for(int i=0; i<matrix_size; i++){
			double row_sum = 0;
			for(int j=0; j<matrix_size; j++){
				if(frequency_matrix.getEntry(i, j)!=0) // important to reduce time cost
					row_sum += frequency_matrix.getEntry(i, j);
			}
			
			for(int j=0; j<matrix_size; j++){
				if(frequency_matrix.getEntry(i, j)!=0){ // important to reduce time cost
					double p_ij = frequency_matrix.getEntry(i, j) / row_sum;
					bound_matrix.setEntry(i, j, 1.96*Math.sqrt(p_ij*(1-p_ij))/Math.sqrt(row_sum));
				}
			}
		}
		return bound_matrix;
	}

}
