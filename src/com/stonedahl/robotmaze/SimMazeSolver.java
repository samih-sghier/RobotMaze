package com.stonedahl.robotmaze;

import java.io.FileNotFoundException;
import java.util.*;


public class SimMazeSolver {
    private static List<VisitedCell> visitedCellsList = new ArrayList<>();
    private static RobotTracker tracker;
    private static List<Character> pathToGoBack = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {

        // NOTE: You can change the 500 ms animation delay to make the simulation run faster or slower.
        int animationDelay =100;
        boolean animateNeckTurns = false; // show every time the robot rotates its neck?
        boolean animateTurns = false;     // show every time the robot body rotates?
        SimRobot simRobot = new SimRobot("maze1.txt", animationDelay, animateNeckTurns, animateTurns, new Scanner(System.in));
        solveMaze(simRobot);
    }

    public static void solveMaze(SimRobot simRobot) {
        RobotTracker trackerBot = new RobotTracker(simRobot);
        tracker = trackerBot;
        while (!trackerBot.isSittingOnGoal()) {
            if (trackerBot.isSittingOnGoal()) {
                System.err.println("Found The Goal!");
            } else {
                for (Character dir : trackerBot.findDirectionsWithoutWall()) {
                    trackerBot.moveOneCell(dir);
                    pathToGoBack.add(convertToOppositeDirection(dir));
                    List<Character> newDirectionsOfNewPosition = trackerBot.findDirectionsWithoutWall();
                    visitedCellsList.add(new VisitedCell(trackerBot.getX(),trackerBot.getY(), newDirectionsOfNewPosition));
                    explore(dir);
                }
            }
        }
        promptEnterKey();
        returnHome();
    }

    public static void returnHome() {
        for (int i = pathToGoBack.size() -1; i >= 0; i--) {
            tracker.moveOneCell(pathToGoBack.get(i));
        }
    }

    public static void explore(Character dir) {
        while (!tracker.isSittingOnGoal()) {
            //movebot to new cell in that direction
            tracker.moveOneCell(dir);
            pathToGoBack.add(convertToOppositeDirection(dir));
            // remove opposite sense direction from list of possible directions
            List<Character> list = tracker.findDirectionsWithoutWall();
            list.remove(convertToOppositeDirection(dir));
            //add the cell to visited cells and new directions
            visitedCellsList.add(new VisitedCell(tracker.getX(), tracker.getY(), list));
            if (tracker.isSittingOnGoal()) {
                System.err.println("Found The Goal!");
                return;
            } else if (list.isEmpty()) {
                noNewDirectionsToExplore(dir);
            } else {
                exploreNewDirectionsAvailable();
            }
        }
    }

    public static void noNewDirectionsToExplore(Character dir) {
        //move back the robot in the opposite direction
        tracker.moveOneCell(convertToOppositeDirection(dir));
        pathToGoBack.remove(pathToGoBack.get(pathToGoBack.size()-1));
        // remove last direction from visited cell
        getLastVisitedCell().removeDirection(dir);
        //at this point we know that the last cell is empty from directions left to explore
        for (int i = visitedCellsList.size() - 1; i >= 0; i--) {
            List<Character> directionsLeftToExplore = visitedCellsList.get(i).getDirectionsToExploreForCell();
            if (!directionsLeftToExplore.isEmpty() && !tracker.isSittingOnGoal()) {
                directionsLeftToExplore.remove(dir);
                visitedCellsList.get(i).setDirectionsToExploreForCell(directionsLeftToExplore);
                System.out.println("DIRECTIONS LEFT TO EXPLORE: " + directionsLeftToExplore + "IN CELL (" +
                        visitedCellsList.get(i).getVistedX() + ", " + visitedCellsList.get(i).getVisitedY() + ")");
                //no more directions to explore in that cell
                if (directionsLeftToExplore.isEmpty()){
                    List<Character> updatedDir = tracker.findDirectionsWithoutWall();
                    updatedDir.remove(dir);
                    updatedDir.remove(convertToOppositeDirection(dir));
                    Character newDir = updatedDir.get(updatedDir.size()-1);
                    tracker.moveOneCell(newDir);
                    pathToGoBack.remove(convertToOppositeDirection(newDir));
                    List<Character> newList = tracker.findDirectionsWithoutWall();
                    newList.remove(newDir);
                    newList.remove(convertToOppositeDirection(newDir));
                    explore(newList.get(newList.size()-1));
                }
                if (!tracker.isSittingOnGoal()) explore(directionsLeftToExplore.get(directionsLeftToExplore.size()-1));
            }
        }

    }

    public static void exploreNewDirectionsAvailable() {
        List<Character> newDirectionsToExplore = getLastVisitedCell().getDirectionsToExploreForCell();
        Character newDirectionToExplore = newDirectionsToExplore.get(newDirectionsToExplore.size()-1);
        explore(newDirectionToExplore);
    }

    public static VisitedCell getLastVisitedCell() {
        return visitedCellsList.get(visitedCellsList.size()-1);
    }

    public static Character convertToOppositeDirection(Character direction) {
        if (direction == 'N') {
            return 'S';
        } else if (direction == 'S') {
            return 'N';
        } else if (direction == 'E') {
            return 'W';
        } else if (direction == 'W') {
            return 'E';
        }
        return null;
    }

    public static void promptEnterKey(){
        System.out.println("Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}