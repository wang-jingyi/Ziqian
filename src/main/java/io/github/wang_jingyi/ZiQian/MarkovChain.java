package io.github.wang_jingyi.ZiQian;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class MarkovChain {
	
	public static void main(String[] args) { 

        // the state transition matrix
        int N = 2;
        double[][] transition = { { 0,1},
                                  { 0.002, 0.998}
                                };


        // compute using 50 iterations of power method
        Matrix A = new Matrix(transition);
        A = A.transpose();
        Matrix x = new Matrix(N, 1, 1.0 / N); // initial guess for eigenvector
        for (int i = 0; i < 50; i++) {
            x = A.times(x);
            x = x.times(1.0 / x.norm1());       // rescale
        }
        System.out.println("Stationary distribution using power method:");
        x.print(9, 6);



        // compute by finding eigenvector corresponding to eigenvalue = 1
        EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
        Matrix V = eig.getV();
        double[] real = eig.getRealEigenvalues();
        for (int i = 0; i < N; i++) {
            if (Math.abs(real[i] - 1.0) < 1E-6) {
                x = V.getMatrix(0, N-1, i, i);
                x = x.times(1.0 / x.norm1());
                System.out.println("Stationary distribution using eigenvector:");
                x.print(9, 6);
            }
        }

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
    }

}
