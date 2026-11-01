package com.hurricane.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for shelter check-in, check-out and space checks.
 *
 * @author Jon Luu
 */
public class ShelterTest {
    private Shelter shelter;

    private Location testLocation() {
        return new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
    }

    private Victim testVictim(String username) {
        return new Victim(username, "password", "803-222-3333", testLocation());
    }

    // Fresh open shelter with 2 beds before every test
    @Before
    public void setup() {
        shelter = new Shelter("shelter-test", "Test Shelter", testLocation(), 2);
    }

    @Test
    public void testCheckInVictimWithSpaceReturnsTrue() {
        boolean success = shelter.checkIn(testVictim("victimone"));
        assertTrue(success);
    }

    @Test
    public void testCheckInVictimIncreasesOccupancy() {
        shelter.checkIn(testVictim("victimone"));
        assertEquals(1, shelter.getCurrentOccupancy());
    }

    @Test
    public void testCheckInClosedShelterReturnsFalse() {
        shelter.setOpen(false);
        boolean success = shelter.checkIn(testVictim("victimone"));
        assertFalse(success);
    }

    @Test
    public void testCheckInFullShelterReturnsFalse() {
        shelter.checkIn(testVictim("victimone"));
        shelter.checkIn(testVictim("victimtwo"));
        boolean success = shelter.checkIn(testVictim("victimthree"));
        assertFalse(success);
    }

    @Test
    public void testCheckInSameVictimTwiceReturnsFalse() {
        Victim victim = testVictim("victimone");
        shelter.checkIn(victim);
        boolean success = shelter.checkIn(victim);
        assertFalse(success);
    }

    @Test
    public void testCheckOutLowersOccupancy() {
        Victim victim = testVictim("victimone");
        shelter.checkIn(victim);
        shelter.checkOut(victim);
        assertEquals(0, shelter.getCurrentOccupancy());
    }

    @Test
    public void testCheckOutVictimNotInShelterKeepsOccupancy() {
        shelter.checkIn(testVictim("victimone"));
        shelter.checkOut(testVictim("victimtwo"));
        assertEquals(1, shelter.getCurrentOccupancy());
    }

    @Test
    public void testHasSpaceForTooManyPeople() {
        assertFalse(shelter.hasSpaceFor(3, 0));
    }
}
