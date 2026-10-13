package com.hurricane.model;

import java.util.ArrayList;

public class Victim extends User {
	private ArrayList<ReliefRequest> activeRequests;
	private Hurricane affectedByHurricane;

	public Victim(String username, String password, String phoneNumber, Location location) {
		super(username, password, phoneNumber, location);
		this.activeRequests = new ArrayList<ReliefRequest>();
	}

	// Requests this victim has made that are still OPEN or IN_PROGRESS
	public ArrayList<ReliefRequest> getActiveRequests() {
		return activeRequests;
	}

	// The storm affecting this victim (null if none); set by the loader from hurricanes.json
	public Hurricane getAffectedByHurricane() {
		return affectedByHurricane;
	}

	public void setAffectedByHurricane(Hurricane hurricane) {
		this.affectedByHurricane = hurricane;
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
