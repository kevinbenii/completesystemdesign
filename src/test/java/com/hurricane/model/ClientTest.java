package com.hurricane.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

// Tests for the client logging in and passing requests to the Server.
// Author: Max Vogel
public class ClientTest extends ServerTestBase {
    private Client client;

    // The fresh server already exists, so the client shares it and not the real json folder
    @Before
    public void setupClient() {
        server.registerUser(victim);
        client = new Client();
    }

    @Test
    public void testAttemptLoginWithRightPasswordWorks() {
        assertTrue(client.attemptLogin("victimone", "password"));
    }

    @Test
    public void testAttemptLoginWithWrongPasswordFails() {
        assertFalse(client.attemptLogin("victimone", "wrongpassword"));
    }

    @Test
    public void testAttemptLoginSetsCurrentUser() {
        client.attemptLogin("victimone", "password");
        assertSame(victim, client.getCurrentUser());
    }

    @Test
    public void testLogoutAlsoLogsOutOfServer() {
        client.attemptLogin("victimone", "password");
        client.logout();
        assertFalse(server.isLoggedIn());
    }

    @Test
    public void testSubmitRequestReachesServer() {
        client.submitRequest(testRequest());
        assertEquals(1, server.getAllRequests().size());
    }

    @Test
    public void testFilterRequestsBySkillFindsMatch() {
        client.submitRequest(testRequest());
        assertEquals(1, client.filterRequestsBySkill(Skill.FOOD).size());
    }

    @Test
    public void testFilterRequestsBySkillLeavesOutOtherSkills() {
        client.submitRequest(testRequest());
        assertEquals(0, client.filterRequestsBySkill(Skill.MEDICAL).size());
    }

    @Test
    public void testFilterRequestsByLocationFindsNearbyRequest() {
        client.submitRequest(testRequest());
        assertEquals(1, client.filterRequestsByLocation(location, 100.0).size());
    }

    @Test
    public void testFilterRequestsByLocationLeavesOutFarRequest() {
        client.submitRequest(testRequest(farAway, Skill.FOOD));
        assertEquals(0, client.filterRequestsByLocation(location, 100.0).size());
    }
}
