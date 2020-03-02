package com.stonedahl.robotmaze;

// WARNING: DO NOT MODIFY THIS FILE!  (You can read it though, to learn more!)

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * This class represents a simulated (much simplified) Lego Robot 
 * (and environment) attempting to solve a maze exploration problem for CSC 320.
 * 
 * For writing your maze-solving algorithm, you should NOT modify this class at all...
 * You may only call its PUBLIC methods from your own code.
 * Your code must do its own work of keeping track of the robot's location/orientation,
 * (just like your physical robot will have to keep track of that information itself)
 *  
 * @author Dr. Stonedahl
 *
 */
public class SimRobot {
	private static final int NORTH = 0;
	private static final int EAST = 1;
	private static final int SOUTH = 2;
	private static final int WEST = 3;

	private static final int MAZE_WIDTH = 5;
	private static final int MAZE_HEIGHT = 3;
	private static final float MAZE_CELL_WIDTH_IN_METERS = 0.37f; 
	
	private char[][] grid;

	private int robotX;
	private int robotY;
	private int robotDir = NORTH;
	private int neckDir = NORTH;
	private int animationDelay = 0; 
	private boolean animateNeckTurns;
	private boolean animateTurns;
	private Scanner console;
	
	/** 
	 * @param mazeFileName - text file with a 21x13 character grid, 
	 *        which translates into a 5x3 grid for the actual maze layout.
	 *        (Note that this map is NOT drawn to scale...)
	 *     The goal G must be placed in the middle of one of the cells,
	 *     and the walls must be Xs and the open floor marked with periods.
	 * @param animationDelay - millisecond delay after each time printing the world state...
	 * @param animateNeckTurns - should the world be printed each time the neck rotates?
	 * @param animateTurns - should the world be printed each time the whole robot rotates?
	 * @param console -- needed to support the waitForEnterKey() behavior 
	 * @throws FileNotFoundException if the maze text file isn't found
	 */
	public SimRobot(String mazeFileName, int animationDelay, boolean animateNeckTurns, boolean animateTurns, Scanner console) throws FileNotFoundException {
		this.robotX = 2;  // the center of cell at (0,0) 
		this.robotY = 2;
		this.grid = new char[MAZE_WIDTH*4+1][MAZE_HEIGHT*4+1];
		this.animationDelay = animationDelay;
		this.animateNeckTurns = animateNeckTurns;
		this.animateTurns = animateTurns;
		this.console = console;
		loadMaze(mazeFileName);
		printWorld();
	}	
	
	/**
	 * 
	 * @return *true* if the move succeeded, and *false* if the robot hit a wall 
	 * and the bump sensor was activated.
	 */
	public boolean forwardOneCell() {
		if (canMove(robotDir)) {
			for (int i = 0; i < 4; i++) {
				robotX += 1 * dxForDir(robotDir);
				robotY += 1 * dyForDir(robotDir);
				printWorld();
			}
			return true;
		} else {
			robotX += 1 * dxForDir(robotDir);
			robotY += 1 * dyForDir(robotDir);
			System.err.println("BUMP SENSOR PUSHED! (will back up to center of cell)");
			printWorld();
			robotX -= 1 * dxForDir(robotDir);
			robotY -= 1 * dyForDir(robotDir);
			printWorld();
			return false;
		}
	}
	/**
	 * Turn the robot 90 degrees to the right...
	 */
	public void right90() {
		robotDir = (robotDir + 1) % 4;
		neckDir = (neckDir + 1) % 4;
		if (animateTurns) {
			printWorld();
		}
	}
	/**
	 * Turn the robot 90 degrees to the left...
	 */
	public void left90() {
		robotDir = (robotDir - 1 + 4) % 4;
		neckDir = (neckDir - 1 + 4) % 4;
		if (animateTurns) {
			printWorld();
		}
	}
	/**
	 * Rotate the neck of the robot 90 degrees to the right...
	 */
	public void neckRight90() {
		neckDir = (neckDir + 1) % 4; 
		if (animateNeckTurns) {
			printWorld();
		}
	}
	/**
	 * Rotate the neck of the robot 90 degrees to the left...
	 */
	public void neckLeft90() {
		neckDir = (neckDir - 1 + 4) % 4;
		if (animateNeckTurns) {
			printWorld();
		}
	}
	/**
	 * Measures the distance read by the simulated robot's distance sensor,
	 * in the current direction the neck is facing, and returns that distance.
	 * 
	 * This method assumes the robot's sensor is exactly in the middle of its current cell.
	 * (NOTE: this will NOT be the case in the physical robot's world, where the distance sensor
	 *        is back behind.)
	 * Thus, if N is the number of full cells the robot can see across, this method will
	 * return (N+0.5)*CELL_WIDTH in meters, or Float.POSITIVE_INFINITY if the distance
	 * is greater than 2.5m (like the physical robot would).  
	 * 
	 * @return the distance (in meters) this robot would report from its distance sensor...
	 */
	public float getDistanceMeasurement() {
		int dx =  dxForDir(neckDir);
		int dy = dyForDir(neckDir);
		int x = robotX + 2 * dx;
		int y = robotY + 2 * dy;
		int dist = 0;
		while (grid[x][y] != 'X') {
			x += dx;
			y += dy;
			dist++;
		}
		float fDist = (dist / 4 + 0.5f) * MAZE_CELL_WIDTH_IN_METERS;
		if (fDist <= 2.5) {
			return fDist;
		} else {
			return Float.POSITIVE_INFINITY;
		}
	}
	
	/** 
	 * @return the name of the color the robot's color sensor is seeing.
	 * In this simulation, it will only return "WHITE" if the robot is
	 * sitting on the white goal square (designated by a 'G' in the maze map file).
	 */
	public String getColorSensorValue() {
		if (grid[robotX][robotY] == 'G') { 
			return "WHITE";
		} else {
			return "BLACK";
		}
	}

	/** Cause the robot to wait for N milliseconds (ms) before continuing */ 
	public void msDelay(long millsecondsToDelay) {
		if (millsecondsToDelay > 0) {
			try {
				Thread.sleep(millsecondsToDelay);
			} catch (InterruptedException ex) {
				ex.printStackTrace(); // this shouldn't happen though...
			}
		}
	}

	/** Causes the robot to wait until the ENTER key is pressed before continuing */
	public void waitForEnterKey() {
		System.out.print("(Press ENTER to continue)");
		console.nextLine();
	}

	
	////////////////////////////
	// PRIVATE METHODS FOLLOW //
	////////////////////////////
	
	// internal method to load maze as the environment	
	private void loadMaze(String fileName) throws FileNotFoundException {
		Scanner input = new Scanner(new File(fileName));
		for (int y = MAZE_HEIGHT*4; y >= 0; y--) {
			String line = input.nextLine();
			for (int x = 0; x <= MAZE_WIDTH*4; x++) {
				grid[x][y] = line.charAt(x);
			}
		}
		input.close();
	}
	
	private void printWorld() {
		System.out.println();
		for (int y = MAZE_HEIGHT*4; y >= 0; y--) {		
			for (int x = 0; x <= MAZE_WIDTH*4 ; x++) {
				if (x == robotX && y == robotY) {
					System.out.print(directionChar(neckDir));					
				} else if (x == robotX + dxForDir(robotDir) && y == (robotY + dyForDir(robotDir))) {
					System.out.print(directionChar(robotDir));										
				}
				else {
					System.out.print(grid[x][y]);
				}
			}
			System.out.println();
		}
		msDelay(animationDelay);
	}
	private boolean canMove(int dir) {
		return grid[robotX + 2*dxForDir(dir)][robotY + 2*dyForDir(dir)] == '.';
	}
	
	private static int dxForDir(int dir) {
		switch (dir) {
			case NORTH: return 0;
			case EAST:	return 1;
			case SOUTH:	return 0;
			case WEST:	return -1;
		}
		throw new IllegalArgumentException("Direction code " + dir + " isn't a valid direction for robot movement.");
	}

	private static int dyForDir(int dir) {
		switch (dir) {
			case NORTH: return 1;
			case EAST:	return 0;
			case SOUTH:	return -1;
			case WEST:	return 0;
		}
		throw new IllegalArgumentException("Direction code " + dir + " isn't a valid direction for robot movement.");
	}
	private static char directionChar(int dir) {
		return (new char[] {'^', '>', 'v', '<'})[dir];
	}
	
}
