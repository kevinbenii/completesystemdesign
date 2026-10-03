package com.model;

public class Admin extends User {
	private String adminId;

	public Admin(String username, String password, String phoneNumber, Location location) {
		super(username, password, phoneNumber, location);
		this.adminId = username;
	}

	public void deleteRequest(ReliefRequest request) {
		request.setStatus(ReliefRequestStatus.MODERATOR_REMOVED);
	}

	public void markAsResolved(ReliefRequest request) {
		request.setStatus(ReliefRequestStatus.RESOLVED);
	}

	public void banUser(User user) {
		// add the user to the blacklist
	}
}
