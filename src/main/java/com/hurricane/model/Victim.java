package com.hurricane.model;

import java.util.ArrayList;

public class Victim extends User {
	private ArrayList<ReliefRequest> activeRequests;
	private Hurricane affectedByHurricane;
	private ArrayList<FamilyMember> familyMembers;

	public Victim(String username, String password, String phoneNumber, Location location) {
		super(username, password, phoneNumber, location);
		this.activeRequests = new ArrayList<ReliefRequest>();
		this.familyMembers = new ArrayList<FamilyMember>();
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

	/** Everyone in the household, including the victim */
	public ArrayList<FamilyMember> getFamilyMembers() {
		return familyMembers;
	}

	/** Adds someone to the household */
	public void addFamilyMember(FamilyMember member) {
		familyMembers.add(member);
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
