package com.hurricane.model;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for what each kind of user is allowed to do.
 *
 * +-------------------------------------+-------------------------------------------------+
 * | Test                                | Reasoning                                       |
 * +-------------------------------------+-------------------------------------------------+
 * | admin can complete requests         | admins are allowed to complete requests         |
 * | shelter staff can complete requests | shelter staff are allowed to complete requests  |
 * | volunteer cannot complete requests  | volunteers are not allowed to complete requests |
 * | victim cannot complete requests     | victims are not allowed to complete requests    |
 * | shelter staff keeps shelter id      | staff have to stay linked to their shelter      |
 * | volunteer role name                 | each role has to report its own name            |
 * +-------------------------------------+-------------------------------------------------+
 *
 * @author Jon Luu
 */
public class UserRoleTest {
    private Location location;

    @Before
    public void setup() {
        location = new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
    }

    @Test
    public void testAdminCanCompleteRequests() {
        Admin admin = new Admin("adminone", "password", "803-333-3333", location);
        assertTrue(admin.canCompleteRequests());
    }

    @Test
    public void testShelterStaffCanCompleteRequests() {
        ShelterStaff staff = new ShelterStaff("staffone", "password", "803-444-4444", location, "shelter-1");
        assertTrue(staff.canCompleteRequests());
    }

    @Test
    public void testVolunteerCannotCompleteRequests() {
        Volunteer volunteer = new Volunteer("volunteerone", "password", "803-111-1111", location, new ArrayList<Skill>());
        assertFalse(volunteer.canCompleteRequests());
    }

    @Test
    public void testVictimCannotCompleteRequests() {
        Victim victim = new Victim("victimone", "password", "803-222-2222", location);
        assertFalse(victim.canCompleteRequests());
    }

    @Test
    public void testShelterStaffKeepsItsShelterId() {
        ShelterStaff staff = new ShelterStaff("staffone", "password", "803-444-4444", location, "shelter-1");
        assertEquals("shelter-1", staff.getShelterId());
    }

    @Test
    public void testRoleNameOfVolunteerIsVolunteer() {
        Volunteer volunteer = new Volunteer("volunteerone", "password", "803-111-1111", location, new ArrayList<Skill>());
        assertEquals("volunteer", volunteer.getRoleName());
    }
}
