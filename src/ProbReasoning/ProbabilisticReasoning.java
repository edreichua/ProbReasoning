package ProbReasoning;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by edreichua on 2/25/16.
 */
public class ProbabilisticReasoning {

    // boolean flag - set true for forward-backward propagation, false for just forward propagation
    public final static boolean isForwardBackward  = true;

    // Constants for probablitiy and color data
    public final static double rightColor = 0.88;
    public final static double wrongColor = 0.04;

    // Input
    private Maze maze;
    private char[] sensorData;
    private int numSteps;

    // Matrices
    private Matrix transition, transitionTranspose, sensorRed, sensorGreen, sensorBlue, sensorYellow;
    public double[][] vectors;
    public double[][] fbvectors;

    // Debugging
    private boolean debug = false;

    public ProbabilisticReasoning(Maze maze, char[] sensorData){

        // initialization
        this.maze = maze;
        this.sensorData = sensorData;
        numSteps = sensorData.length;
        vectors = new double[numSteps][maze.numStates];
        fbvectors = new double[numSteps][maze.numStates];

        // preprocessing of matrices
        buildTransition();
        buildSensor();
        buildStartVector();

        // forward backward propagation or just forward propagation of probability depending on boolean flag
        if(isForwardBackward)
            forwardBackward();
        else
            forward();

        // for debugging
        if(debug) {
            System.out.println(transition.toString());
            System.out.println(sensorRed.toString());
            System.out.println(sensorBlue.toString());
            System.out.println(sensorGreen.toString());
            System.out.println(sensorYellow.toString());

            for (int i = 0; i < numSteps; i++)
                System.out.println(Arrays.toString(vectors[i]));

            // backward propagation of probability
            double[] b1 = backward(new double[]{1,1,1,1},numSteps-1);
            double[] b2 = backward(b1,numSteps-2);
            System.out.println(Arrays.toString(b1));
            System.out.println(Arrays.toString(b2));
        }
    }

    public void forwardBackward(){
        // perform forward propagation first
        forward();

        // initialise backward vector
        double[] b = new double[maze.numStates];
        Arrays.fill(b,1);

        // fill in forward backward vector
        for(int t = numSteps-1; t>=1; t--){
            fbvectors[t] = pointMultiplyVect(vectors[t],b); // multiply both vectors pointwise
            normalize(fbvectors[t]); // normalize vectors
            b = backward(b,t-1);
        }
    }

    /**
     * forward
     * function to perform forward calculation of probability using matrix algorithm
     * v[t] = normlize(sensor*transitionTranspose*v[t-1])
     */
    public void forward(){

        for(int t = 1; t < numSteps; t++){

            // multiply with the transpose of the transition matrix
            vectors[t] = transitionTranspose.multiplyVector(vectors[t-1]);

            // multiply with the corresponding sensor matrix
            switch (sensorData[t]){
                case 'r': vectors[t] = sensorRed.multiplyVector(vectors[t]);
                    break;
                case 'b': vectors[t] = sensorBlue.multiplyVector(vectors[t]);
                    break;
                case 'g': vectors[t] = sensorGreen.multiplyVector(vectors[t]);
                    break;
                case 'y': vectors[t] = sensorYellow.multiplyVector(vectors[t]);
                    break;
                default:
                    break;
            }

            normalize(vectors[t]);

        }
    }

    /**
     * backward
     * function to compute the backward probability distribution. helper to forwardBackward
     * @param b backward vector
     * @param t time step
     * @return the new backward vector
     */
    public double[] backward(double[] b, int t){

        double[] result = new double[b.length];

        // multiply with the corresponding sensor matrix
        switch (sensorData[t]){
            case 'r': result = sensorRed.multiplyVector(b);
                break;
            case 'b': result = sensorBlue.multiplyVector(b);
                break;
            case 'g': result = sensorGreen.multiplyVector(b);
                break;
            case 'y': result = sensorYellow.multiplyVector(b);
                break;
            default:
                break;
        }

        // multiply with the transition matrix
        return transition.multiplyVector(result);
    }

    /**
     * pointMultiplyVect
     * helper function to multiply two vectors point wise
     * @param first
     * @param second
     * @return the pointwise product of two vectors
     */
    private double[] pointMultiplyVect(double[] first, double[] second){
        double[] result = new double[first.length];

        for(int i = 0; i < first.length; i++)
            result[i] = first[i]*second[i];

        return result;
    }

    /**
     * normalize
     * function to normalize vector
     * @param vect
     */
    public void normalize(double[] vect){
        double[] result = new double[vect.length];

        double sum = 0;
        for(int i = 0; i < vect.length; i++){
            sum += vect[i];
        }

        for(int i = 0; i < vect.length; i++){
            vect[i] = vect[i] / sum;
        }

    }

    /**
     * buildStartVector
     * function to build the start vector
     */
    public void buildStartVector(){
        Arrays.fill(vectors[0], 1.0 / ((double) maze.numStates));
    }

    /**
     * buildTransition
     * function to build transition matrix
     */
    public void buildTransition(){
        transition = new Matrix(maze.numStates,maze.numStates);

        for(int r = 0; r < maze.height; r++){
            for(int c = 0; c < maze.width; c++){

                int stateNum = maze.getStates(r,c);

                // default zero if it is a wall
                if(stateNum == -1)
                    continue;

                int numWalls;
                List<Integer> neighbors = findNeighbors(r,c);

                if(neighbors.size()==1) {
                    transition.data[stateNum][stateNum] = 1;

                }else {
                    numWalls = 5 - neighbors.size();
                    transition.data[stateNum][stateNum] = (double)numWalls/4.0;

                    for(int num: neighbors){
                        if(num == stateNum) continue;
                        transition.data[stateNum][num] = 0.25;
                    }
                }
            }
        }
        transitionTranspose = transition.transpose();
    }

    /**
     * buildSensor: build sensor matrix
     * function to build sensor matrices
     */
    public void buildSensor(){

        // initialise the four different sensor matrices
        sensorRed = new Matrix(maze.numStates,maze.numStates);
        sensorGreen = new Matrix(maze.numStates,maze.numStates);
        sensorBlue = new Matrix(maze.numStates,maze.numStates);
        sensorYellow = new Matrix(maze.numStates,maze.numStates);

        for(int r = 0; r < maze.height; r++){
            for(int c = 0; c < maze.width; c++){

                int stateNum = maze.getStates(r,c);

                // skip if it is a wall
                if(stateNum == -1)
                    continue;

                // Set the diagonal of the sensor matrices to its respective probability
                sensorRed.data[stateNum][stateNum] = wrongColor;
                sensorGreen.data[stateNum][stateNum] = wrongColor;
                sensorYellow.data[stateNum][stateNum] = wrongColor;
                sensorBlue.data[stateNum][stateNum] = wrongColor;

                switch (maze.getChar(r,c)){
                    case 'r': sensorRed.data[stateNum][stateNum] = rightColor;
                        break;

                    case 'g': sensorGreen.data[stateNum][stateNum] = rightColor;
                        break;

                    case 'b': sensorBlue.data[stateNum][stateNum] = rightColor;
                        break;

                    case 'y': sensorYellow.data[stateNum][stateNum] = rightColor;
                        break;

                    default:
                        break;

                }
            }
        }
    }

    /**
     * findNeighbours
     * helper function for buildTransition: find all the neighbours of a position, including itself
     * @param r row number of maze position
     * @param c column number of maze position
     * @return list of all neighbours (state number) of a maze position, including itself
     */
    private List<Integer> findNeighbors(int r, int c){
        List<Integer> result = new ArrayList<>();

        // loop through all 9 possibilities
        for(int i = r-1; i <= r+1; i++){
            for(int j = c-1; j <= c+1; j++){

                // only consider the 4 adjacent neighbour and the position itself. Check if there is a wall
                if((i==r || j==c) && maze.isLegal(i,j))
                    result.add(maze.getStates(i,j));
            }
        }
        return result;
    }

    public static void main(String[] args){
        Maze m = Maze.readFromFile("simple.maz");
        ProbabilisticReasoning p = new ProbabilisticReasoning(m,new char[]{'r','b','b'});
    }


}
