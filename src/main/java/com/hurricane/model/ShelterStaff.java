package com.hurricane.model;

/** Shelter manager who runs one shelter */
public class ShelterStaff extends User {
	//Shelter this person runs
	private String shelterId;

	/** Makes a shelter manager for that shelter */
	public ShelterStaff(String username, String password, String phoneNumber, Location location, String shelterId) {
		super(username, password, phoneNumber, location);
		this.shelterId = shelterId;
	}

	/** Id of the shelter they run */
	public String getShelterId() {
		return shelterId;
	}

	/** Shelter managers are allowed to complete relief requests */
	@Override
	public boolean canCompleteRequests() {
		return true;
	}
}
