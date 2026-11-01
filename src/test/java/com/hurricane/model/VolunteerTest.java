package com.hurricane.model;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Tests for a volunteer taking and finishing relief requests.
 *
 * @author Jon Luu
 */
public class VolunteerTest {
    private Volunteer volunteer;
    private ReliefRequest request;

    @Before
    public void setup() {
        Location location = new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
        volunteer = new Volunteer("volunteerone", "password", "803-111-1111", location, new ArrayList<Skill>());
        Victim victim = new Victim("victimone", "password", "803-222-3333", location);
        request = victim.createRequest("Need food", "No food", new ArrayList<Skill>());
    }

    @Test
    public void testVolunteerForRequestAssignsVolunteer() {
        volunteer.volunteerForRequest(request);
        assertSame(volunteer, request.getAssignedVolunteer());
    }

    @Test
    public void testVolunteerForRequestSetsStatusToAssigned() {
        volunteer.volunteerForRequest(request);
        assertEquals(ReliefRequestStatus.ASSIGNED, request.getStatus());
    }

    @Test
    public void testVolunteerForCompletedRequestKeepsStatusComplete() {
        request.setStatus(ReliefRequestStatus.COMPLETE);
        volunteer.volunteerForRequest(request);
        assertEquals(ReliefRequestStatus.COMPLETE, request.getStatus());
    }
}
