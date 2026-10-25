package com.hurricane.model;

import java.util.ArrayList;

/** Person who needs help */
public class Victim extends User {
	private ArrayList<ReliefRequest> activeRequests;
	private Hurricane affectedByHurricane;
	private ArrayList<FamilyMember> familyMembers;
	private ArrayList<Pet> pets;

	/** Makes a victim */
	public Victim(String username, String password, String phoneNumber, Location location) {
		super(username, password, phoneNumber, location);
		this.activeRequests = new ArrayList<ReliefRequest>();
		this.familyMembers = new ArrayList<FamilyMember>();
		this.pets = new ArrayList<Pet>();
	}

	/** Open requests */
	public ArrayList<ReliefRequest> getActiveRequests() {
		return activeRequests;
	}

	/** Hurricane affecting them */
	public Hurricane getAffectedByHurricane() {
		return affectedByHurricane;
	}

	/** Sets the hurricane affecting them */
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

	/** Household pets */
	public ArrayList<Pet> getPets() {
		return pets;
	}

	/** Adds a pet */
	public void addPet(Pet pet) {
		pets.add(pet);
	}

	/** Makes a request */
	public ReliefRequest createRequest(String subject, String body, ArrayList<Skill> requiredSkills) {
		ReliefRequest request = new ReliefRequest(location, subject, body, requiredSkills, this);
		activeRequests.add(request);
		return request;
	}

	/** Status of a request */
	public ReliefRequestStatus viewRequestStatus(ReliefRequest request) {
		return request.getStatus();
	}
}
