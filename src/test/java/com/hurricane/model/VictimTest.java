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
 * +----------------------------------------+----------------------------------------------+
 * | Test                                   | Reasoning                                    |
 * +----------------------------------------+----------------------------------------------+
 * | Add family member                      | The member should join the household         |
 * | Add null family member                 | Null should not enter the household          |
 * | Add pet                                | The pet should enter the list                |
 * | Add null pet                           | Null should not enter the list               |
 * | Create request adds to active requests | New requests should be tracked               |
 * | Create request sets requester          | The request should identify its creator      |
 * | Create request uses victim location    | The request should use the victim's location |
 * | Create request with empty subject      | Empty subjects should be rejected            |
 * | View new request status                | New requests should be open                  |
 * | View completed request status          | Finished requests should show complete       |
 * | View null request status               | A missing request should return null         |
 * +----------------------------------------+----------------------------------------------+
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
