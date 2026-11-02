package com.hurricane.model;

import java.util.ArrayList;

/** Client that talks to the server */
public class Client {
	private Server server;
	private User currentUser;

	/** Connects to the server */
	public Client() {
		// Same data folder as Server.getInstance(); see FileDataManager.defaultDataFolder()
		server = Server.getInstance(new FileDataManager(FileDataManager.defaultDataFolder()));
	}

	/** Tries to log in */
	public boolean attemptLogin(String username, String password) {
		boolean loggedIn = server.authenticateUser(username, password);
		if (loggedIn)
			currentUser = server.getCurrentUser();
		return loggedIn;
	}

	/** Logs out */
	public void logout() {
		server.logout();
		currentUser = null;
	}

	/** Logged in user */
	public User getCurrentUser() {
		return currentUser;
	}

	/** Sends a request */
	public void submitRequest(ReliefRequest request) {
		server.submitRequest(request);
	}

	/** Requests that need a skill */
	public ArrayList<ReliefRequest> filterRequestsBySkill(Skill skill) {
		return server.filterRequestsBySkill(skill);
	}

	/** Requests within radius miles */
	public ArrayList<ReliefRequest> filterRequestsByLocation(Location location, double radius) {
		return server.filterRequestsByLocation(location, radius);
	}
}
