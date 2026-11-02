package com.hurricane.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

// Tests for an admin moderating requests and banning users.
// Author: Max Vogel
public class AdminTest extends ServerTestBase {
    @Test
    public void testAdminIdStartsAsUsername() {
        assertEquals("adminone", admin.getAdminId());
    }

    @Test
    public void testDeleteRequestMarksItRemovedByModerator() {
        ReliefRequest request = testRequest();
        admin.deleteRequest(request);
        assertEquals(ReliefRequestStatus.MODERATOR_REMOVED, request.getStatus());
    }

    @Test
    public void testMarkAsResolvedChangesStatus() {
        ReliefRequest request = testRequest();
        admin.markAsResolved(request);
        assertEquals(ReliefRequestStatus.RESOLVED, request.getStatus());
    }

    @Test
    public void testLoggedInAdminBansUser() {
        server.registerUser(victim);
        loginAs(admin);
        assertTrue(admin.banUser(victim));
    }

    @Test
    public void testBannedUserCannotLogIn() {
        server.registerUser(victim);
        loginAs(admin);
        admin.banUser(victim);
        assertFalse(server.login("victimone", "password"));
    }

    @Test
    public void testLoggedOutAdminCannotBanUser() {
        server.registerUser(victim);
        assertFalse(admin.banUser(victim));
    }

    @Test
    public void testBanNullUserFails() {
        loginAs(admin);
        assertFalse(admin.banUser(null));
    }

    @Test
    public void testAdminWhoBansThemselvesIsLoggedOut() {
        loginAs(admin);
        admin.banUser(admin);
        assertFalse(server.isLoggedIn());
    }
}
