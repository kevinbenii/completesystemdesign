package com.hurricane.model;

import java.io.IOException;
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
import static org.junit.Assert.assertTrue;

// Tests for saving shelters and requests to files and loading them back.
// Author: Max Vogel
public class FileDataManagerSaveTest {
    private Path testFolder;
    private FileDataManager dataManager;
    private Location location;
    private Shelter shelter;
    private Victim victim;

    // Saves the shelter with one data manager, then loads it with a brand new one on the same folder
    private Shelter saveThenReloadShelter() {
        ArrayList<Shelter> shelters = new ArrayList<Shelter>();
        shelters.add(shelter);
        dataManager.saveShelters(shelters);
        return new FileDataManager(testFolder.toString()).loadShelters().get(0);
    }

    private ReliefRequest saveThenReloadRequest(ReliefRequest request) {
        ArrayList<ReliefRequest> requests = new ArrayList<ReliefRequest>();
        requests.add(request);
        dataManager.saveRequests(requests);
        return new FileDataManager(testFolder.toString()).loadRequests().get(0);
    }

    // Shelters and requests only store usernames, so the user has to be in users.json to be found again
    private void saveUser(User user) {
        ArrayList<User> users = new ArrayList<User>();
        users.add(user);
        dataManager.saveUsers(users);
    }

    private ReliefRequest testRequest() {
        return new ReliefRequest(location, "Need food", "No food", new ArrayList<Skill>(), victim);
    }

    // Empty folder for every test, so the real json files are never read or changed
    @Before
    public void setup() throws IOException {
        testFolder = Files.createTempDirectory("filedatamanagersavetest");
        dataManager = new FileDataManager(testFolder.toString());
        location = new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
        shelter = new Shelter("shelter-test", "Test Shelter", location, 5);
        victim = new Victim("victimone", "password", "803-222-3333", location);
    }

    // The tests save files into the folder, so remove them before removing the folder
    @After
    public void tearDown() throws IOException {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(testFolder)) {
            for (Path file : files)
                Files.deleteIfExists(file);
        }
        Files.deleteIfExists(testFolder);
    }

    @Test
    public void testLoadSheltersWithNoFileIsEmpty() {
        assertEquals(0, dataManager.loadShelters().size());
    }

    @Test
    public void testLoadRequestsWithNoFileIsEmpty() {
        assertEquals(0, dataManager.loadRequests().size());
    }

    @Test(expected = IllegalStateException.class)
    public void testLoadSheltersWithBrokenFileThrows() throws IOException {
        Files.writeString(testFolder.resolve(DataConstants.SHELTERS_FILE), "this is not json");
        dataManager.loadShelters();
    }

    @Test
    public void testSavedShelterKeepsName() {
        assertEquals("Test Shelter", saveThenReloadShelter().getName());
    }

    @Test
    public void testSavedShelterKeepsCapacity() {
        assertEquals(5, saveThenReloadShelter().getCapacity());
    }

    @Test
    public void testSavedClosedShelterStaysClosed() {
        shelter.setOpen(false);
        assertFalse(saveThenReloadShelter().isOpen());
    }

    @Test
    public void testSavedShelterKeepsAcceptsDogs() {
        shelter.setAcceptsDogs(true);
        assertTrue(saveThenReloadShelter().acceptsDogs());
    }

    @Test
    public void testSavedShelterKeepsMedicalStaff() {
        shelter.setHasMedicalStaff(true);
        assertTrue(saveThenReloadShelter().hasMedicalStaff());
    }

    @Test
    public void testSavedShelterKeepsPetCapacity() {
        shelter.setPetCapacity(4);
        assertEquals(4, saveThenReloadShelter().getPetCapacity());
    }

    @Test
    public void testSavedShelterKeepsPets() {
        shelter.addPet(new Pet("Rex", "dog"));
        assertEquals(1, saveThenReloadShelter().getPets().size());
    }

    @Test
    public void testSavedShelterKeepsAvailableSkills() {
        shelter.getAvailableSkills().add(Skill.FOOD);
        assertTrue(saveThenReloadShelter().offersSkill(Skill.FOOD));
    }

    @Test
    public void testSavedShelterCountsWholeFamilyAfterReload() {
        victim.addFamilyMember(new FamilyMember("Dana", 40, ""));
        victim.addFamilyMember(new FamilyMember("Eli", 12, ""));
        saveUser(victim);
        shelter.checkIn(victim);
        assertEquals(2, saveThenReloadShelter().getCurrentOccupancy());
    }

    @Test
    public void testClosedShelterKeepsOccupantsAfterReload() {
        saveUser(victim);
        shelter.checkIn(victim);
        shelter.setOpen(false);
        assertEquals(1, saveThenReloadShelter().getOccupants().size());
    }

    @Test
    public void testOccupantMissingFromUsersFileIsDroppedOnReload() {
        shelter.checkIn(victim);
        assertEquals(0, saveThenReloadShelter().getOccupants().size());
    }

    @Test
    public void testSavedRequestKeepsId() {
        ReliefRequest request = testRequest();
        assertEquals(request.getId(), saveThenReloadRequest(request).getId());
    }

    @Test
    public void testSavedRequestKeepsSubject() {
        assertEquals("Need food", saveThenReloadRequest(testRequest()).getSubject());
    }

    @Test
    public void testSavedRequestKeepsStatus() {
        ReliefRequest request = testRequest();
        request.setStatus(ReliefRequestStatus.COMPLETE);
        assertEquals(ReliefRequestStatus.COMPLETE, saveThenReloadRequest(request).getStatus());
    }

    @Test
    public void testSavedRequestKeepsPeopleCount() {
        ReliefRequest request = testRequest();
        request.setPeopleCount(4);
        assertEquals(4, saveThenReloadRequest(request).getPeopleCount());
    }

    @Test
    public void testSavedRequestKeepsMedicalNote() {
        ReliefRequest request = testRequest();
        request.setMedical(true, "Needs insulin");
        assertEquals("Needs insulin", saveThenReloadRequest(request).getMedicalNote());
    }

    @Test
    public void testSavedRequestKeepsPets() {
        ReliefRequest request = testRequest();
        request.addPet(new Pet("Rex", "dog"));
        assertEquals(1, saveThenReloadRequest(request).getPets().size());
    }

    @Test
    public void testSavedRequestKeepsDestinationShelter() {
        ReliefRequest request = testRequest();
        request.setDestinationShelterId("shelter-test");
        assertEquals("shelter-test", saveThenReloadRequest(request).getDestinationShelterId());
    }

    @Test
    public void testSavedRequestKeepsRequester() {
        saveUser(victim);
        assertEquals("victimone", saveThenReloadRequest(testRequest()).getRequester().getUsername());
    }

    @Test
    public void testRequesterMissingFromUsersFileIsNullAfterReload() {
        assertNull(saveThenReloadRequest(testRequest()).getRequester());
    }

    @Test
    public void testSavedRequestKeepsAssignedVolunteer() {
        Volunteer volunteer = new Volunteer("volunteerone", "password", "803-111-1111", location, new ArrayList<Skill>());
        saveUser(volunteer);
        ReliefRequest request = testRequest();
        request.setAssignedVolunteer(volunteer);
        assertEquals("volunteerone", saveThenReloadRequest(request).getAssignedVolunteer().getUsername());
    }

    @Test
    public void testUnassignedRequestHasNoVolunteerAfterReload() {
        assertNull(saveThenReloadRequest(testRequest()).getAssignedVolunteer());
    }
}
