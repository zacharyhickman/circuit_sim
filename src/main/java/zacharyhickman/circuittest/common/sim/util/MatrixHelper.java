package zacharyhickman.circuittest.common.sim.util;

import org.ejml.data.DMatrixRMaj;
import org.ejml.simple.SimpleMatrix;

public class MatrixHelper
{
	/**
	 * Adds two matrices of same dimensions and returns the result matrix.
	 * @param A
	 * @param B
	 * @return
	 */
	public static SimpleMatrix addMatrix(SimpleMatrix matA, SimpleMatrix matB)
	{
		// Throw an exception if the matricies are of different size.
		if ((matA.numRows() != matB.numRows()) || (matA.numCols() != matB.numCols()))
			throw new ArrayIndexOutOfBoundsException("Matrix A and matrix B are not the same size (row x col).");
		
		// Create a result matrix based on the size of the first matrix.
		SimpleMatrix resultMat = new SimpleMatrix(matA.numRows(), matA.numCols());
		
		// Row loop.
		for (int i = 0; i < matA.numRows(); i++)
		{
			// Column loop.
			for (int j = 0; j < matA.numCols(); j++)
			{
				// Add elements and set element in result matrix.
				resultMat.set(i, j, matA.get(i, j) + matB.get(i, j));
			}
		}
		
		// Return result matrix.
		return resultMat;
	}
	
	/**
	 * Copies data from a SimpleMatrix into a new DMatrixRMaj object for complex operations.
	 * @param simpleMat
	 * @return
	 */
	public static DMatrixRMaj convertSimpleToDMatrixRMaj(SimpleMatrix simpleMat)
	{
		// Create a result matrix based on the size of the first matrix.
		DMatrixRMaj resultMat = new DMatrixRMaj(simpleMat.numRows(), simpleMat.numCols());
		
		// Row loop.
		for (int i = 0; i < simpleMat.numRows(); i++)
		{
			// Column loop.
			for (int j = 0; j < simpleMat.numCols(); j++)
			{
				// Add elements and set element in result matrix.
				resultMat.set(i, j, simpleMat.get(i, j));
			}
		}
		
		// Return result matrix.
		return resultMat;
	}
	
	/**
	 * Copies data from a DMatrixRMaj into a new SimpleMatrix object for easier operations.
	 * @param complexMat
	 * @return
	 */
	public static SimpleMatrix convertDMatrixRMajToSimple(DMatrixRMaj complexMat)
	{
		// Create a result matrix based on the size of the first matrix.
		SimpleMatrix resultMat = new SimpleMatrix(complexMat.getNumRows(), complexMat.getNumCols());
		
		// Row loop.
		for (int i = 0; i < complexMat.getNumRows(); i++)
		{
			// Column loop.
			for (int j = 0; j < complexMat.getNumCols(); j++)
			{
				// Add elements and set element in result matrix.
				resultMat.set(i, j, complexMat.get(i, j));
			}
		}
		
		// Return result matrix.
		return resultMat;
	}
}
