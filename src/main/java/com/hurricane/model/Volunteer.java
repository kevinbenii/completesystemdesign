package com.hurricane.model;

import java.util.ArrayList;

/** Person who helps */
public class Volunteer extends User {
	private ArrayList<Skill> skills;

	/** Makes a volunteer */
	public Volunteer(String username, String password, String phoneNumber, Location location, ArrayList<Skill> skills) {
		super(username, password, phoneNumber, location);
		this.skills = skills;
	}

	/** Their skills */
	public ArrayList<Skill> getSkills() {
		return skills;
	}

	/** Adds a skill */
	public void addSkill(Skill skill) {
		skills.add(skill);
	}

	/** Takes a request */
	public void volunteerForRequest(ReliefRequest request) {
		request.setAssignedVolunteer(this);
		request.setStatus(ReliefRequestStatus.ASSIGNED);
	}

	/** Marks a request resolved */
	public void completeRequest(ReliefRequest request) {
		request.setStatus(ReliefRequestStatus.RESOLVED);
	}
}
