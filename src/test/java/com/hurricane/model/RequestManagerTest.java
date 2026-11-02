package com.hurricane.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Tests for submitting, completing and filtering relief requests.
 *
 * +-----------------------------------+----------------------------------------------------------+
 * | Test                              | Reasoning                                                |
 * +-----------------------------------+----------------------------------------------------------+
 * | new manager is empty              | a new manager with no saved data starts with no requests |
 * | submit a request                  | a submitted request has to show up in the list           |
 * | complete a request                | completing a request changes its status to COMPLETE      |
 * | open requests leave out completed | finished requests should not show as open                |
 * | filter by skill, match            | only requests that need the skill come back              |
 * | filter by skill, no match         | no match returns an empty list, not an error             |
 * | submit null request               | null should not be added to the list                     |
 * | filter by location, nearby        | a request close to the location is included              |
 * | filter by location, far away      | a request far outside the radius is left out             |
 * | mark as resolved                  | marking resolved changes the status to RESOLVED          |
 * +-----------------------------------+----------------------------------------------------------+
 *
 * @author Jon Luu
 */
public class RequestManagerTest {
    private Path emptyFolder;
    private RequestManager requestManager;
    private Victim victim;

    private Location testLocation() {
        return new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
    }

    private ReliefRequest testRequest(String subject, Skill skill) {
        ArrayList<Skill> skills = new ArrayList<Skill>();
        skills.add(skill);
        return new ReliefRequest(testLocation(), subject, "Details", skills, victim);
    }

    // Empty folder for every test, so the real json files are never read or changed
    @Before
    public void setup() throws IOException {
        emptyFolder = Files.createTempDirectory("requestmanagertest");
        requestManager = new RequestManager(new FileDataManager(emptyFolder.toString()));
        victim = new Victim("victimone", "password", "803-222-3333", testLocation());
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(emptyFolder);
    }

    @Test
    public void testNewManagerHasNoRequests() {
        assertEquals(0, requestManager.getAllRequests().size());
    }

    @Test
    public void testSubmitRequestAddsToAllRequests() {
        requestManager.submitRequest(testRequest("Need food", Skill.FOOD));
        assertEquals(1, requestManager.getAllRequests().size());
    }

    @Test
    public void testCompleteRequestSetsStatusToComplete() {
        ReliefRequest request = testRequest("Need food", Skill.FOOD);
        requestManager.submitRequest(request);
        requestManager.completeRequest(request);
        assertEquals(ReliefRequestStatus.COMPLETE, request.getStatus());
    }

    @Test
    public void testGetOpenRequestsLeavesOutCompletedRequest() {
        ReliefRequest done = testRequest("Need food", Skill.FOOD);
        requestManager.submitRequest(done);
        requestManager.submitRequest(testRequest("Need a ride", Skill.TRANSPORTATION));
        requestManager.completeRequest(done);
        assertEquals(1, requestManager.getOpenRequests().size());
    }

    @Test
    public void testFilterBySkillReturnsOnlyMatchingRequest() {
        ReliefRequest food = testRequest("Need food", Skill.FOOD);
        requestManager.submitRequest(food);
        requestManager.submitRequest(testRequest("Need a ride", Skill.TRANSPORTATION));
        ArrayList<ReliefRequest> matches = requestManager.filterBySkill(Skill.FOOD);
        assertSame(food, matches.get(0));
    }

    @Test
    public void testFilterBySkillNoMatchReturnsEmptyList() {
        requestManager.submitRequest(testRequest("Need food", Skill.FOOD));
        assertEquals(0, requestManager.filterBySkill(Skill.MEDICAL).size());
    }

    @Test
    public void testSubmitNullRequestIsNotAdded() {
        requestManager.submitRequest(null);
        assertEquals(0, requestManager.getAllRequests().size());
    }

    @Test
    public void testFilterByLocationReturnsNearbyRequest() {
        ReliefRequest nearby = testRequest("Need food", Skill.FOOD);
        requestManager.submitRequest(nearby);
        Location sameTown = new Location("500 Other Street", "Columbia", "SC", "29201", 34.01, -81.01);
        assertSame(nearby, requestManager.filterByLocation(sameTown, 10).get(0));
    }

    @Test
    public void testFilterByLocationLeavesOutFarAwayRequest() {
        requestManager.submitRequest(testRequest("Need food", Skill.FOOD));
        Location newYork = new Location("1 Broadway", "New York", "NY", "10004", 40.7, -74.0);
        assertEquals(0, requestManager.filterByLocation(newYork, 10).size());
    }

    @Test
    public void testMarkAsResolvedSetsStatusToResolved() {
        ReliefRequest request = testRequest("Need food", Skill.FOOD);
        requestManager.submitRequest(request);
        requestManager.markAsResolved(request);
        assertEquals(ReliefRequestStatus.RESOLVED, request.getStatus());
    }
}
