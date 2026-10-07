package com.hurricane.model;

import java.util.ArrayList;

public class Server {
	private static Server instance;
	private UserManager userManager;
	private RequestManager requestManager;
	private HurricaneTracker hurricaneTracker;
	private ShelterManager shelterManager;

	// Users load first so the other managers can link their records to the same User objects
	private Server(DataManager dataManager) {
		userManager = new UserManager(dataManager);
		requestManager = new RequestManager(dataManager);
		hurricaneTracker = new HurricaneTracker(dataManager);
		shelterManager = new ShelterManager(dataManager);
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

	// Shelter changes are saved right away so occupancy survives a restart
	public void addShelter(Shelter shelter) {
		shelterManager.addShelter(shelter);
		shelterManager.saveShelters();
	}

	// The shelter methods below just pass through to ShelterManager (radius is in miles)
	public ArrayList<Shelter> filterSheltersByLocation(Location location, double radius) {
		return shelterManager.filterByLocation(location, radius);
	}

	public ArrayList<Shelter> findAvailableShelters(Location location, double radius) {
		return shelterManager.findAvailableShelters(location, radius);
	}

	// Only saves if the move worked (shelter was open and had space)
	public boolean assignVictimToShelter(Victim victim, Shelter shelter) {
		boolean assigned = shelterManager.assignVictimToShelter(victim, shelter);
		if (assigned)
			shelterManager.saveShelters();
		return assigned;
	}
}
