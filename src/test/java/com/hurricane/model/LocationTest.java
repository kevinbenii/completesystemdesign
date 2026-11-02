package com.hurricane.model;

import org.junit.Before;
import org.junit.Test;

import com.location.Address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests for distances and addresses of a location.
 *
 * +---------------------------+---------------------------------------------+
 * | Test                      | Reasoning                                   |
 * +---------------------------+---------------------------------------------+
 * | Distance to self          | The same location should give zero          |
 * | Distance one degree north | The distance should be about 69 miles       |
 * | Distance both ways        | Both directions should give equal distances |
 * | Distance to null          | Null should throw an exception              |
 * | Full address joins parts  | The address should include all parts        |
 * | Address keeps street      | The street should stay the same             |
 * | Address keeps ZIP         | The ZIP should stay the same                |
 * | Full address missing city | The address should skip missing cities      |
 * | Address missing street    | A missing street should return empty text   |
 * +---------------------------+---------------------------------------------+
 *
 * @author Kevin Beni
 */
public class LocationTest {
    private Location columbia;

    @Before
    public void setup() {
        columbia = new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
    }

    @Test
    public void testDistanceToSelfIsZero() {
        assertEquals(0, columbia.distanceTo(columbia), 0.001);
    }

    @Test
    public void testDistanceToOneDegreeNorthIsAbout69Miles() {
        Location north = new Location("9 North Road", "Rock Hill", "SC", "29730", 35.0, -81.0);
        assertEquals(69.09, columbia.distanceTo(north), 0.01);
    }

    @Test
    public void testDistanceToIsSameBothWays() {
        Location charleston = new Location("1 Harbor Drive", "Charleston", "SC", "29401", 32.78, -79.93);
        assertEquals(columbia.distanceTo(charleston), charleston.distanceTo(columbia), 0.001);
    }

    @Test
    public void testDistanceToNullThrows() {
        assertThrows(NullPointerException.class, () -> columbia.distanceTo(null));
    }

    @Test
    public void testGetFullAddressJoinsAllParts() {
        assertEquals("123 Test Street, Columbia, SC 29201", columbia.getFullAddress());
    }

    @Test
    public void testGetFullAddressLeavesOutMissingCity() {
        Location noCity = new Location("123 Test Street", null, "SC", "29201", 34.0, -81.0);
        assertEquals("123 Test Street, SC 29201", noCity.getFullAddress());
    }

    @Test
    public void testToAddressMissingStreetIsEmpty() {
        Location noStreet = new Location(null, "Columbia", "SC", "29201", 34.0, -81.0);
        assertEquals("", noStreet.toAddress().getStreet());
    }

    @Test
    public void testToAddressKeepsStreet() {
        Address address = columbia.toAddress();
        assertEquals("123 Test Street", address.getStreet());
    }

    @Test
    public void testToAddressKeepsZip() {
        Address address = columbia.toAddress();
        assertEquals("29201", address.getZip());
    }
}
