package ProbReasoning;
import java.text.*;

/**
 * Created by edreichua on 2/25/16.
 */
public class MazeDriver {

    private static final DecimalFormat fourdp = new DecimalFormat("#0.000");
    private static Maze maze;
    private static int[] startPos;
    private static char[] sensorData, direction;
    private static int[][] path;
    private static ProbabilisticReasoning p;

    /**
     * init
     * function to initialize the maze and to construct the probability distribution
     * by creating the ProbabilisticReasoning object
     */
    public static void init(){
        getPathfromDirection(direction,startPos);
        constructSensorData();
        p = new ProbabilisticReasoning(maze,sensorData);
        drawAll();

    }

    /**
     * drawAll
     * function to draw Ascii graphics for all time steps
     */
    public static void drawAll(){
        for(int i = 0; i < path.length; i++){
            System.out.println("Time step = "+i);
            System.out.println("Color observed by sensor is: "+sensorData[i]);
            drawAscii(i);
        }
    }

    /**
     * drawAscii
     * function to draw Ascii graphics for the time step currStep
     * @param currStep
     */
    public static void drawAscii(int currStep){

        for(int r = 0; r < maze.height; r++){
            for(int i = 0; i <= 4; i++) {
                for (int c = 0; c < maze.width; c++) {
                    if (!maze.isLegal(r, c) && i != 0) {
                        System.out.print("|###########");
                    }else if(!maze.isLegal(r, c)){
                        System.out.print("------------");
                    }else {

                        switch(i){
                            case 0:
                                System.out.print("------------");
                                break;
                            case 1:
                                System.out.print("| f = " + fourdp.format(p.vectors[currStep][maze.getStates(r, c)])+" ");
                                break;
                            case 2:
                                if(ProbabilisticReasoning.isForwardBackward && currStep != 0 && currStep != path.length-1) {
                                    System.out.print("| fb = " + fourdp.format(p.fbvectors[currStep][maze.getStates(r, c)]) );
                                }else if(ProbabilisticReasoning.isForwardBackward){
                                    System.out.print("| fb =  NA  ");
                                }else{
                                    System.out.print("|           ");
                                }
                                break;
                            case 3:
                                System.out.print("|     " + maze.getChar(r,c)+"     ");
                                break;
                            case 4:
                                if(path[currStep][0] == r && path[currStep][1] == c){
                                    System.out.print("|   robot   ");
                                }else{
                                    System.out.print("|           ");
                                }
                                break;
                            default:
                                System.out.print("|           ");
                                break;
                        }
                    }
                }
                if(i == 0)
                    System.out.println("-");
                else
                    System.out.println("|");
            }
        }
        for(int i = 0; i < maze.width; i++)
            System.out.print("------------");
        System.out.println("-\n");
    }




    /**
     * constructSensorData
     * function to construct the sensor data from the path taken
     */
    public static void constructSensorData(){
        sensorData = new char[path.length];
        for(int t = 0; t < path.length; t++){
            char actualColor = maze.getChar(path[t][0],path[t][1]);
            sensorData[t] = findRandColor(actualColor);
        }

    }

    /**
     * getPathFromDirection
     * function to find the path (in terms of coordinates given by row and column number) from
     * direction
     * @param dir
     * @param startPos
     */
    public static void getPathfromDirection(char[] dir, int[] startPos){

        path = new int[dir.length+1][2];
        path[0] = startPos;

        for(int i = 1; i < path.length; i++){
            switch(dir[i-1]){
                case 'n': path[i] = move(path[i-1],Maze.NORTH);
                    break;
                case 's': path[i] = move(path[i-1],Maze.SOUTH);
                    break;
                case 'e': path[i] = move(path[i-1],Maze.EAST);
                    break;
                case 'w': path[i] = move(path[i-1],Maze.WEST);
                    break;
            }
        }
    }

    /**
     * move
     * helper function for getPathFromDirection
     * @param currentPos
     * @param dir
     * @return the next position
     */
    public static int[] move(int[] currentPos, int[] dir){
        int[] nextPos = new int[currentPos.length];
        for(int i = 0; i < nextPos.length; i++){
            nextPos[i] = currentPos[i] + dir[i];
        }
        if(maze.isLegal(nextPos[0],nextPos[1])){
            return nextPos;
        }

        // return the same location if the next location is not legal
        return currentPos.clone();
    }

    /**
     * findRandColor
     * helper function for constructSensorData: test the program by simulating the detection of
     * color using the probability given
     * @param actualColor
     * @return
     */
    public static char findRandColor(char actualColor){

        double rand = Math.random();

        // construct the otherColors array
        char[] otherColors;
        switch(actualColor){
            case 'r': otherColors = new char[]{'b','g','y'};
                break;
            case 'b': otherColors = new char[]{'r','g','y'};
                break;
            case 'g': otherColors = new char[]{'r','b','y'};
                break;
            default: otherColors = new char[]{'r','b','g'};
                break;
        }

        // return the color detected by the sensor given the weighted probability
        if(rand < ProbabilisticReasoning.rightColor){
            return actualColor;
        }else if(rand < ProbabilisticReasoning.rightColor + ProbabilisticReasoning.wrongColor){
            return otherColors[0];
        }else if(rand < ProbabilisticReasoning.rightColor + 2*ProbabilisticReasoning.wrongColor){
            return otherColors[1];
        }else{
            return otherColors[2];
        }

    }

    public static void main(String[] args){
        maze = Maze.readFromFile("simple.maz");
        //maze = Maze.readFromFile("medium.maz");
        direction = new char[]{'e','e','w','s','s'};
        startPos = new int[]{0,0}; // indicated by row and column number
        init();

    }
}
