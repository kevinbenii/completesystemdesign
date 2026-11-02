package com.hurricane.model;

import java.lang.reflect.Field;
import java.nio.file.DirectoryStream;
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
 * Tests for the Server's shelter and hurricane methods.
 *
 * AI was used to write the test code. Test list and reasoning:
 * +--------------------------------+-------------------------------------------------------------------+
 * | Test                           | Reasoning                                                         |
 * +--------------------------------+-------------------------------------------------------------------+
 * |                                |                                                                   |
 * +--------------------------------+-------------------------------------------------------------------+
 *
 * @author Kevin Beni
 */
public class ServerShelterTest {
    private Path testFolder;
    private Server server;
    private Victim victim;

    private Location columbia() {
        return new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
    }

    private Location charleston() {
        return new Location("1 Harbor Drive", "Charleston", "SC", "29401", 32.78, -79.93);
    }

    // Victim plus two family members, so a household of 3
    private Victim familyOfThree() {
        Victim family = new Victim("carterfamily", "password", "803-222-3333", columbia());
        family.addFamilyMember(new FamilyMember("Rosa", 40, ""));
        family.addFamilyMember(new FamilyMember("Felix", 12, ""));
        family.addFamilyMember(new FamilyMember("Ana", 8, ""));
        return family;
    }

    private ReliefRequest requestFrom(Victim requester) {
        return requester.createRequest("Need a shelter", "Our house flooded", new ArrayList<Skill>());
    }

    // Logs in a manager who runs the shelter with that id
    private void loginStaffFor(String shelterId) {
        server.registerUser(new ShelterStaff("staffmember", "password", "803-111-1111", columbia(), shelterId));
        server.login("staffmember", "password");
    }

    // Server is a singleton, so clear it to get a fresh one
    private void resetServer() throws Exception {
        Field instance = Server.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    // Empty folder for every test, so the real json files are never read or changed
    @Before
    public void setup() throws Exception {
        testFolder = Files.createTempDirectory("servershelter");
        resetServer();
        server = Server.getInstance(new FileDataManager(testFolder.toString()));
        victim = new Victim("victimone", "password", "803-222-3333", columbia());
    }

    // Saves write files, so remove them before the folder
    @After
    public void tearDown() throws Exception {
        resetServer();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(testFolder)) {
            for (Path file : files)
                Files.deleteIfExists(file);
        }
        Files.deleteIfExists(testFolder);
    }

    @Test
    public void testAddHurricaneIsFoundAtItsLocation() {
        Hurricane hurricane = new Hurricane(columbia(), 3, 120);
        server.addHurricane(hurricane);
        assertSame(hurricane, server.filterHurricanesByLocation(columbia(), 1).get(0));
    }

    @Test
    public void testAddHurricaneIsSavedForNextServer() throws Exception {
        server.addHurricane(new Hurricane(columbia(), 3, 120));
        resetServer();
        Server reloaded = Server.getInstance(new FileDataManager(testFolder.toString()));
        assertEquals(1, reloaded.filterHurricanesByLocation(columbia(), 1).size());
    }

    @Test
    public void testAddNullHurricaneIsIgnored() {
        server.addHurricane(null);
        assertEquals(0, server.filterHurricanesByLocation(columbia(), 10000).size());
    }

    @Test
    public void testFilterHurricanesLeavesOutFarHurricane() {
        server.addHurricane(new Hurricane(charleston(), 3, 120));
        assertEquals(0, server.filterHurricanesByLocation(columbia(), 50).size());
    }

    @Test
    public void testFilterHurricanesByNullLocationReturnsNothing() {
        server.addHurricane(new Hurricane(columbia(), 3, 120));
        assertEquals(0, server.filterHurricanesByLocation(null, 50).size());
    }

    @Test
    public void testAddShelterAddsToAllShelters() {
        server.addShelter(new Shelter("shelter-1", "Columbia High", columbia(), 10));
        assertEquals(1, server.getAllShelters().size());
    }

    @Test
    public void testAddShelterIsSavedForNextServer() throws Exception {
        server.addShelter(new Shelter("shelter-1", "Columbia High", columbia(), 10));
        resetServer();
        Server reloaded = Server.getInstance(new FileDataManager(testFolder.toString()));
        assertEquals(1, reloaded.getAllShelters().size());
    }

    @Test
    public void testAddNullShelterIsIgnored() {
        server.addShelter(null);
        assertEquals(0, server.getAllShelters().size());
    }

    @Test
    public void testFilterSheltersFindsNearbyShelter() {
        server.addShelter(new Shelter("shelter-1", "Columbia High", columbia(), 10));
        assertEquals(1, server.filterSheltersByLocation(columbia(), 10).size());
    }

    @Test
    public void testFilterSheltersLeavesOutFarShelter() {
        server.addShelter(new Shelter("shelter-1", "Charleston Arena", charleston(), 10));
        assertEquals(0, server.filterSheltersByLocation(columbia(), 50).size());
    }

    @Test
    public void testFilterSheltersByNullLocationReturnsNothing() {
        server.addShelter(new Shelter("shelter-1", "Columbia High", columbia(), 10));
        assertEquals(0, server.filterSheltersByLocation(null, 50).size());
    }

    @Test
    public void testFindAvailableSheltersFindsOpenShelterWithRoom() {
        server.addShelter(new Shelter("shelter-1", "Columbia High", columbia(), 10));
        assertEquals(1, server.findAvailableShelters(columbia(), 10).size());
    }

    @Test
    public void testFindAvailableSheltersLeavesOutFullShelter() {
        Shelter full = new Shelter("shelter-1", "Columbia High", columbia(), 1);
        full.checkIn(victim);
        server.addShelter(full);
        assertEquals(0, server.findAvailableShelters(columbia(), 10).size());
    }

    @Test
    public void testFindClosestShelterNotLoggedInIsNull() {
        server.addShelter(new Shelter("shelter-1", "Columbia High", columbia(), 10));
        assertNull(server.findClosestShelter());
    }

    @Test
    public void testIsSuitableShelterWithRoomIsTrue() {
        Shelter shelter = new Shelter("shelter-1", "Columbia High", columbia(), 10);
        assertTrue(server.isSuitable(shelter, requestFrom(victim)));
    }

    @Test
    public void testIsSuitableDogAtShelterWithoutDogsIsFalse() {
        Shelter shelter = new Shelter("shelter-1", "Columbia High", columbia(), 10);
        shelter.setPetCapacity(5);
        ReliefRequest request = requestFrom(victim);
        request.addPet(new Pet("Buddy", "Dog"));
        assertFalse(server.isSuitable(shelter, request));
    }

    @Test
    public void testIsSuitableMedicalNeedWithoutMedicalStaffIsFalse() {
        Shelter shelter = new Shelter("shelter-1", "Columbia High", columbia(), 10);
        ReliefRequest request = requestFrom(victim);
        request.setMedical(true, "Needs insulin");
        assertFalse(server.isSuitable(shelter, request));
    }

    @Test
    public void testIsSuitableTooManyPeopleIsFalse() {
        Shelter shelter = new Shelter("shelter-1", "Columbia High", columbia(), 2);
        ReliefRequest request = requestFrom(victim);
        request.setPeopleCount(3);
        assertFalse(server.isSuitable(shelter, request));
    }

    @Test
    public void testIsSuitableNullShelterIsFalse() {
        assertFalse(server.isSuitable(null, requestFrom(victim)));
    }

    @Test
    public void testIsSuitableNullRequestIsFalse() {
        Shelter shelter = new Shelter("shelter-1", "Columbia High", columbia(), 10);
        assertFalse(server.isSuitable(shelter, null));
    }

    @Test
    public void testFindSuitableShelterPicksOnlyShelterThatFits() {
        Shelter tooSmall = new Shelter("shelter-1", "Columbia High", columbia(), 1);
        Shelter bigEnough = new Shelter("shelter-2", "Charleston Arena", charleston(), 10);
        server.addShelter(tooSmall);
        server.addShelter(bigEnough);
        ReliefRequest request = requestFrom(victim);
        request.setPeopleCount(3);
        assertSame(bigEnough, server.findSuitableShelter(request));
    }

    @Test
    public void testFindSuitableShelterNoneFitIsNull() {
        server.addShelter(new Shelter("shelter-1", "Columbia High", columbia(), 1));
        ReliefRequest request = requestFrom(victim);
        request.setPeopleCount(3);
        assertNull(server.findSuitableShelter(request));
    }

    @Test
    public void testFindSuitableShelterNullRequestIsNull() {
        server.addShelter(new Shelter("shelter-1", "Columbia High", columbia(), 10));
        assertNull(server.findSuitableShelter(null));
    }

    @Test
    public void testCheckInFamilyByStaffIsTrue() {
        server.addShelter(new Shelter("shelter-1", "Columbia High", columbia(), 10));
        loginStaffFor("shelter-1");
        assertTrue(server.checkInFamily(requestFrom(familyOfThree())));
    }

    @Test
    public void testCheckInFamilyCountsWholeHousehold() {
        Shelter shelter = new Shelter("shelter-1", "Columbia High", columbia(), 10);
        server.addShelter(shelter);
        loginStaffFor("shelter-1");
        server.checkInFamily(requestFrom(familyOfThree()));
        assertEquals(3, shelter.getCurrentOccupancy());
    }

    @Test
    public void testCheckInFamilyAddsTheirPet() {
        Shelter shelter = new Shelter("shelter-1", "Columbia High", columbia(), 10);
        shelter.setPetCapacity(2);
        server.addShelter(shelter);
        loginStaffFor("shelter-1");
        ReliefRequest request = requestFrom(familyOfThree());
        request.addPet(new Pet("Buddy", "Dog"));
        server.checkInFamily(request);
        assertEquals(1, shelter.getPets().size());
    }

    @Test
    public void testCheckInFamilyNotLoggedInIsFalse() {
        server.addShelter(new Shelter("shelter-1", "Columbia High", columbia(), 10));
        assertFalse(server.checkInFamily(requestFrom(familyOfThree())));
    }

    @Test
    public void testCheckInFamilyWithoutEnoughRoomIsFalse() {
        server.addShelter(new Shelter("shelter-1", "Columbia High", columbia(), 2));
        loginStaffFor("shelter-1");
        assertFalse(server.checkInFamily(requestFrom(familyOfThree())));
    }

    @Test
    public void testCheckInFamilyNullRequestIsFalse() {
        server.addShelter(new Shelter("shelter-1", "Columbia High", columbia(), 10));
        loginStaffFor("shelter-1");
        assertFalse(server.checkInFamily(null));
    }

    @Test
    public void testAssignVictimToShelterWithRoomIsTrue() {
        Shelter shelter = new Shelter("shelter-1", "Columbia High", columbia(), 10);
        server.addShelter(shelter);
        assertTrue(server.assignVictimToShelter(victim, shelter));
    }

    @Test
    public void testAssignVictimToShelterAddsOccupant() {
        Shelter shelter = new Shelter("shelter-1", "Columbia High", columbia(), 10);
        server.addShelter(shelter);
        server.assignVictimToShelter(victim, shelter);
        assertTrue(shelter.getOccupants().contains(victim));
    }

    @Test
    public void testAssignVictimToFullShelterIsFalse() {
        Shelter full = new Shelter("shelter-1", "Columbia High", columbia(), 1);
        full.checkIn(new Victim("someoneelse", "password", "803-444-5555", columbia()));
        server.addShelter(full);
        assertFalse(server.assignVictimToShelter(victim, full));
    }

    @Test
    public void testAssignNullVictimToShelterIsFalse() {
        Shelter shelter = new Shelter("shelter-1", "Columbia High", columbia(), 10);
        server.addShelter(shelter);
        assertFalse(server.assignVictimToShelter(null, shelter));
    }

    @Test
    public void testAssignVictimToNullShelterIsFalse() {
        assertFalse(server.assignVictimToShelter(victim, null));
    }
}
