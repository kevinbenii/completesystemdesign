package com.model;

import java.util.ArrayList;

public class Server {
	private static Server instance;
	private UserManager userManager;
	private RequestManager requestManager;
	private HurricaneTracker hurricaneTracker;

	private Server(DataManager dataManager) {
		userManager = new UserManager(dataManager);
		requestManager = new RequestManager(dataManager);
		hurricaneTracker = new HurricaneTracker(dataManager);
	}

	public static Server getInstance(DataManager dataManager) {
		if (instance == null) {
			instance = new Server(dataManager);
		}

		return instance;
	}

	public void registerUser(User user) {
		userManager.registerUser(user);
	}

	public boolean authenticateUser(String username, String password) {
		return userManager.authenticateUser(username, password);
	}

	public void banUser(User user) {
		userManager.banUser(user);
	}

	public void submitRequest(ReliefRequest request) {
		requestManager.submitRequest(request);
	}

	public void deleteRequest(ReliefRequest request) {
		requestManager.deleteRequest(request);
	}

	public void markAsResolved(ReliefRequest request) {
		requestManager.markAsResolved(request);
	}

	public ArrayList<ReliefRequest> filterRequestsBySkill(Skill skill) {
		return requestManager.filterBySkill(skill);
	}

	public ArrayList<ReliefRequest> filterRequestsByLocation(Location location, double radius) {
		return requestManager.filterByLocation(location, radius);
	}

	public ArrayList<Hurricane> filterHurricanesByLocation(Location location) {
		return hurricaneTracker.filterByLocation(location);
	}
}
