package com.hurricane.model;

import java.util.ArrayList;

public class ReliefRequest {
	private Location location;
	private ReliefRequestStatus status;
	private String subject;
	private String body;
	private ArrayList<Skill> requiredSkills;
	private Victim requester;
	private Volunteer assignedVolunteer;

	public ReliefRequest(Location location, String subject, String body, ArrayList<Skill> requiredSkills, Victim requester) {
		this.location = location;
		this.subject = subject;
		this.body = body;
		this.requiredSkills = requiredSkills;
		this.requester = requester;
		this.status = ReliefRequestStatus.OPEN;
	}

	// Getters for every field, used by the UI to display a request and by the writer to save it
	public Location getLocation() {
		return location;
	}

	public ReliefRequestStatus getStatus() {
		return status;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public ArrayList<Skill> getRequiredSkills() {
		return requiredSkills;
	}

	public Victim getRequester() {
		return requester;
	}

	// null until a volunteer takes the request
	public Volunteer getAssignedVolunteer() {
		return assignedVolunteer;
	}

	// Called by Volunteer.volunteerForRequest() and by the loader
	public void setAssignedVolunteer(Volunteer volunteer) {
		this.assignedVolunteer = volunteer;
	}

	public void display() {
		System.out.println(subject);
		System.out.println(body);
	}

	public boolean isNear(Location targetLocation, double radius) {
		return true;
	}

	public boolean needsSkill(Skill skill) {
		return requiredSkills.contains(skill);
	}

	public void setStatus(ReliefRequestStatus status) {
		this.status = status;
	}
}
