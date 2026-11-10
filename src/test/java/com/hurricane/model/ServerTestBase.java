package com.hurricane.model;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertTrue;

// Setup shared by the tests that need a Server: a fresh one on an empty temp folder for every test.
// Author: Max Vogel
public abstract class ServerTestBase {
    protected Path testFolder;
    protected Server server;
    protected Location location;
    protected Location farAway;

    protected Victim victim;
    protected Volunteer volunteer;
    protected Admin admin;
    protected ShelterStaff staff;

    // Server is a singleton, so clear it to give each test its own server and folder
    private void clearServer() throws ReflectiveOperationException {
        Field instance = Server.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    protected FileDataManager newDataManager() {
        return new FileDataManager(testFolder.toString());
    }

    protected void loginAs(User user) {
        server.registerUser(user);
        assertTrue(server.login(user.getUsername(), "password"));
    }

    protected ArrayList<Skill> skills(Skill skill) {
        ArrayList<Skill> skills = new ArrayList<Skill>();
        skills.add(skill);
        return skills;
    }

    protected ReliefRequest testRequest(Location requestLocation, Skill skill) {
        return new ReliefRequest(requestLocation, "Need help", "Please help", skills(skill), victim);
    }

    protected ReliefRequest testRequest() {
        return testRequest(location, Skill.FOOD);
    }

    // Empty folder for every test, so the real json files are never read or changed
    @Before
    public void setupServer() throws IOException, ReflectiveOperationException {
        testFolder = Files.createTempDirectory("servertest");
        clearServer();
        server = Server.getInstance(newDataManager());

        location = new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
        // About 414 miles north of location
        farAway = new Location("1 Far Road", "Far", "VA", "24000", 40.0, -81.0);

        victim = new Victim("victimone", "password", "803-222-3333", location);
        volunteer = new Volunteer("volunteerone", "password", "803-111-1111", location, skills(Skill.FOOD));
        admin = new Admin("adminone", "password", "803-333-3333", location);
        staff = new ShelterStaff("staffone", "password", "803-444-4444", location, "shelter-test");
    }

    // Clear the singleton again so no other test class is left using the deleted folder
    @After
    public void tearDownServer() throws IOException, ReflectiveOperationException {
        clearServer();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(testFolder)) {
            for (Path file : files)
                Files.deleteIfExists(file);
        }
        Files.deleteIfExists(testFolder);
    }
}
