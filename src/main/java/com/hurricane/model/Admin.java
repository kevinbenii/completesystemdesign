package com.hurricane.model;

/** Admin who moderates the system */
public class Admin extends User {
	private String adminId;

	/** Makes an admin */
	public Admin(String username, String password, String phoneNumber, Location location) {
		super(username, password, phoneNumber, location);
		this.adminId = username;
	}

	/** Admin id */
	public String getAdminId() {
		return adminId;
	}

	/** Restores a saved admin id */
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	/** Removes a request */
	public void deleteRequest(ReliefRequest request) {
		request.setStatus(ReliefRequestStatus.MODERATOR_REMOVED);
	}

	/** Marks a request resolved */
	public void markAsResolved(ReliefRequest request) {
		request.setStatus(ReliefRequestStatus.RESOLVED);
	}

	/** Bans a user */
	public boolean banUser(User user) {
		return Server.getInstance().banUser(user);
	}

	/** Admins are allowed to complete relief requests */
	@Override
	public boolean canCompleteRequests() {
		return true;
	}
}
