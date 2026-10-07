package com.hurricane.model;

public class Admin extends User {
	private String adminId;

	public Admin(String username, String password, String phoneNumber, Location location) {
		super(username, password, phoneNumber, location);
		this.adminId = username;
	}

	// The constructor defaults adminId to the username; the setter lets the
	// loader restore a saved id like "ADMIN001"
	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
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
