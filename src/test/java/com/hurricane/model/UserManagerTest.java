package com.hurricane.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for logging in and banning users.
 *
 * @author Jon Luu
 */
public class UserManagerTest {
    private Path emptyFolder;
    private UserManager userManager;

    private Location testLocation() {
        return new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
    }

    private Victim testVictim(String username) {
        return new Victim(username, "password", "803-222-3333", testLocation());
    }

    // Empty folder for every test, so the real json files are never read or changed.
    // An empty folder makes UserManager start with its sample users (volunteer1, victim1, admin1).
    @Before
    public void setup() throws IOException {
        emptyFolder = Files.createTempDirectory("usermanagertest");
        userManager = new UserManager(new FileDataManager(emptyFolder.toString()));
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(emptyFolder);
    }

    @Test
    public void testAuthenticateSampleVolunteerReturnsTrue() {
        assertTrue(userManager.authenticateUser("volunteer1", "password123"));
    }

    @Test
    public void testAuthenticateWrongPasswordReturnsFalse() {
        assertFalse(userManager.authenticateUser("volunteer1", "wrongPassword"));
    }

    @Test
    public void testBanUserWhenNotLoggedInReturnsFalse() {
        Victim victim = testVictim("victimone");
        userManager.registerUser(victim);
        assertFalse(userManager.banUser(victim));
    }

    @Test
    public void testBanUserAsVolunteerReturnsFalse() {
        Victim victim = testVictim("victimone");
        userManager.registerUser(victim);
        userManager.authenticateUser("volunteer1", "password123");
        assertFalse(userManager.banUser(victim));
    }

    @Test
    public void testBanUserAsAdminReturnsTrue() {
        Victim victim = testVictim("victimone");
        userManager.registerUser(victim);
        userManager.authenticateUser("admin1", "adminPassword123");
        assertTrue(userManager.banUser(victim));
    }

    @Test
    public void testBannedUserCannotLogIn() {
        Victim victim = testVictim("victimone");
        userManager.registerUser(victim);
        userManager.authenticateUser("admin1", "adminPassword123");
        userManager.banUser(victim);
        userManager.logout();
        assertFalse(userManager.authenticateUser("victimone", "password"));
    }

    @Test
    public void testBanNullUserReturnsFalse() {
        userManager.authenticateUser("admin1", "adminPassword123");
        assertFalse(userManager.banUser(null));
    }
}
