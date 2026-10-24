package com.hurricane.model;

import java.util.ArrayList;
import java.util.UUID;

/** Victim help request */
public class ReliefRequest {
	private UUID id;
	private Location location;
	private ReliefRequestStatus status;
	private String subject;
	private String body;
	private ArrayList<Skill> requiredSkills;
	private Victim requester;
	private Volunteer assignedVolunteer;
	private int peopleCount;
	private boolean needsMedical;
	private String medicalNote;
	private ArrayList<Pet> pets;

	/** Makes an open request */
	public ReliefRequest(Location location, String subject, String body, ArrayList<Skill> requiredSkills, Victim requester) {
		this.id = UUID.randomUUID();
		this.location = location;
		this.subject = subject;
		this.body = body;
		this.requiredSkills = requiredSkills;
		this.requester = requester;
		this.status = ReliefRequestStatus.OPEN;
		this.peopleCount = 1;
		this.medicalNote = "";
		this.pets = new ArrayList<Pet>();
	}

	/** Id */
	public UUID getId() {
		return id;
	}

	/** Restores a saved id */
	public void setId(UUID id) {
		this.id = id;
	}

	/** Location */
	public Location getLocation() {
		return location;
	}

	/** Status */
	public ReliefRequestStatus getStatus() {
		return status;
	}

	/** Subject */
	public String getSubject() {
		return subject;
	}

	/** Body */
	public String getBody() {
		return body;
	}

	/** Skills needed */
	public ArrayList<Skill> getRequiredSkills() {
		return requiredSkills;
	}

	/** Victim who asked */
	public Victim getRequester() {
		return requester;
	}

	/** Assigned volunteer, or null */
	public Volunteer getAssignedVolunteer() {
		return assignedVolunteer;
	}

	/** Sets the volunteer */
	public void setAssignedVolunteer(Volunteer volunteer) {
		this.assignedVolunteer = volunteer;
	}

	/** Prints the request */
	public void display() {
		System.out.println(subject);
		System.out.println(body);
	}

	/** Within radius miles */
	public boolean isNear(Location targetLocation, double radius) {
		return location.distanceTo(targetLocation) <= radius;
	}

	/** True if it needs that skill */
	public boolean needsSkill(Skill skill) {
		return requiredSkills.contains(skill);
	}

	/** Sets the status */
	public void setStatus(ReliefRequestStatus status) {
		this.status = status;
	}

	// How many people need help
	public int getPeopleCount() {
		return peopleCount;
	}

	public void setPeopleCount(int peopleCount) {
		this.peopleCount = peopleCount;
	}

	// True if someone in the group needs medical care
	public boolean needsMedical() {
		return needsMedical;
	}

	// Describes the medical need; empty if none
	public String getMedicalNote() {
		return medicalNote;
	}

	// Records a medical need on the request
	public void setMedical(boolean needsMedical, String medicalNote) {
		this.needsMedical = needsMedical;
		this.medicalNote = medicalNote;
	}

	// Pets coming along with the group
	public ArrayList<Pet> getPets() {
		return pets;
	}

	// Adds a pet to the request
	public void addPet(Pet pet) {
		pets.add(pet);
	}
}
