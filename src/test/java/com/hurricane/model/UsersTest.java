package com.hurricane.model;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UsersTest {
    private Location testLocation() {
        return new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
    }

    // Server is a singleton, so make sure nobody stays logged in between tests
    @After
    public void logoutAfterEachTest() {
        Server.getInstance().logout();
    }

    @Test
    public void testLoginValidCredentialsReturnsTrue() {
        Server server = Server.getInstance();
        boolean success = server.login("volunteer1", "password123");
        assertTrue(success);
    }

    @Test
    public void testLoginValidCredentialsSetsCurrentUser() {
        Server server = Server.getInstance();
        server.login("volunteer1", "password123");
        String username = server.getCurrentUser().getUsername();
        assertEquals("volunteer1", username);
    }

    @Test
    public void testLoginUnknownUsername() {
        Server server = Server.getInstance();
        boolean success = server.login("bross", "password123");
        assertFalse(success);
    }

    @Test
    public void testLoginWrongPassword() {
        Server server = Server.getInstance();
        boolean success = server.login("volunteer1", "wrongPassword");
        assertFalse(success);
    }

    @Test
    public void testLogoutClearsCurrentUser() {
        Server server = Server.getInstance();
        server.login("volunteer1", "password123");
        server.logout();
        assertNull(server.getCurrentUser());
    }

    @Test
    public void testCreateVictimAccountCanLogInAfterwards() {
        Server server = Server.getInstance();
        server.createVictimAccount("jmath", "password", "803-222-3333", testLocation());
        server.logout();
        server.login("jmath", "password");
        String phoneNumber = server.getCurrentUser().getPhoneNumber();
        assertEquals("803-222-3333", phoneNumber);
    }

    @Test
    public void testCreateVictimAccountDuplicateUsername() {
        Server server = Server.getInstance();
        server.createVictimAccount("jmath", "password", "803-222-3333", testLocation());
        boolean success = server.createVictimAccount("jmath", "otherPassword", "803-222-3333", testLocation());
        assertFalse(success);
    }

    @Test
    public void testCreateVictimAccountBlankUsername() {
        Server server = Server.getInstance();
        boolean success = server.createVictimAccount("", "password", "803-222-3333", testLocation());
        assertFalse(success);
    }

    @Test
    public void testCreateVictimAccountNullUsername() {
        Server server = Server.getInstance();
        boolean success = server.createVictimAccount(null, "password", "803-222-3333", testLocation());
        assertFalse(success);
    }

    @Test
    public void testCreateVictimAccountBlankPassword() {
        Server server = Server.getInstance();
        boolean success = server.createVictimAccount("newvictim", "", "803-222-3333", testLocation());
        assertFalse(success);
    }

    @Test
    public void testCreateVictimAccountNullPassword() {
        Server server = Server.getInstance();
        boolean success = server.createVictimAccount("newvictim", null, "803-222-3333", testLocation());
        assertFalse(success);
    }
}
