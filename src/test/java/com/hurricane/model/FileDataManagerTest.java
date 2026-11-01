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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for saving data to files and loading it back.
 *
 * @author Jon Luu
 */
public class FileDataManagerTest {
    private Path testFolder;
    private FileDataManager dataManager;

    private Location testLocation() {
        return new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
    }

    // Saves the users with one data manager, then loads them with a brand new one on the same folder
    private ArrayList<User> saveThenReload(ArrayList<User> users) {
        dataManager.saveUsers(users);
        return new FileDataManager(testFolder.toString()).loadUsers();
    }

    private User findUser(ArrayList<User> users, String username) {
        for (User user : users) {
            if (user.getUsername().equals(username))
                return user;
        }
        return null;
    }

    // Empty folder for every test, so the real json files are never read or changed
    @Before
    public void setup() throws IOException {
        testFolder = Files.createTempDirectory("filedatamanagertest");
        dataManager = new FileDataManager(testFolder.toString());
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
    public void testSavedUserIsLoadedAgain() {
        ArrayList<User> users = new ArrayList<User>();
        users.add(new Victim("victimone", "password", "803-222-3333", testLocation()));
        ArrayList<User> loaded = saveThenReload(users);
        assertNotNull(findUser(loaded, "victimone"));
    }

    @Test
    public void testSavedUserKeepsPhoneNumber() {
        ArrayList<User> users = new ArrayList<User>();
        users.add(new Victim("victimone", "password", "803-222-3333", testLocation()));
        ArrayList<User> loaded = saveThenReload(users);
        assertEquals("803-222-3333", findUser(loaded, "victimone").getPhoneNumber());
    }

    @Test
    public void testSavedVolunteerKeepsSkills() {
        ArrayList<Skill> skills = new ArrayList<Skill>();
        skills.add(Skill.FOOD);
        ArrayList<User> users = new ArrayList<User>();
        users.add(new Volunteer("volunteerone", "password", "803-111-1111", testLocation(), skills));
        ArrayList<User> loaded = saveThenReload(users);
        Volunteer volunteer = (Volunteer) findUser(loaded, "volunteerone");
        assertTrue(volunteer.getSkills().contains(Skill.FOOD));
    }

    @Test
    public void testSavedBannedUserIsStillBannedAfterReload() {
        Victim victim = new Victim("victimone", "password", "803-222-3333", testLocation());
        victim.setBanned(true);
        ArrayList<User> users = new ArrayList<User>();
        users.add(victim);
        ArrayList<User> loaded = saveThenReload(users);
        assertTrue(findUser(loaded, "victimone").isBanned());
    }
}
