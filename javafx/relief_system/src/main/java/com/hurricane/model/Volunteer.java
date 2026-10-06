package com.hurricane.model;

import java.util.ArrayList;

public class Volunteer extends User {
	private ArrayList<Skill> skills;

	public Volunteer(String username, String password, String phoneNumber, Location location, ArrayList<Skill> skills) {
		super(username, password, phoneNumber, location);
		this.skills = skills;
	}

	public void addSkill(Skill skill) {
		skills.add(skill);
	}

	public void volunteerForRequest(ReliefRequest request) {
		request.setStatus(ReliefRequestStatus.IN_PROGRESS);
	}

	public void completeRequest(ReliefRequest request) {
		request.setStatus(ReliefRequestStatus.RESOLVED);
	}
}
