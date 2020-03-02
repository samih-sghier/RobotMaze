package com.stonedahl.robotmaze;

import java.util.*;

public class RobotTracker {
	private int x = 0;
	private int y = 0;	
	private char dir = 'N';
	private SimRobot body; 
	
	public RobotTracker(SimRobot body) {
		this.body = body;
	}
	
	public void moveOneCell(char directionToMove) {
		face(directionToMove);
		boolean succeeded = body.forwardOneCell(); 
		if (succeeded) {
			if (dir == 'N') {
				y++;
			} else if (dir == 'S') {
				y--;
			} else if (dir == 'W') {
				x--;
			} else { // dir == 'E'
				x++;
			}
		}
	}
	
	private void face(char newDirection) {	
		if (dir == 'N') {
			if (newDirection == 'E') {
				body.right90();				
			} else if (newDirection == 'S') {
				body.right90();				
				body.right90();
			} else if (newDirection == 'W') {
				body.left90();
			}
		}
		else if (dir == 'E') {
			if (newDirection == 'S') {
				body.right90();				
			} else if (newDirection == 'W') {
				body.right90();				
				body.right90();
			} else if (newDirection == 'N') {
				body.left90();
			}
		}
		else if (dir == 'S') {
			if (newDirection == 'W') {
				body.right90();				
			} else if (newDirection == 'N') {
				body.right90();				
				body.right90();
			} else if (newDirection == 'E') {
				body.left90();
			}
		}
		else { // dir == 'W'
			if (newDirection == 'N') {
				body.right90();				
			} else if (newDirection == 'E') {
				body.right90();				
				body.right90();
			} else if (newDirection == 'S') {
				body.left90();
			}
		}
		dir = newDirection;
	}

	public float measureDistanceInDirection(char directionToMeasure) {
		float distance = -1;
		if (dir == 'N') {
			if (directionToMeasure == 'N') {
				distance = body.getDistanceMeasurement();
			} else if (directionToMeasure == 'E') {
				body.neckRight90();
				distance = body.getDistanceMeasurement();
				body.neckLeft90();
			} else if (directionToMeasure == 'S') {
				body.neckRight90();
				body.neckRight90();
				distance = body.getDistanceMeasurement();
				body.neckLeft90();
				body.neckLeft90();
			} else if (directionToMeasure == 'W') {
				body.neckLeft90();
				distance = body.getDistanceMeasurement();
				body.neckRight90();				
			}
		} else if (dir == 'E') {
			if (directionToMeasure == 'E') {
				distance = body.getDistanceMeasurement();
			} else if (directionToMeasure == 'S') {
				body.neckRight90();
				distance = body.getDistanceMeasurement();
				body.neckLeft90();
			} else if (directionToMeasure == 'W') {
				body.neckRight90();
				body.neckRight90();
				distance = body.getDistanceMeasurement();
				body.neckLeft90();
				body.neckLeft90();
			} else if (directionToMeasure == 'N') {
				body.neckLeft90();
				distance = body.getDistanceMeasurement();
				body.neckRight90();				
			}
		} else if (dir == 'S') {
			if (directionToMeasure == 'S') {
				distance = body.getDistanceMeasurement();
			} else if (directionToMeasure == 'W') {
				body.neckRight90();
				distance = body.getDistanceMeasurement();
				body.neckLeft90();
			} else if (directionToMeasure == 'N') {
				body.neckRight90();
				body.neckRight90();
				distance = body.getDistanceMeasurement();
				body.neckLeft90();
				body.neckLeft90();
			} else if (directionToMeasure == 'E') {
				body.neckLeft90();
				distance = body.getDistanceMeasurement();
				body.neckRight90();				
			}
		} else { //  (dir == 'W')
			if (directionToMeasure == 'W') {
				distance = body.getDistanceMeasurement();
			} else if (directionToMeasure == 'N') {
				body.neckRight90();
				distance = body.getDistanceMeasurement();
				body.neckLeft90();
			} else if (directionToMeasure == 'E') {
				body.neckRight90();
				body.neckRight90();
				distance = body.getDistanceMeasurement();
				body.neckLeft90();
				body.neckLeft90();
			} else if (directionToMeasure == 'S') {
				body.neckLeft90();
				distance = body.getDistanceMeasurement();
				body.neckRight90();				
			}
		}
		
		return distance;
	}
	
	public List<Character> findDirectionsWithoutWall() {
		List<Character> openDirections = new ArrayList<>();
		if (measureDistanceInDirection('N') > 0.25) {
			openDirections.add('N');
		}
		if (measureDistanceInDirection('E') > 0.25) {
			openDirections.add('E');
		}
		if (measureDistanceInDirection('W') > 0.25) {
			openDirections.add('W');
		}
		if (measureDistanceInDirection('S') > 0.25) {
			openDirections.add('S');
		}
		return openDirections;
	}
	
	public boolean isSittingOnGoal() {
		return body.getColorSensorValue().equals("WHITE");
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
		
	public String toString() {	
		return ("Robot is at: " + x + "," + y + " (facing" + dir + ")");
	}
}
