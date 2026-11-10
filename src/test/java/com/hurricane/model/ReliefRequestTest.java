package com.hurricane.model;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

// Tests for a relief request's starting values, location and skill checks, and display.
//
// +--------------------------+---------------------------------+
// | Test                     | Reasoning                       |
// +--------------------------+---------------------------------+
// | new request status       | a new request starts OPEN       |
// | new request people count | a new request is for one person |
// | new request medical      | no medical need by default      |
// | new request medical note | the note starts empty, not null |
// | new request pets         | a new request has no pets       |
// | near, inside radius      | a nearby location is near       |
// | near, outside radius     | a far location is not near      |
// | skill that was asked for | the required skill matches      |
// | skill not asked for      | other skills don't match        |
// | null skill               | null is never needed, no crash  |
// | set medical              | the note has to be kept         |
// | add pet                  | the pet joins the request       |
// | display subject          | the subject has to be printed   |
// | display body             | the body has to be printed      |
// +--------------------------+---------------------------------+
//
// Author: Max Vogel
public class ReliefRequestTest {
    private Location location;
    private Location farAway;
    private ReliefRequest request;

    @Before
    public void setup() {
        location = new Location("123 Test Street", "Columbia", "SC", "29201", 34.0, -81.0);
        // About 414 miles north of location
        farAway = new Location("1 Far Road", "Far", "VA", "24000", 40.0, -81.0);
        ArrayList<Skill> skills = new ArrayList<Skill>();
        skills.add(Skill.FOOD);
        Victim victim = new Victim("victimone", "password", "803-222-3333", location);
        request = new ReliefRequest(location, "Need food", "No food", skills, victim);
    }

    // Runs display() and returns what it printed
    private String displayedText() {
        PrintStream original = System.out;
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed));
        try {
            request.display();
        } finally {
            System.setOut(original);
        }
        return printed.toString();
    }

    @Test
    public void testNewRequestIsOpen() {
        assertEquals(ReliefRequestStatus.OPEN, request.getStatus());
    }

    @Test
    public void testNewRequestIsForOnePerson() {
        assertEquals(1, request.getPeopleCount());
    }

    @Test
    public void testNewRequestDoesNotNeedMedical() {
        assertFalse(request.needsMedical());
    }

    @Test
    public void testNewRequestHasEmptyMedicalNote() {
        assertEquals("", request.getMedicalNote());
    }

    @Test
    public void testNewRequestHasNoPets() {
        assertEquals(0, request.getPets().size());
    }

    @Test
    public void testIsNearInsideRadius() {
        assertTrue(request.isNear(location, 100.0));
    }

    @Test
    public void testIsNearOutsideRadius() {
        assertFalse(request.isNear(farAway, 100.0));
    }

    @Test
    public void testNeedsSkillThatWasAskedFor() {
        assertTrue(request.needsSkill(Skill.FOOD));
    }

    @Test
    public void testNeedsSkillThatWasNotAskedFor() {
        assertFalse(request.needsSkill(Skill.MEDICAL));
    }

    @Test
    public void testNeedsSkillWithNullSkillIsFalse() {
        assertFalse(request.needsSkill(null));
    }

    @Test
    public void testSetMedicalKeepsNote() {
        request.setMedical(true, "Needs insulin");
        assertEquals("Needs insulin", request.getMedicalNote());
    }

    @Test
    public void testAddPetAddsToPets() {
        request.addPet(new Pet("Rex", "dog"));
        assertEquals(1, request.getPets().size());
    }

    @Test
    public void testDisplayPrintsSubject() {
        assertTrue(displayedText().contains("Need food"));
    }

    @Test
    public void testDisplayPrintsBody() {
        assertTrue(displayedText().contains("No food"));
    }
}
