package ProbReasoning;

/**
 * Created by edreichua on 2/25/16.
 */
public class Matrix {

    public double[][] data;
    private int numRows, numColumns;

    /**
     * Constructor for Matrix, default all elements to zero
     * @param numRows
     * @param numColumns
     */
    public Matrix(int numRows, int numColumns){
        this.numRows = numRows;
        this.numColumns = numColumns;
        data = new double[numRows][numColumns];
    }

    /**
     * transpose: Find the transposition matrix
     * @return transposition matrix
     */
    public Matrix transpose() {
        Matrix result = new Matrix(numColumns, numRows);
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                result.data[j][i] = this.data[i][j];
            }
        }
        return result;
    }

    /**
     * multiplyVector: multiply the matrix A by a vector v (i.e. A*v)
     * @param vect
     * @return a vector of the product
     */
    public double[] multiplyVector(double[] vect){
        if(vect.length != numColumns)
            return null;
        double[] result = new double[numRows];
        for(int i = 0; i < numRows; i++){
            double currSum = 0;
            for(int j = 0; j < numColumns; j++){
                currSum += data[i][j]*vect[j];
            }
            result[i] = currSum;
        }
        return result;
    }

    public String toString() {
        String s = "";
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numColumns; c++) {
                s += (data[r][c]+" ");
            }
            s += "\n";
        }
        return s;
    }
}
