package com.location;

import java.util.ArrayList;

/** Route between two places */
public class DrivingRoute {

    private final MapLocation startingLocation;
    private final MapLocation endingLocation;
    private final ArrayList<Coordinates> routePoints;

    /** Makes a route */
    public DrivingRoute(
            MapLocation startingLocation,
            MapLocation endingLocation,
            ArrayList<Coordinates> routePoints
    ) {
        this.startingLocation = startingLocation;
        this.endingLocation = endingLocation;
        this.routePoints = routePoints;
    }

    /** Start */
    public MapLocation getStartingLocation() {
        return startingLocation;
    }

    /** End */
    public MapLocation getEndingLocation() {
        return endingLocation;
    }

    /** Points along the route */
    public ArrayList<Coordinates> getRoutePoints() {
        return routePoints;
    }
}