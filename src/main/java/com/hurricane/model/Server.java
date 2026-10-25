package com.hurricane.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.location.Address;
import com.location.MapTools;

/** Facade the UI talks to */
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

	/** Shared server using that data */
	public static Server getInstance(DataManager dataManager) {
		if (instance == null) {
			instance = new Server(dataManager);
		}

		return instance;
	}

	/** Shared server using the json folder */
	public static Server getInstance() {
		return getInstance(new FileDataManager(FileDataManager.defaultDataFolder()));
	}

	/** Adds a user and saves */
	public boolean registerUser(User user) {
		boolean registered = userManager.registerUser(user);
		if (registered)
			userManager.saveUsers();
		return registered;
	}

	/** Makes a victim account */
	public boolean createVictimAccount(String username, String password, String phoneNumber, Location location) {
		if (!isValidAccount(username, password))
			return false;
		return registerUser(new Victim(username, password, phoneNumber, location));
	}

	/** Victim account with family and pets, saved together */
	public boolean createFamilyAccount(String username, String password, String phoneNumber, Location location,
			ArrayList<FamilyMember> family, ArrayList<Pet> pets) {
		if (!isValidAccount(username, password))
			return false;
		Victim victim = new Victim(username, password, phoneNumber, location);
		for (FamilyMember member : family)
			victim.addFamilyMember(member);
		for (Pet pet : pets)
			victim.addPet(pet);
		return registerUser(victim);
	}

	/** Makes a volunteer account */
	public boolean createVolunteerAccount(String username, String password, String phoneNumber, Location location,
			ArrayList<Skill> skills) {
		if (!isValidAccount(username, password))
			return false;
		return registerUser(new Volunteer(username, password, phoneNumber, location, skills));
	}

	private boolean isValidAccount(String username, String password) {
		return username != null && !username.isBlank() && password != null && !password.isBlank();
	}

	/** Logs in */
	public boolean login(String username, String password) {
		return authenticateUser(username, password);
	}

	/** Logs out */
	public void logout() {
		userManager.logout();
	}

	/** Logged in user */
	public User getCurrentUser() {
		return userManager.getCurrentUser();
	}

	/** True if someone is logged in */
	public boolean isLoggedIn() {
		return getCurrentUser() != null;
	}

	/** Checks a login */
	public boolean authenticateUser(String username, String password) {
		return userManager.authenticateUser(username, password);
	}

	/** Bans a user and saves */
	public boolean banUser(User user) {
		boolean banned = userManager.banUser(user);
		if (banned)
			userManager.saveUsers();
		return banned;
	}

	/** Adds a request and saves */
	public void submitRequest(ReliefRequest request) {
		requestManager.submitRequest(request);
		requestManager.saveRequests();
	}

	/** Logged in victim sends a request */
	public boolean submitRequest(String subject, String body, ArrayList<Skill> requiredSkills) {
		if (!(getCurrentUser() instanceof Victim))
			return false;
		Victim victim = (Victim) getCurrentUser();
		submitRequest(victim.createRequest(subject, body, requiredSkills));
		return true;
	}

	/** Logged-in victim sends a family request, returns it or null */
	public ReliefRequest submitFamilyRequest(String subject, String body, ArrayList<Skill> requiredSkills,
			String medicalNote) {
		if (!(getCurrentUser() instanceof Victim))
			return null;
		Victim victim = (Victim) getCurrentUser();
		ReliefRequest request = victim.createRequest(subject, body, requiredSkills);
		request.setPeopleCount(Math.max(1, victim.getFamilyMembers().size()));
		request.setMedical(medicalNote != null && !medicalNote.isEmpty(), medicalNote == null ? "" : medicalNote);
		for (Pet pet : victim.getPets())
			request.addPet(pet);
		submitRequest(request);
		return request;
	}

	/** Deletes a request and saves */
	public void deleteRequest(ReliefRequest request) {
		requestManager.deleteRequest(request);
		requestManager.saveRequests();
	}

	/** Marks a request resolved and saves */
	public void markAsResolved(ReliefRequest request) {
		requestManager.markAsResolved(request);
		requestManager.saveRequests();
	}

	/** Completes a request if the role allows it */
	public boolean completeRequest(ReliefRequest request) {
		User user = getCurrentUser();
		if (user == null || !user.canCompleteRequests())
			return false;
		requestManager.completeRequest(request);
		requestManager.saveRequests();
		return true;
	}

	/** Logged in volunteer takes a request */
	public boolean volunteerForRequest(ReliefRequest request) {
		if (!(getCurrentUser() instanceof Volunteer))
			return false;
		((Volunteer) getCurrentUser()).volunteerForRequest(request);
		requestManager.saveRequests();
		return true;
	}

	/** Requests that need a skill */
	public ArrayList<ReliefRequest> filterRequestsBySkill(Skill skill) {
		return requestManager.filterBySkill(skill);
	}

	/** Requests within radius miles */
	public ArrayList<ReliefRequest> filterRequestsByLocation(Location location, double radius) {
		return requestManager.filterByLocation(location, radius);
	}

	/** All requests */
	public ArrayList<ReliefRequest> getAllRequests() {
		return requestManager.getAllRequests();
	}

	//Recommend radius in miles
	private static final double RECOMMEND_RADIUS = 10;

	/** Open requests, best first */
	public ArrayList<ReliefRequest> recommendRequests() {
		ArrayList<ReliefRequest> recommended = new ArrayList<ReliefRequest>();
		ArrayList<ReliefRequest> others = new ArrayList<ReliefRequest>();
		if (!(getCurrentUser() instanceof Volunteer))
			return recommended;
		for (ReliefRequest request : requestManager.getOpenRequests()) {
			if (isRecommended(request))
				recommended.add(request);
			else
				others.add(request);
		}
		sortByDistance(recommended);
		sortByDistance(others);
		recommended.addAll(others);
		return recommended;
	}

	/** Why it is recommended */
	public String getRecommendationReason(ReliefRequest request) {
		if (!isRecommended(request))
			return "";
		Skill skill = matchingSkill(request);
		return String.format("Recommended: %.1f miles away, needs %s (your specialty)", distanceTo(request), skill);
	}

	//Nearby and skill match
	private boolean isRecommended(ReliefRequest request) {
		return matchingSkill(request) != null && distanceTo(request) <= RECOMMEND_RADIUS;
	}

	//Matching skill
	private Skill matchingSkill(ReliefRequest request) {
		if (!(getCurrentUser() instanceof Volunteer))
			return null;
		for (Skill skill : ((Volunteer) getCurrentUser()).getSkills()) {
			if (request.needsSkill(skill))
				return skill;
		}
		return null;
	}

	//Miles to the request
	private double distanceTo(ReliefRequest request) {
		return getCurrentUser().getLocation().distanceTo(request.getLocation());
	}

	//Closest first
	private void sortByDistance(ArrayList<ReliefRequest> list) {
		list.sort((a, b) -> Double.compare(distanceTo(a), distanceTo(b)));
	}

	/** Adds a hurricane and saves */
	public void addHurricane(Hurricane hurricane) {
		hurricaneTracker.addHurricane(hurricane);
		hurricaneTracker.saveHurricanes();
	}

	/** Hurricanes within radius miles */
	public ArrayList<Hurricane> filterHurricanesByLocation(Location location, double radius) {
		return hurricaneTracker.filterByLocation(location, radius);
	}

	/** Adds a shelter and saves */
	public void addShelter(Shelter shelter) {
		shelterManager.addShelter(shelter);
		shelterManager.saveShelters();
	}

	/** Shelters within radius miles */
	public ArrayList<Shelter> filterSheltersByLocation(Location location, double radius) {
		return shelterManager.filterByLocation(location, radius);
	}

	/** Open shelters with space within radius miles */
	public ArrayList<Shelter> findAvailableShelters(Location location, double radius) {
		return shelterManager.findAvailableShelters(location, radius);
	}

	/** All shelters */
	public ArrayList<Shelter> getAllShelters() {
		return shelterManager.getAllShelters();
	}

	/** Shelter the logged in manager runs */
	public Shelter getMyShelter() {
		if (!(getCurrentUser() instanceof ShelterStaff))
			return null;
		return shelterManager.getShelterById(((ShelterStaff) getCurrentUser()).getShelterId());
	}

	/** Closest shelter by driving */
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

	/** True if the shelter fits the request */
	public boolean isSuitable(Shelter shelter, ReliefRequest request) {
		if (hasDog(request) && !shelter.acceptsDogs())
			return false;
		if (request.needsMedical() && !shelter.hasMedicalStaff())
			return false;
		return shelter.hasSpaceFor(request.getPeopleCount(), request.getPets().size());
	}

	/** Closest suitable shelter by driving */
	public Shelter findSuitableShelter(ReliefRequest request) {
		ArrayList<Shelter> suitable = new ArrayList<Shelter>();
		for (Shelter shelter : getAllShelters()) {
			if (isSuitable(shelter, request))
				suitable.add(shelter);
		}
		if (suitable.isEmpty())
			return null;

		Address[] addresses = new Address[suitable.size()];
		for (int i = 0; i < suitable.size(); i++)
			addresses[i] = suitable.get(i).getLocation().toAddress();
		try {
			Address closest = MapTools.getInstance().findClosestAddress(request.getLocation().toAddress(), addresses);
			for (int i = 0; i < addresses.length; i++)
				if (addresses[i] == closest)
					return suitable.get(i);
		} catch (Exception e) {
			System.out.println("Could not reach the map service, using straight-line distance.");
		}
		return nearestByMiles(suitable, request.getLocation());
	}

	//Any dog in the request
	private boolean hasDog(ReliefRequest request) {
		for (Pet pet : request.getPets()) {
			if (pet.getType().equalsIgnoreCase("dog"))
				return true;
		}
		return false;
	}

	//Backup when the map is down
	private Shelter nearestByMiles(ArrayList<Shelter> shelters, Location from) {
		Shelter nearest = null;
		for (Shelter shelter : shelters) {
			if (nearest == null || shelter.getLocation().distanceTo(from) < nearest.getLocation().distanceTo(from))
				nearest = shelter;
		}
		return nearest;
	}

	/** Driving directions to a shelter */
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

	/** Writes the trip sheet */
	public String exportTrip(ReliefRequest request, Shelter shelter) {
		if (!isLoggedIn() || request == null || shelter == null)
			return null;
		Location start = getCurrentUser().getLocation();
		Location pickup = request.getLocation();
		Location dropoff = shelter.getLocation();
		Victim requester = request.getRequester();

		StringBuilder out = new StringBuilder();
		out.append("RELIEF TRIP SHEET\n");
		out.append("=================\n\n");
		out.append("Request: ").append(request.getSubject()).append("\n");
		out.append(request.getBody()).append("\n\n");

		out.append("FAMILY\n");
		if (requester != null) {
			for (FamilyMember member : requester.getFamilyMembers())
				out.append("- ").append(member.getName()).append(", age ").append(member.getAge()).append("\n");
		}
		for (Pet pet : request.getPets())
			out.append("- ").append(pet.getName()).append(" (").append(pet.getType()).append(")\n");
		out.append("People to move: ").append(request.getPeopleCount()).append("\n\n");

		if (request.needsMedical()) {
			out.append("MEDICAL NOTE\n");
			out.append(request.getMedicalNote()).append("\n\n");
		}

		out.append("CONTACT\n");
		if (requester != null) {
			String name = requester.getFamilyMembers().isEmpty() ? requester.getUsername()
					: requester.getFamilyMembers().get(0).getName() + " (" + requester.getUsername() + ")";
			out.append("Name: ").append(name).append("\n");
			out.append("Phone: ").append(requester.getPhoneNumber()).append("\n");
		}
		out.append("Pickup address: ").append(pickup.getFullAddress()).append("\n\n");

		out.append("SHELTER\n");
		out.append(shelter.getName()).append("\n");
		out.append(dropoff.getFullAddress()).append("\n\n");

		out.append("DIRECTIONS: YOUR LOCATION TO PICKUP\n");
		appendDirections(out, start, pickup);
		out.append("\nDIRECTIONS: PICKUP TO SHELTER\n");
		appendDirections(out, pickup, dropoff);

		try {
			Path file = Paths.get("exports", "trip.txt");
			Files.createDirectories(file.getParent());
			Files.writeString(file, out.toString());
			return file.toString();
		} catch (IOException e) {
			System.out.println("Could not write the trip file.");
			return null;
		}
	}

	//Numbered steps
	private void appendDirections(StringBuilder out, Location from, Location to) {
		try {
			String[] steps = MapTools.getInstance().getDrivingDirections(from.toAddress(), to.toAddress());
			for (int i = 0; i < steps.length; i++)
				out.append(i + 1).append(". ").append(steps[i]).append("\n");
		} catch (Exception e) {
			out.append("Directions not available.\n");
		}
	}

	/** Shelter manager checks a family and pets in */
	public boolean checkInFamily(ReliefRequest request) {
		Shelter shelter = getMyShelter();
		if (shelter == null || request == null || request.getRequester() == null)
			return false;
		Victim victim = request.getRequester();
		int people = Math.max(1, victim.getFamilyMembers().size());
		if (!shelter.hasSpaceFor(people, request.getPets().size()))
			return false;
		if (!shelter.checkIn(victim))
			return false;
		for (Pet pet : request.getPets())
			shelter.addPet(pet);
		shelterManager.saveShelters();
		return true;
	}

	/** Puts a victim in a shelter and saves */
	public boolean assignVictimToShelter(Victim victim, Shelter shelter) {
		boolean assigned = shelterManager.assignVictimToShelter(victim, shelter);
		if (assigned)
			shelterManager.saveShelters();
		return assigned;
	}
}
