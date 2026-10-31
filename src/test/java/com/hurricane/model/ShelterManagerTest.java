package com.hurricane.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for adding, removing and finding shelters.
 *
 * @author Jon Luu
 */
public class ShelterManagerTest {
    private Path emptyFolder;
    private ShelterManager shelterManager;

    private Location testLocation() {
        return new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
    }

    private Shelter testShelter(String shelterId) {
        return new Shelter(shelterId, "Shelter " + shelterId, testLocation(), 10);
    }

    private Victim testVictim(String username) {
        return new Victim(username, "password", "803-222-3333", testLocation());
    }

    // Empty folder for every test, so the real json files are never read or changed
    @Before
    public void setup() throws IOException {
        emptyFolder = Files.createTempDirectory("sheltermanagertest");
        shelterManager = new ShelterManager(new FileDataManager(emptyFolder.toString()));
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(emptyFolder);
    }

    @Test
    public void testAddShelterAppearsInAllShelters() {
        Shelter shelter = testShelter("test-add");
        shelterManager.addShelter(shelter);
        assertTrue(shelterManager.getAllShelters().contains(shelter));
    }

    @Test
    public void testRemoveShelterTakesItOutOfAllShelters() {
        Shelter shelter = testShelter("test-remove");
        shelterManager.addShelter(shelter);
        shelterManager.removeShelter(shelter);
        assertFalse(shelterManager.getAllShelters().contains(shelter));
    }

    @Test
    public void testGetShelterByIdFindsAddedShelter() {
        Shelter shelter = testShelter("test-find");
        shelterManager.addShelter(shelter);
        assertSame(shelter, shelterManager.getShelterById("test-find"));
    }

    @Test
    public void testGetShelterByIdUnknownIdReturnsNull() {
        assertNull(shelterManager.getShelterById("no-such-shelter"));
    }

    @Test
    public void testFindAvailableSheltersLeavesOutClosedShelter() {
        Shelter closed = testShelter("test-closed");
        closed.setOpen(false);
        shelterManager.addShelter(closed);
        ArrayList<Shelter> available = shelterManager.findAvailableShelters(testLocation(), 10);
        assertFalse(available.contains(closed));
    }

    @Test
    public void testAssignVictimToShelterWithSpaceReturnsTrue() {
        Shelter shelter = testShelter("test-space");
        shelterManager.addShelter(shelter);
        boolean success = shelterManager.assignVictimToShelter(testVictim("victimone"), shelter);
        assertTrue(success);
    }

    @Test
    public void testAssignVictimToClosedShelterReturnsFalse() {
        Shelter shelter = testShelter("test-closed-assign");
        shelter.setOpen(false);
        shelterManager.addShelter(shelter);
        boolean success = shelterManager.assignVictimToShelter(testVictim("victimone"), shelter);
        assertFalse(success);
    }

    @Test
    public void testAssignVictimToFullShelterReturnsFalse() {
        Shelter shelter = new Shelter("test-full", "Full Shelter", testLocation(), 1);
        shelterManager.addShelter(shelter);
        shelterManager.assignVictimToShelter(testVictim("victimone"), shelter);
        boolean success = shelterManager.assignVictimToShelter(testVictim("victimtwo"), shelter);
        assertFalse(success);
    }

    @Test
    public void testAssignVictimToNewShelterLeavesOldShelter() {
        Shelter oldShelter = testShelter("test-old");
        Shelter newShelter = testShelter("test-new");
        shelterManager.addShelter(oldShelter);
        shelterManager.addShelter(newShelter);
        Victim victim = testVictim("victimone");
        shelterManager.assignVictimToShelter(victim, oldShelter);
        shelterManager.assignVictimToShelter(victim, newShelter);
        assertEquals(0, oldShelter.getCurrentOccupancy());
    }
}
