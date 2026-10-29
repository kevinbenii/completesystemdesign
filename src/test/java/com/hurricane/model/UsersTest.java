package com.hurricane.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UsersTest {
    private Location testLocation() {
        return new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
    }

    @Test
    public void testTesting(){
        assertTrue(true);
    }

    @Test
    public void testValidLogin(){
        Server server = Server.getInstance();
        server.login("volunteer1", "password123");
        String username = server.getCurrentUser().getUsername();
        server.logout();

        assertEquals("volunteer1", username);
    }

    @Test
    public void testInValidLogin(){
        Server server = Server.getInstance();
        boolean success = server.login("bross", "password123");
        assertFalse(success);
    }

    @Test
    public void testWrongPassword(){
        Server server = Server.getInstance();
        boolean success = server.login("volunteer1", "wrongPassword");
        assertFalse(success);
    }

    @Test
    public void testLogout(){
        Server server = Server.getInstance();
        server.login("volunteer1", "password123");
        server.logout();
        assertNull(server.getCurrentUser());
    }

    @Test
    public void testAddValidUser() {
        Server server = Server.getInstance();
        server.createVictimAccount("jmath", "password", "803-222-3333", testLocation());
        server.logout();
        server = Server.getInstance();
        server.login("jmath", "password");
        String phoneNumber = server.getCurrentUser().getPhoneNumber();
        server.logout();

        assertEquals("803-222-3333", phoneNumber);
    }

    @Test
    public void testDuplicateValidUser() {
        Server server = Server.getInstance();
        server.createVictimAccount("jmath", "password", "803-222-3333", testLocation());
        boolean success = server.createVictimAccount("jmath", "otherPassword", "803-222-3333", testLocation());
        assertFalse(success);
    }

    @Test
    public void testBlankUsername() {
        Server server = Server.getInstance();
        boolean success = server.createVictimAccount("", "password", "803-222-3333", testLocation());
        assertFalse(success);
    }
}
