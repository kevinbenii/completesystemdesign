package com.model;

import java.util.ArrayList;

public class Victim extends User {
	private ArrayList<ReliefRequest> activeRequests;
	private Hurricane affectedByHurricane;

	public Victim(String username, String password, String phoneNumber, Location location) {
		super(username, password, phoneNumber, location);
		this.activeRequests = new ArrayList<ReliefRequest>();
	}

	public ReliefRequest createRequest(String subject, String body, ArrayList<Skill> requiredSkills) {
		ReliefRequest request = new ReliefRequest(location, subject, body, requiredSkills, this);
		activeRequests.add(request);
		return request;
	}

	public ReliefRequestStatus viewRequestStatus(ReliefRequest request) {
		return ReliefRequestStatus.OPEN;
	}
}
