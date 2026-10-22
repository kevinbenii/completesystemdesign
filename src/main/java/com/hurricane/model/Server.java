package com.hurricane.model;

import java.util.ArrayList;

import com.location.Address;
import com.location.MapTools;

public class Server {
	private static Server instance;
	private UserManager userManager;
	private RequestManager requestManager;
	private HurricaneTracker hurricaneTracker;
	private ShelterManager shelterManager;

	//Load users first
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

	//Use the json folder
	public static Server getInstance() {
		return getInstance(new FileDataManager(FileDataManager.defaultDataFolder()));
	}

	public boolean registerUser(User user) {
		boolean registered = userManager.registerUser(user);
		if (registered)
			userManager.saveUsers();
		return registered;
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

	public boolean banUser(User user) {
		boolean banned = userManager.banUser(user);
		if (banned)
			userManager.saveUsers();
		return banned;
	}

	public void submitRequest(ReliefRequest request) {
		requestManager.submitRequest(request);
		requestManager.saveRequests();
	}

	//Victim sends a request
	public boolean submitRequest(String subject, String body, ArrayList<Skill> requiredSkills) {
		if (!(getCurrentUser() instanceof Victim))
			return false;
		Victim victim = (Victim) getCurrentUser();
		submitRequest(victim.createRequest(subject, body, requiredSkills));
		return true;
	}

	public void deleteRequest(ReliefRequest request) {
		requestManager.deleteRequest(request);
		requestManager.saveRequests();
	}

	public void markAsResolved(ReliefRequest request) {
		requestManager.markAsResolved(request);
		requestManager.saveRequests();
	}

	// Only a role that is allowed to (like an admin) can complete a request; everyone else gets false
	public boolean completeRequest(ReliefRequest request) {
		User user = getCurrentUser();
		if (user == null || !user.canCompleteRequests())
			return false;
		requestManager.completeRequest(request);
		requestManager.saveRequests();
		return true;
	}

	public ArrayList<ReliefRequest> filterRequestsBySkill(Skill skill) {
		return requestManager.filterBySkill(skill);
	}

	public ArrayList<ReliefRequest> filterRequestsByLocation(Location location, double radius) {
		return requestManager.filterByLocation(location, radius);
	}

	//All requests
	public ArrayList<ReliefRequest> getAllRequests() {
		return requestManager.getAllRequests();
	}

	public void addHurricane(Hurricane hurricane) {
		hurricaneTracker.addHurricane(hurricane);
		hurricaneTracker.saveHurricanes();
	}

	//Radius in miles
	public ArrayList<Hurricane> filterHurricanesByLocation(Location location, double radius) {
		return hurricaneTracker.filterByLocation(location, radius);
	}

	//Save right away
	public void addShelter(Shelter shelter) {
		shelterManager.addShelter(shelter);
		shelterManager.saveShelters();
	}

	//Radius in miles
	public ArrayList<Shelter> filterSheltersByLocation(Location location, double radius) {
		return shelterManager.filterByLocation(location, radius);
	}

	public ArrayList<Shelter> findAvailableShelters(Location location, double radius) {
		return shelterManager.findAvailableShelters(location, radius);
	}

	//All shelters
	public ArrayList<Shelter> getAllShelters() {
		return shelterManager.getAllShelters();
	}

	//Closest shelter by driving time
	public Shelter findClosestShelter() {
		if (!isLoggedIn())
			return null;
		ArrayList<Shelter> shelters = getAllShelters();
		Address[] addresses = new Address[shelters.size()];
		for (int i = 0; i < shelters.size(); i++)
			addresses[i] = shelters.get(i).getLocation().toAddress();
		try {
			Address closest = MapTools.getInstance().findClosestAddress(getCurrentUser().getLocation().toAddress(), addresses);
			for (int i = 0; i < addresses.length; i++)
				if (addresses[i] == closest)
					return shelters.get(i);
		} catch (Exception e) {
			System.out.println("Could not reach the map service.");
		}
		return null;
	}

	//Driving steps to a shelter
	public String[] getDirectionsToShelter(Shelter shelter) {
		if (!isLoggedIn() || shelter == null)
			return new String[0];
		try {
			return MapTools.getInstance().getDrivingDirections(getCurrentUser().getLocation().toAddress(), shelter.getLocation().toAddress());
		} catch (Exception e) {
			System.out.println("Could not reach the map service.");
			return new String[0];
		}
	}

	//Save only if it worked
	public boolean assignVictimToShelter(Victim victim, Shelter shelter) {
		boolean assigned = shelterManager.assignVictimToShelter(victim, shelter);
		if (assigned)
			shelterManager.saveShelters();
		return assigned;
	}
}
