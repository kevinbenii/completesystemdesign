package com.hurricane.model;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

// Tests for moving a hurricane and working out its direction and affected area.
//
// +---------------------------------+----------------------------------------------+
// | Test                            | Reasoning                                    |
// +---------------------------------+----------------------------------------------+
// | new hurricane history           | the track has to start at the first location |
// | update moves hurricane          | the current location becomes the new one     |
// | update changes category         | the new category replaces the old one        |
// | update changes wind speed       | the new wind speed replaces the old one      |
// | update adds to history          | each move has to be kept in the track        |
// | update with null location       | a null location would break later calls      |
// | update with category over 5     | categories only go from 1 to 5               |
// | update with negative wind speed | wind speed can't be below zero               |
// | direction before moving         | with one point there is no direction         |
// | direction X                     | X is the change in longitude                 |
// | direction Y                     | Y is the change in latitude                  |
// | direction after two moves       | only the last move should count              |
// | area includes current location  | the storm is inside its own area             |
// | area leaves out far points      | points outside the radius are not affected   |
// | area keeps old points in radius | old points inside the radius still count     |
// | area with negative radius       | no point can be inside a negative radius     |
// +---------------------------------+----------------------------------------------+
//
// Author: Max Vogel
public class HurricaneTest {
    private static final double DELTA = 0.000001;

    private Location start;
    private Location east;
    private Location farNorth;
    private Hurricane hurricane;

    private Location testLocation(double latitude, double longitude) {
        return new Location("123 Test Street", "Columbia", "SC", "29201", latitude, longitude);
    }

    @Before
    public void setup() {
        start = testLocation(34.0, -81.0);
        east = testLocation(35.0, -79.0);
        // About 414 miles north of start
        farNorth = testLocation(40.0, -81.0);
        hurricane = new Hurricane(start, 2, 100.0);
    }

    @Test
    public void testNewHurricaneHistoryStartsWithItsLocation() {
        assertSame(start, hurricane.getLocationHistory().get(0));
    }

    @Test
    public void testUpdateDataMovesHurricane() {
        hurricane.updateData(east, 3, 120.0);
        assertSame(east, hurricane.getLocation());
    }

    @Test
    public void testUpdateDataChangesCategory() {
        hurricane.updateData(east, 3, 120.0);
        assertEquals(3, hurricane.getCategory());
    }

    @Test
    public void testUpdateDataChangesWindSpeed() {
        hurricane.updateData(east, 3, 120.0);
        assertEquals(120.0, hurricane.getWindSpeed(), DELTA);
    }

    @Test
    public void testUpdateDataAddsNewLocationToHistory() {
        hurricane.updateData(east, 3, 120.0);
        assertEquals(2, hurricane.getLocationHistory().size());
    }

    // A null location would break calculateDirection and getAffectedArea later on
    @Test
    public void testUpdateDataWithNullLocationKeepsCurrentLocation() {
        hurricane.updateData(null, 3, 120.0);
        assertSame(start, hurricane.getLocation());
    }

    // Hurricane categories only go from 1 to 5
    @Test
    public void testUpdateDataWithCategoryOverFiveKeepsCurrentCategory() {
        hurricane.updateData(east, 6, 120.0);
        assertEquals(2, hurricane.getCategory());
    }

    @Test
    public void testUpdateDataWithNegativeWindSpeedKeepsCurrentWindSpeed() {
        hurricane.updateData(east, 3, -10.0);
        assertEquals(100.0, hurricane.getWindSpeed(), DELTA);
    }

    @Test
    public void testCalculateDirectionBeforeMovingIsZero() {
        Vector2 direction = hurricane.calculateDirection();
        assertEquals(0.0, direction.dot(direction), DELTA);
    }

    @Test
    public void testCalculateDirectionXIsChangeInLongitude() {
        hurricane.updateData(east, 3, 120.0);
        assertEquals(2.0, hurricane.calculateDirection().getX(), DELTA);
    }

    @Test
    public void testCalculateDirectionYIsChangeInLatitude() {
        hurricane.updateData(east, 3, 120.0);
        assertEquals(1.0, hurricane.calculateDirection().getY(), DELTA);
    }

    @Test
    public void testCalculateDirectionOnlyUsesLastMove() {
        hurricane.updateData(east, 3, 120.0);
        hurricane.updateData(testLocation(36.0, -79.0), 3, 120.0);
        assertEquals(0.0, hurricane.calculateDirection().getX(), DELTA);
    }

    @Test
    public void testGetAffectedAreaIncludesCurrentLocation() {
        ArrayList<Location> area = hurricane.getAffectedArea(100.0);
        assertTrue(area.contains(start));
    }

    @Test
    public void testGetAffectedAreaLeavesOutPointsOutsideRadius() {
        hurricane.updateData(farNorth, 3, 120.0);
        ArrayList<Location> area = hurricane.getAffectedArea(100.0);
        assertFalse(area.contains(start));
    }

    @Test
    public void testGetAffectedAreaIncludesOldPointsInsideRadius() {
        hurricane.updateData(farNorth, 3, 120.0);
        ArrayList<Location> area = hurricane.getAffectedArea(500.0);
        assertTrue(area.contains(start));
    }

    @Test
    public void testGetAffectedAreaWithNegativeRadiusIsEmpty() {
        ArrayList<Location> area = hurricane.getAffectedArea(-1.0);
        assertEquals(0, area.size());
    }
}
