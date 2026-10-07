package com.hurricane.model;

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

	public static Server getInstance() {
		return getInstance(new FileDataManager("data.json"));
	}

	public boolean registerUser(User user) {
		return userManager.registerUser(user);
	}

	public boolean createVictimAccount(String username, String password, String phoneNumber, Location location) {
		if (!isValidAccount(username, password))
			return false;
		return registerUser(new Victim(username, password, phoneNumber, location));
	}

	public boolean createVolunteerAccount(String username, String password, String phoneNumber, Location location,
			ArrayList<Skill> skills) {
		if (!isValidAccount(username, password))
			return false;
		return registerUser(new Volunteer(username, password, phoneNumber, location, skills));
	}

	private boolean isValidAccount(String username, String password) {
		return username != null && !username.isBlank() && password != null && !password.isBlank();
	}

	public boolean login(String username, String password) {
		return authenticateUser(username, password);
	}

	public void logout() {
		userManager.logout();
	}

	public User getCurrentUser() {
		return userManager.getCurrentUser();
	}

	public boolean isLoggedIn() {
		return getCurrentUser() != null;
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
