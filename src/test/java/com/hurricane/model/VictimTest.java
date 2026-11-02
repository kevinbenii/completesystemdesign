package com.hurricane.model;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests for a victim's household and relief requests.
 *
 * AI was used to write the test code. Test list and reasoning:
 * +--------------------------------+-------------------------------------------------------------------+
 * | Test                           | Reasoning                                                         |
 * +--------------------------------+-------------------------------------------------------------------+
 * |                                |                                                                   |
 * +--------------------------------+-------------------------------------------------------------------+
 *
 * @author Kevin Beni
 */
public class VictimTest {
    private Location location;
    private Victim victim;

    @Before
    public void setup() {
        location = new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
        victim = new Victim("victimone", "password", "803-222-3333", location);
    }

    @Test
    public void testAddFamilyMemberAddsToHousehold() {
        victim.addFamilyMember(new FamilyMember("Rosa", 40, ""));
        assertEquals(1, victim.getFamilyMembers().size());
    }

    @Test
    public void testAddNullFamilyMemberIsIgnored() {
        victim.addFamilyMember(null);
        assertEquals(0, victim.getFamilyMembers().size());
    }

    @Test
    public void testAddPetAddsToPets() {
        victim.addPet(new Pet("Buddy", "Dog"));
        assertEquals(1, victim.getPets().size());
    }

    @Test
    public void testAddNullPetIsIgnored() {
        victim.addPet(null);
        assertEquals(0, victim.getPets().size());
    }

    @Test
    public void testCreateRequestAddsToActiveRequests() {
        victim.createRequest("Need food", "No food", new ArrayList<Skill>());
        assertEquals(1, victim.getActiveRequests().size());
    }

    @Test
    public void testCreateRequestSetsVictimAsRequester() {
        ReliefRequest request = victim.createRequest("Need food", "No food", new ArrayList<Skill>());
        assertSame(victim, request.getRequester());
    }

    @Test
    public void testCreateRequestUsesVictimLocation() {
        ReliefRequest request = victim.createRequest("Need food", "No food", new ArrayList<Skill>());
        assertSame(location, request.getLocation());
    }

    @Test
    public void testCreateRequestEmptySubjectIsNotAdded() {
        victim.createRequest("", "No food", new ArrayList<Skill>());
        assertEquals(0, victim.getActiveRequests().size());
    }

    @Test
    public void testViewRequestStatusNewRequestIsOpen() {
        ReliefRequest request = victim.createRequest("Need food", "No food", new ArrayList<Skill>());
        assertEquals(ReliefRequestStatus.OPEN, victim.viewRequestStatus(request));
    }

    @Test
    public void testViewRequestStatusShowsComplete() {
        ReliefRequest request = victim.createRequest("Need food", "No food", new ArrayList<Skill>());
        request.setStatus(ReliefRequestStatus.COMPLETE);
        assertEquals(ReliefRequestStatus.COMPLETE, victim.viewRequestStatus(request));
    }

    @Test
    public void testViewRequestStatusNullRequestIsNull() {
        assertNull(victim.viewRequestStatus(null));
    }
}
