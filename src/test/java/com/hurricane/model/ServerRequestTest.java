package com.hurricane.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

// Tests for submitting, completing, taking and recommending requests through the Server.
// Author: Max Vogel
public class ServerRequestTest extends ServerTestBase {
    @Test
    public void testSubmitRequestWhileLoggedOutFails() {
        assertFalse(server.submitRequest("Need food", "No food", skills(Skill.FOOD)));
    }

    @Test
    public void testVolunteerCannotSubmitRequest() {
        loginAs(volunteer);
        assertFalse(server.submitRequest("Need food", "No food", skills(Skill.FOOD)));
    }

    @Test
    public void testVictimSubmitsRequest() {
        loginAs(victim);
        server.submitRequest("Need food", "No food", skills(Skill.FOOD));
        assertEquals(1, server.getAllRequests().size());
    }

    @Test
    public void testSubmittedRequestIsSavedToFile() {
        server.submitRequest(testRequest());
        assertEquals(1, newDataManager().loadRequests().size());
    }

    @Test
    public void testSubmitFamilyRequestWhileLoggedOutReturnsNull() {
        assertNull(server.submitFamilyRequest("Need food", "No food", skills(Skill.FOOD), ""));
    }

    @Test
    public void testSubmitFamilyRequestCountsFamily() {
        victim.addFamilyMember(new FamilyMember("Dana", 40, ""));
        victim.addFamilyMember(new FamilyMember("Eli", 12, ""));
        loginAs(victim);
        ReliefRequest request = server.submitFamilyRequest("Need food", "No food", skills(Skill.FOOD), "");
        assertEquals(2, request.getPeopleCount());
    }

    @Test
    public void testSubmitFamilyRequestWithNoFamilyCountsOnePerson() {
        loginAs(victim);
        ReliefRequest request = server.submitFamilyRequest("Need food", "No food", skills(Skill.FOOD), "");
        assertEquals(1, request.getPeopleCount());
    }

    @Test
    public void testSubmitFamilyRequestCopiesPets() {
        victim.addPet(new Pet("Rex", "dog"));
        loginAs(victim);
        ReliefRequest request = server.submitFamilyRequest("Need food", "No food", skills(Skill.FOOD), "");
        assertEquals(1, request.getPets().size());
    }

    @Test
    public void testSubmitFamilyRequestWithNoteNeedsMedical() {
        loginAs(victim);
        ReliefRequest request = server.submitFamilyRequest("Need food", "No food", skills(Skill.FOOD), "Insulin");
        assertTrue(request.needsMedical());
    }

    @Test
    public void testSubmitFamilyRequestWithNullNoteDoesNotNeedMedical() {
        loginAs(victim);
        ReliefRequest request = server.submitFamilyRequest("Need food", "No food", skills(Skill.FOOD), null);
        assertFalse(request.needsMedical());
    }

    @Test
    public void testDeleteRequestRemovesIt() {
        ReliefRequest request = testRequest();
        server.submitRequest(request);
        server.deleteRequest(request);
        assertEquals(0, server.getAllRequests().size());
    }

    @Test
    public void testMarkAsResolvedChangesStatus() {
        ReliefRequest request = testRequest();
        server.submitRequest(request);
        server.markAsResolved(request);
        assertEquals(ReliefRequestStatus.RESOLVED, request.getStatus());
    }

    @Test
    public void testFilterRequestsBySkillFindsMatch() {
        server.submitRequest(testRequest());
        assertEquals(1, server.filterRequestsBySkill(Skill.FOOD).size());
    }

    @Test
    public void testFilterRequestsByLocationLeavesOutFarRequest() {
        server.submitRequest(testRequest(farAway, Skill.FOOD));
        assertEquals(0, server.filterRequestsByLocation(location, 100.0).size());
    }

    @Test
    public void testCompleteRequestWhileLoggedOutFails() {
        assertFalse(server.completeRequest(testRequest()));
    }

    @Test
    public void testVictimCannotCompleteRequest() {
        loginAs(victim);
        assertFalse(server.completeRequest(testRequest()));
    }

    @Test
    public void testAdminCompletesRequest() {
        ReliefRequest request = testRequest();
        server.submitRequest(request);
        loginAs(admin);
        server.completeRequest(request);
        assertEquals(ReliefRequestStatus.COMPLETE, request.getStatus());
    }

    @Test
    public void testCompleteNullRequestReturnsFalse() {
        loginAs(admin);
        assertFalse(server.completeRequest(null));
    }

    @Test
    public void testVictimCannotVolunteerForRequest() {
        loginAs(victim);
        assertFalse(server.volunteerForRequest(testRequest()));
    }

    @Test
    public void testVolunteerTakesRequest() {
        ReliefRequest request = testRequest();
        loginAs(volunteer);
        server.volunteerForRequest(request);
        assertSame(volunteer, request.getAssignedVolunteer());
    }

    @Test
    public void testVolunteerForNullRequestReturnsFalse() {
        loginAs(volunteer);
        assertFalse(server.volunteerForRequest(null));
    }

    @Test
    public void testSecondVolunteerCannotTakeAssignedRequest() {
        ReliefRequest request = testRequest();
        request.setAssignedVolunteer(new Volunteer("volunteertwo", "password", "803-555-5555", location, skills(Skill.FOOD)));
        request.setStatus(ReliefRequestStatus.ASSIGNED);
        loginAs(volunteer);
        assertFalse(server.volunteerForRequest(request));
    }

    @Test
    public void testRecommendRequestsForVictimIsEmpty() {
        server.submitRequest(testRequest());
        loginAs(victim);
        assertEquals(0, server.recommendRequests().size());
    }

    @Test
    public void testRecommendRequestsPutsNearbySkillMatchFirst() {
        ReliefRequest nearbyMatch = testRequest();
        server.submitRequest(testRequest(farAway, Skill.MEDICAL));
        server.submitRequest(nearbyMatch);
        loginAs(volunteer);
        assertSame(nearbyMatch, server.recommendRequests().get(0));
    }

    @Test
    public void testRecommendRequestsSortsOthersByDistance() {
        ReliefRequest near = testRequest(location, Skill.MEDICAL);
        server.submitRequest(testRequest(farAway, Skill.MEDICAL));
        server.submitRequest(near);
        loginAs(volunteer);
        assertSame(near, server.recommendRequests().get(0));
    }

    @Test
    public void testRecommendRequestsLeavesOutTakenRequests() {
        ReliefRequest request = testRequest();
        request.setStatus(ReliefRequestStatus.ASSIGNED);
        server.submitRequest(request);
        loginAs(volunteer);
        assertEquals(0, server.recommendRequests().size());
    }

    @Test
    public void testRecommendationReasonNamesTheSkill() {
        loginAs(volunteer);
        assertTrue(server.getRecommendationReason(testRequest()).contains("FOOD"));
    }

    @Test
    public void testRecommendationReasonIsEmptyWithoutSkillMatch() {
        loginAs(volunteer);
        assertEquals("", server.getRecommendationReason(testRequest(location, Skill.MEDICAL)));
    }

    @Test
    public void testRecommendationReasonIsEmptyWhenTooFarAway() {
        loginAs(volunteer);
        assertEquals("", server.getRecommendationReason(testRequest(farAway, Skill.FOOD)));
    }
}
