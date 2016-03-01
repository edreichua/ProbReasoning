README for Probabilistic Reasoning

Author: Edrei Chua
Created on: 02/26/2016

*********** DIRECTORY STRUCTURE ***********

There are a few important files in this directory:

Report (directory for report)
    ProbReasoning.pdf (detailed documentation of the code)
    ProbReasoning.tex (tex file)
src (directory for source code)
    > ProbabilisticReasoning.java
    > Maze.java
    > MazeDriver.java
    > Matrix.java
README.txt
simple.maz
medium.maz


*********** HOW TO START THE DEFAULT PROGRAM ***********

To start the program, compile all the .java files.

To run the program, run MazeDriver.java.

The default setup run forward-backward propagation and use the maze medium.maz

*********** ADDITIONAL FUNCTIONALITY ***********

To change the default setup, toggle the boolean constants isForwardBackward on line 17 of
ProbabilisticReasoning.java (set to false to show only results from forward propagation)

To change the default maze settings used (such as maze file, starting position and sequence of
directions), change the settings in the main function of MazeDriver.java

*********** SPECIAL CONSIDERATION ***********

The implementation of the driver uses a row-column representation instead of a xy-coordinate
representation. Therefore, moving North is equivalent to the vector {-1, 0} since we are
moving a row upwards (decreasing the row number by 1) while staying in the same column
number.
