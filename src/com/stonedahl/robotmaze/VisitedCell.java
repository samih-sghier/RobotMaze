package com.stonedahl.robotmaze;

import java.util.List;

public class VisitedCell {
    private int x;
    private int y;
    private List<Character> directionsToExplore;

    public VisitedCell(int x, int y, List<Character> directionsToExplore) {
        this.x = x;
        this.y = y;
        this.directionsToExplore = directionsToExplore;
    }

    public int getVistedX() {
        return x;
    }

    public int getVisitedY() {
        return y;
    }

    public void setDirectionsToExploreForCell(List<Character> directionsToExplore) {
       this.directionsToExplore = directionsToExplore;
    }

    public List<Character> getDirectionsToExploreForCell() {
        return directionsToExplore;
    }

    public void removeDirection(Character direction) {
        directionsToExplore.remove(direction);
    }
}
