package com.hurricane.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

// Tests for hurricanes, shelters, suitability and check-in through the Server.
// Author: Max Vogel
public class ServerTest extends ServerTestBase {
    private Shelter shelter;

    @Before
    public void setupShelter() {
        shelter = new Shelter("shelter-test", "Test Shelter", location, 5);
    }

    @Test
    public void testAddedHurricaneIsSavedToFile() {
        server.addHurricane(new Hurricane(location, 2, 100.0));
        assertEquals(1, newDataManager().loadHurricanes().size());
    }

    @Test
    public void testFilterHurricanesByLocationFindsNearbyHurricane() {
        server.addHurricane(new Hurricane(location, 2, 100.0));
        assertEquals(1, server.filterHurricanesByLocation(location, 100.0).size());
    }

    @Test
    public void testAddedShelterIsSavedToFile() {
        server.addShelter(shelter);
        assertEquals(1, newDataManager().loadShelters().size());
    }

    @Test
    public void testFilterSheltersByLocationLeavesOutFarShelter() {
        server.addShelter(shelter);
        assertEquals(0, server.filterSheltersByLocation(farAway, 100.0).size());
    }

    @Test
    public void testFindAvailableSheltersLeavesOutClosedShelter() {
        shelter.setOpen(false);
        server.addShelter(shelter);
        assertEquals(0, server.findAvailableShelters(location, 100.0).size());
    }

    @Test
    public void testGetMyShelterForStaffFindsTheirShelter() {
        server.addShelter(shelter);
        loginAs(staff);
        assertSame(shelter, server.getMyShelter());
    }

    @Test
    public void testGetMyShelterForVictimIsNull() {
        loginAs(victim);
        assertNull(server.getMyShelter());
    }

    @Test
    public void testFindClosestShelterWhileLoggedOutIsNull() {
        assertNull(server.findClosestShelter());
    }

    @Test
    public void testGetDirectionsWhileLoggedOutIsEmpty() {
        assertEquals(0, server.getDirectionsToShelter(shelter).length);
    }

    @Test
    public void testGetDirectionsToNullShelterIsEmpty() {
        loginAs(victim);
        assertEquals(0, server.getDirectionsToShelter(null).length);
    }

    @Test
    public void testIsSuitableForShelterWithRoom() {
        assertTrue(server.isSuitable(shelter, testRequest()));
    }

    @Test
    public void testIsSuitableRefusesDogWhenShelterDoesNotAcceptDogs() {
        shelter.setPetCapacity(1);
        ReliefRequest request = testRequest();
        request.addPet(new Pet("Rex", "Dog"));
        assertFalse(server.isSuitable(shelter, request));
    }

    @Test
    public void testIsSuitableAllowsCatWhenShelterDoesNotAcceptDogs() {
        shelter.setPetCapacity(1);
        ReliefRequest request = testRequest();
        request.addPet(new Pet("Tom", "cat"));
        assertTrue(server.isSuitable(shelter, request));
    }

    @Test
    public void testIsSuitableRefusesMedicalNeedWithoutMedicalStaff() {
        ReliefRequest request = testRequest();
        request.setMedical(true, "Needs insulin");
        assertFalse(server.isSuitable(shelter, request));
    }

    @Test
    public void testIsSuitableRefusesGroupTooBigForShelter() {
        ReliefRequest request = testRequest();
        request.setPeopleCount(6);
        assertFalse(server.isSuitable(shelter, request));
    }

    // Returns before the map service is called, so this test needs no network
    @Test
    public void testFindSuitableShelterWithNoSheltersIsNull() {
        assertNull(server.findSuitableShelter(testRequest()));
    }

    @Test
    public void testCheckInFamilyPutsVictimInShelter() {
        server.addShelter(shelter);
        loginAs(staff);
        server.checkInFamily(testRequest());
        assertTrue(shelter.getOccupants().contains(victim));
    }

    @Test
    public void testCheckInFamilyAddsPetsToShelter() {
        shelter.setPetCapacity(1);
        server.addShelter(shelter);
        ReliefRequest request = testRequest();
        request.addPet(new Pet("Rex", "dog"));
        loginAs(staff);
        server.checkInFamily(request);
        assertEquals(1, shelter.getPets().size());
    }

    @Test
    public void testCheckInFamilyByVictimFails() {
        server.addShelter(shelter);
        loginAs(victim);
        assertFalse(server.checkInFamily(testRequest()));
    }

    @Test
    public void testCheckInNullRequestFails() {
        server.addShelter(shelter);
        loginAs(staff);
        assertFalse(server.checkInFamily(null));
    }

    @Test
    public void testCheckInRequestWithNoRequesterFails() {
        server.addShelter(shelter);
        loginAs(staff);
        assertFalse(server.checkInFamily(new ReliefRequest(location, "Need help", "Please help", skills(Skill.FOOD), null)));
    }

    @Test
    public void testCheckInFamilyWithNoPetRoomFails() {
        server.addShelter(shelter);
        ReliefRequest request = testRequest();
        request.addPet(new Pet("Rex", "dog"));
        loginAs(staff);
        assertFalse(server.checkInFamily(request));
    }

    @Test
    public void testCheckInSameFamilyTwiceFails() {
        server.addShelter(shelter);
        loginAs(staff);
        server.checkInFamily(testRequest());
        assertFalse(server.checkInFamily(testRequest()));
    }

    @Test
    public void testAssignVictimToShelterPutsVictimInShelter() {
        server.addShelter(shelter);
        assertTrue(server.assignVictimToShelter(victim, shelter));
    }
}
