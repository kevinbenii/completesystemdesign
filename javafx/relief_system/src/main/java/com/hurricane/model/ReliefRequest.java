package com.model;

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
