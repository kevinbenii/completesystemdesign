package com.hurricane.model;

import java.util.ArrayList;

public class Client {
	private Server server;
	private User currentUser;

	public Client() {
		// Relative to javafx/relief_system, where `mvn javafx:run` starts
		server = Server.getInstance(new FileDataManager("../../json"));
	}

	public boolean attemptLogin(String username, String password) {
		return server.authenticateUser(username, password);
	}

	public void logout() {
		currentUser = null;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void submitRequest(ReliefRequest request) {
		server.submitRequest(request);
	}

	public ArrayList<ReliefRequest> filterRequestsBySkill(Skill skill) {
		return server.filterRequestsBySkill(skill);
	}

	public ArrayList<ReliefRequest> filterRequestsByLocation(Location location, double radius) {
		return server.filterRequestsByLocation(location, radius);
	}
}
