package com.hurricane.model;

import java.util.ArrayList;

public class Volunteer extends User {
	private ArrayList<Skill> skills;

	public Volunteer(String username, String password, String phoneNumber, Location location, ArrayList<Skill> skills) {
		super(username, password, phoneNumber, location);
		this.skills = skills;
	}

	// The kinds of help this volunteer can give, e.g. FOOD or TRANSPORTATION
	public ArrayList<Skill> getSkills() {
		return skills;
	}

	public void addSkill(Skill skill) {
		skills.add(skill);
	}

	// Takes the request: records this volunteer on it and marks it IN_PROGRESS
	public void volunteerForRequest(ReliefRequest request) {
		request.setAssignedVolunteer(this);
		request.setStatus(ReliefRequestStatus.IN_PROGRESS);
	}

	public void completeRequest(ReliefRequest request) {
		request.setStatus(ReliefRequestStatus.RESOLVED);
	}
}
