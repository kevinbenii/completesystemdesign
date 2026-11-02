package com.hurricane.model;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

// Tests for creating accounts, logging in and banning users through the Server.
// Author: Max Vogel
public class ServerAccountTest extends ServerTestBase {
    private User findSavedUser(String username) {
        for (User user : newDataManager().loadUsers()) {
            if (user.getUsername().equals(username))
                return user;
        }
        return null;
    }

    @Test
    public void testCreateVictimAccountWorks() {
        assertTrue(server.createVictimAccount("victimtwo", "password", "803-222-3333", location));
    }

    @Test
    public void testCreateVictimAccountWithBlankUsernameFails() {
        assertFalse(server.createVictimAccount(" ", "password", "803-222-3333", location));
    }

    @Test
    public void testCreateVictimAccountWithNullPasswordFails() {
        assertFalse(server.createVictimAccount("victimtwo", null, "803-222-3333", location));
    }

    @Test
    public void testCreateAccountWithUsernameInOtherCaseFails() {
        server.createVictimAccount("victimtwo", "password", "803-222-3333", location);
        assertFalse(server.createVictimAccount("VICTIMTWO", "password", "803-222-3333", location));
    }

    @Test
    public void testCreateFamilyAccountAddsFamilyMembers() {
        ArrayList<FamilyMember> family = new ArrayList<FamilyMember>();
        family.add(new FamilyMember("Dana", 40, ""));
        family.add(new FamilyMember("Eli", 12, ""));
        server.createFamilyAccount("familyone", "password", "803-222-3333", location, family, new ArrayList<Pet>());
        server.login("familyone", "password");
        assertEquals(2, ((Victim) server.getCurrentUser()).getFamilyMembers().size());
    }

    @Test
    public void testCreateFamilyAccountAddsPets() {
        ArrayList<Pet> pets = new ArrayList<Pet>();
        pets.add(new Pet("Rex", "dog"));
        server.createFamilyAccount("familyone", "password", "803-222-3333", location, new ArrayList<FamilyMember>(), pets);
        server.login("familyone", "password");
        assertEquals(1, ((Victim) server.getCurrentUser()).getPets().size());
    }

    @Test
    public void testCreateFamilyAccountWithBlankPasswordFails() {
        assertFalse(server.createFamilyAccount("familyone", " ", "803-222-3333", location,
                new ArrayList<FamilyMember>(), new ArrayList<Pet>()));
    }

    @Test
    public void testCreateVolunteerAccountKeepsSkills() {
        server.createVolunteerAccount("helperone", "password", "803-111-1111", location, skills(Skill.MEDICAL));
        server.login("helperone", "password");
        assertTrue(((Volunteer) server.getCurrentUser()).getSkills().contains(Skill.MEDICAL));
    }

    @Test
    public void testCreateVolunteerAccountWithBlankUsernameFails() {
        assertFalse(server.createVolunteerAccount(" ", "password", "803-111-1111", location, skills(Skill.MEDICAL)));
    }

    // An empty folder starts with sample users, so look for this one by name
    @Test
    public void testRegisteredUserIsSavedToFile() {
        server.registerUser(victim);
        assertEquals("victimone", findSavedUser("victimone").getUsername());
    }

    @Test
    public void testIsLoggedInIsFalseWhenNobodyLoggedIn() {
        assertFalse(server.isLoggedIn());
    }

    @Test
    public void testLoginSetsCurrentUser() {
        loginAs(victim);
        assertSame(victim, server.getCurrentUser());
    }

    @Test
    public void testLoginWithWrongPasswordFails() {
        server.registerUser(victim);
        assertFalse(server.login("victimone", "wrongpassword"));
    }

    @Test
    public void testLogoutClearsCurrentUser() {
        loginAs(victim);
        server.logout();
        assertNull(server.getCurrentUser());
    }

    @Test
    public void testAdminBansUser() {
        server.registerUser(victim);
        loginAs(admin);
        assertTrue(server.banUser(victim));
    }

    @Test
    public void testBannedUserIsSavedToFile() {
        server.registerUser(victim);
        loginAs(admin);
        server.banUser(victim);
        assertTrue(findSavedUser("victimone").isBanned());
    }

    @Test
    public void testVictimCannotBanUser() {
        server.registerUser(volunteer);
        loginAs(victim);
        assertFalse(server.banUser(volunteer));
    }

    @Test
    public void testBanNullUserFails() {
        loginAs(admin);
        assertFalse(server.banUser(null));
    }
}
