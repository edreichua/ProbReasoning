/**
 * Author: Edrei Chua
 * Created on: 01/17/2016
 *
 * Maze for Maze World
 *
 * Credit: Stub provided by Balcom, Norvig and Russell Textbook on AI, piazza
 *
 * Dependencies: refer to README
 */
package ProbReasoning;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Maze {
	final static Charset ENCODING = StandardCharsets.UTF_8;

	// A few useful constants to describe actions
	public static int[] NORTH = {-1, 0};
	public static int[] EAST = {0, 1};
	public static int[] SOUTH = {1, 0};
	public static int[] WEST = {0, -1};
	public static int[] STAY = {0, 0};
	
	public int width, height, numStates;

	private int[][] states; // mapping from a position coordinate to a state number
	private char[][] grid;

	public static Maze readFromFile(String filename) {
		Maze m = new Maze();

		try {
			List<String> lines = readFile(filename);
			m.height = lines.size();

			int r = 0;
			m.grid = new char[m.height][];
			m.states = new int[m.height][];
			int stateNum = 0;

			for (String line : lines) {
				m.width = line.length();
				m.grid[r] = new char[m.width];
				m.states[r] = new int[m.width];

				for (int c = 0; c < line.length(); c++) {
					m.grid[r][c] = line.charAt(c);
					// modification to include wall
					if(m.grid[r][c] == '#') {
						m.states[r][c] = -1;
					}else{
						m.states[r][c] = stateNum;
						stateNum++;
					}
				}
				r++;
				m.numStates = stateNum; // numStates give the total number of states
				// System.out.println(line.length());
			}

			return m;
		} catch (IOException E) {
			E.printStackTrace();
			return null;
		}
	}

	private static List<String> readFile(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		return Files.readAllLines(path, ENCODING);
	}

	public char getChar(int r, int c) {
		return grid[r][c];
	}

	public int getStates(int r, int c) {
		return states[r][c];
	}
	
	// is the location x, y on the map, and also a legal floor tile (not a wall)?
	public boolean isLegal(int r, int c) {
		// on the map
		if(r >= 0 && r < height && c >= 0 && c < width) {
			// and it's a floor tile, not a wall tile:
			return getChar(r, c) != '#';
		}
		return false;
	}
	
	
	public String toString() {
		String s = "";
		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				s += grid[r][c];
			}
			s += "\n";
		}
		return s;
	}

	public static void main(String args[]) {
		Maze m = Maze.readFromFile("medium.maz");
		System.out.println(m.toString());
	}

}
