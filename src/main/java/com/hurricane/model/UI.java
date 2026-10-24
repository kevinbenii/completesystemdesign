package com.hurricane.model;

import java.util.ArrayList;

/** Driver scenarios */
public class UI {
	private Server facade;

	/** Gets the facade */
	UI() {
		facade = Server.getInstance();
	}

	/** Runs all scenarios */
	public void run() {
		scenario1();
		scenario2();
		scenario3();
		scenario4();
		scenario5();
		scenario6();
		scenario7();
		scenario8();
		scenario9();
		scenario10();
		scenario11();
	}

	/** Logs in as victim1 */
	public void scenario1() {
		System.out.println();

		String username = "victim1";
		String password = "password456";

		if (!facade.login(username, password)) {
			System.out.println("Login failed.");
			return;
		}
		System.out.println("Logged in.");
	}

	/** Sign up and log in */
	public void scenario2() {
		System.out.println();

		//New name each run
		String username = "user" + System.currentTimeMillis();
		String password = "password";
		String phoneNumber = "phone";
		String address = "address";
		String city = "city";
		String state = "state";
		String zipCode = "zipCode";
		double latitude = 0.0;
		double longitude = 0.0;

		Location location = new Location(address, city, state, zipCode, latitude, longitude);

		if (!facade.createVictimAccount(username, password, phoneNumber, location)) {
			System.out.println("Account creation failed.");
			return;
		}
		System.out.println("Account created.");

		if (!facade.login(username, password)) {
			System.out.println("Login failed.");
			return;
		}
		System.out.println("Logged in.");
	}

	/** List shelters */
	public void scenario3() {
		System.out.println();

		ArrayList<Shelter> shelters = facade.getAllShelters();
		if (shelters.isEmpty()) {
			System.out.println("No shelters found.");
			return;
		}

		for (Shelter shelter : shelters) {
			System.out.println(shelter.getName() + " - " + shelter.getAvailableSpace() + " beds available");
		}
	}

	/** Wrong password */
	public void scenario4() {
		System.out.println();

		String username = "victim1";
		String password = "wrongPassword";

		if (facade.login(username, password)) {
			System.out.println("Login should have failed.");
			facade.logout();
			return;
		}
		System.out.println("Login failed as expected.");
	}

	/** Send a request */
	public void scenario5() {
		System.out.println();

		//Details
		String username = "victim1";
		String password = "password456";
		String subject = "Need water";
		String body = "Family of three is out of drinking water.";
		ArrayList<Skill> requiredSkills = new ArrayList<Skill>();
		requiredSkills.add(Skill.FOOD);

		//Log in
		if (!facade.login(username, password)) {
			System.out.println("Login failed.");
			return;
		}

		//Send
		if (!facade.submitRequest(subject, body, requiredSkills)) {
			System.out.println("Request failed.");
			facade.logout();
			return;
		}

		//Show total
		System.out.println("Request submitted. Total requests: " + facade.getAllRequests().size());
		facade.logout();
	}

	/** Hurricanes nearby */
	public void scenario6() {
		System.out.println();

		//Search area
		Location location = new Location("address", "Charleston", "SC", "29401", 32.7765, -79.9311);
		double radius = 100;

		//Find
		ArrayList<Hurricane> hurricanes = facade.filterHurricanesByLocation(location, radius);
		if (hurricanes.isEmpty()) {
			System.out.println("No hurricanes nearby.");
			return;
		}

		//Show each
		for (Hurricane hurricane : hurricanes) {
			System.out.println("Category " + hurricane.getCategory() + " - " + hurricane.getWindSpeed() + " mph");
		}
	}

	/** Closest shelter and directions */
	public void scenario7() {
		System.out.println();

		String username = "victim1";
		String password = "password456";

		//Log in
		if (!facade.login(username, password)) {
			System.out.println("Login failed.");
			return;
		}

		//Find
		Shelter shelter = facade.findClosestShelter();
		if (shelter == null) {
			System.out.println("No shelter found.");
			facade.logout();
			return;
		}
		System.out.println("Closest shelter: " + shelter.getName());

		//Directions
		for (String step : facade.getDirectionsToShelter(shelter)) {
			System.out.println(step);
		}
		facade.logout();
	}

	/** Volunteer takes a request, cannot complete it */
	public void scenario8() {
		System.out.println();

		String username = "volunteer1";
		String password = "password123";

		//Log in
		if (!facade.login(username, password)) {
			System.out.println("Login failed.");
			return;
		}

		//Find an open request
		ReliefRequest request = null;
		for (ReliefRequest each : facade.getAllRequests()) {
			if (each.getStatus() == ReliefRequestStatus.OPEN) {
				request = each;
				break;
			}
		}
		if (request == null) {
			System.out.println("No open requests.");
			facade.logout();
			return;
		}

		//Take it
		if (!facade.volunteerForRequest(request)) {
			System.out.println("Could not take the request.");
			facade.logout();
			return;
		}
		System.out.println("Request \"" + request.getSubject() + "\" is now " + request.getStatus() + ".");

		//Try to complete it
		if (!facade.completeRequest(request)) {
			System.out.println("Sorry, your " + facade.getCurrentUser().getRoleName()
					+ " role does not have permission to complete relief requests. The request stays assigned to you.");
		}
		facade.logout();
	}

	/** Rosa's recommended requests */
	public void scenario9() {
		System.out.println();

		String username = "rdelgado";
		String password = "rosa123";

		//Log in
		if (!facade.login(username, password)) {
			System.out.println("Login failed.");
			return;
		}

		//Show requests
		ArrayList<ReliefRequest> requests = facade.recommendRequests();
		System.out.println("Open requests: " + requests.size());
		for (ReliefRequest request : requests) {
			String reason = facade.getRecommendationReason(request);
			if (reason.isEmpty()) {
				System.out.println("- " + request.getSubject());
			} else {
				System.out.println("* RECOMMENDED: " + request.getSubject());
				System.out.println("  " + reason);
			}
		}
		facade.logout();
	}

	/** Rosa exports a trip sheet */
	public void scenario10() {
		System.out.println();

		String username = "rdelgado";
		String password = "rosa123";

		//Log in
		if (!facade.login(username, password)) {
			System.out.println("Login failed.");
			return;
		}

		//Top request
		ArrayList<ReliefRequest> requests = facade.recommendRequests();
		if (requests.isEmpty()) {
			System.out.println("No open requests.");
			facade.logout();
			return;
		}
		ReliefRequest request = requests.get(0);

		//Suitable shelter
		Shelter shelter = facade.findSuitableShelter(request);

		//Export
		String file = facade.exportTrip(request, shelter);
		if (file == null)
			System.out.println("Could not export the trip.");
		else
			System.out.println("Trip sheet saved to " + file);
		facade.logout();
	}

	/** Rosa finds a shelter and takes the request */
	public void scenario11() {
		System.out.println();

		String username = "rdelgado";
		String password = "rosa123";

		//Log in
		if (!facade.login(username, password)) {
			System.out.println("Login failed.");
			return;
		}

		//Top request
		ArrayList<ReliefRequest> requests = facade.recommendRequests();
		if (requests.isEmpty()) {
			System.out.println("No open requests.");
			facade.logout();
			return;
		}
		ReliefRequest request = requests.get(0);
		System.out.println("Finding a shelter for: " + request.getSubject());
		System.out.println("Needs: " + request.getPeopleCount() + " people, " + request.getPets().size()
				+ " pet(s), medical staff: " + (request.needsMedical() ? "yes" : "no"));

		//Check every shelter
		for (Shelter shelter : facade.getAllShelters()) {
			System.out.printf("%-40s dogs: %-3s medical: %-3s space: %-3s %s%n", shelter.getName(),
					shelter.acceptsDogs() ? "yes" : "no", shelter.hasMedicalStaff() ? "yes" : "no",
					shelter.hasSpaceFor(request.getPeopleCount(), request.getPets().size()) ? "yes" : "no",
					facade.isSuitable(shelter, request) ? "<- suitable" : "");
		}

		//Pick one
		Shelter shelter = facade.findSuitableShelter(request);
		if (shelter == null) {
			System.out.println("No suitable shelter.");
			facade.logout();
			return;
		}
		System.out.println("Closest suitable shelter: " + shelter.getName() + ", "
				+ shelter.getLocation().getFullAddress());

		//Take it
		if (!facade.volunteerForRequest(request)) {
			System.out.println("Could not take the request.");
			facade.logout();
			return;
		}
		System.out.println("Request is now " + request.getStatus() + ", handled by "
				+ request.getAssignedVolunteer().getUsername() + ".");
		facade.logout();
	}

	/** Portia's Carter family scenario */
	public void carterScenario() {
		ReliefRequest request = michaelAsksForHelp();
		if (request == null)
			return;
		Shelter shelter = rosaHelps(request);
		if (shelter == null)
			return;
		felixChecksIn(request);
	}

	/** Felix checks the family in and completes the request */
	private void felixChecksIn(ReliefRequest request) {
		System.out.println();
		System.out.println("=== Felix, the shelter manager, logs in ===");

		//Log in
		if (!facade.login("felix", "felix123")) {
			System.out.println("Login failed.");
			return;
		}
		Shelter shelter = facade.getMyShelter();
		if (shelter == null) {
			System.out.println("No shelter found for Felix.");
			facade.logout();
			return;
		}

		//Before
		printShelter(shelter);

		//Check in
		System.out.println();
		System.out.println("=== Felix checks in the Carter family and Buddy ===");
		if (!facade.checkInFamily(request)) {
			System.out.println("Check-in failed.");
			facade.logout();
			return;
		}
		System.out.println("Checked in 3 people and 1 pet.");
		printShelter(shelter);

		//Complete
		System.out.println();
		System.out.println("=== Felix marks the request Complete ===");
		if (facade.completeRequest(request))
			System.out.println("Status: " + request.getStatus());
		else
			System.out.println("Could not complete the request.");
		facade.logout();
	}

	/** Prints a shelter for its manager */
	private void printShelter(Shelter shelter) {
		System.out.println(shelter.getName());
		System.out.println("  Location: " + shelter.getLocation().getFullAddress());
		System.out.println("  Capacity: " + shelter.getCapacity());
		System.out.println("  Occupancy: " + shelter.getCurrentOccupancy());
		System.out.println("  Available spaces: " + shelter.getAvailableSpace());
		System.out.println("  Occupants:");
		for (Victim victim : shelter.getOccupants()) {
			if (victim.getFamilyMembers().isEmpty()) {
				System.out.println("  - " + victim.getUsername());
			} else {
				for (FamilyMember member : victim.getFamilyMembers())
					System.out.println("  - " + member.getName() + ", age " + member.getAge());
			}
		}
		System.out.println("  Pets:");
		for (Pet pet : shelter.getPets())
			System.out.println("  - " + pet.getName() + " (" + pet.getType() + ")");
	}

	/** Rosa finds, takes and plans the trip */
	private Shelter rosaHelps(ReliefRequest request) {
		System.out.println();
		System.out.println("=== Rosa Delgado logs in and sees open requests ===");

		//Log in
		if (!facade.login("rdelgado", "rosa123")) {
			System.out.println("Login failed.");
			return null;
		}

		//Open requests
		ArrayList<ReliefRequest> requests = facade.recommendRequests();
		System.out.println("Open requests: " + requests.size());
		for (ReliefRequest each : requests) {
			String reason = facade.getRecommendationReason(each);
			if (reason.isEmpty()) {
				System.out.println("- " + each.getSubject());
			} else {
				System.out.println("* RECOMMENDED: " + each.getSubject());
				System.out.println("  " + reason);
			}
		}

		//Open the Carter request
		System.out.println();
		System.out.println("=== Rosa opens the Carter request ===");
		printRequest(request);

		//Search shelters
		System.out.println();
		System.out.println("=== Rosa searches the " + facade.getAllShelters().size() + " shelters ===");
		int people = request.getPeopleCount();
		int pets = request.getPets().size();
		for (Shelter each : facade.getAllShelters()) {
			System.out.printf("%-40s dogs: %-3s medical: %-3s space: %-3s %s%n", each.getName(),
					each.acceptsDogs() ? "yes" : "no", each.hasMedicalStaff() ? "yes" : "no",
					each.hasSpaceFor(people, pets) ? "yes" : "no",
					facade.isSuitable(each, request) ? "<- suitable" : "");
		}
		Shelter shelter = facade.findSuitableShelter(request);
		if (shelter == null) {
			System.out.println("No suitable shelter.");
			facade.logout();
			return null;
		}
		System.out.println("Closest suitable shelter by driving: " + shelter.getName() + ", "
				+ shelter.getLocation().getFullAddress());

		//Take the request
		System.out.println();
		System.out.println("=== Rosa assigns the request to herself ===");
		if (!facade.volunteerForRequest(request)) {
			System.out.println("Could not take the request.");
			facade.logout();
			return null;
		}
		System.out.println("Status: " + request.getStatus() + ", handled by "
				+ request.getAssignedVolunteer().getUsername());

		//Export the trip
		System.out.println();
		System.out.println("=== Rosa exports the trip information ===");
		String file = facade.exportTrip(request, shelter);
		System.out.println(file == null ? "Could not export the trip." : "Trip sheet saved to " + file);

		//Try to complete
		System.out.println();
		System.out.println("=== Rosa tries to mark the request Complete ===");
		if (!facade.completeRequest(request)) {
			System.out.println("Sorry, your " + facade.getCurrentUser().getRoleName()
					+ " role does not have permission to complete relief requests.");
			System.out.println("Status: " + request.getStatus() + ", still assigned to "
					+ request.getAssignedVolunteer().getUsername());
		}
		facade.logout();
		return shelter;
	}

	/** Michael signs up and sends a request */
	private ReliefRequest michaelAsksForHelp() {
		System.out.println();
		System.out.println("=== Michael Carter creates an account and asks for help ===");

		//Family details
		String username = "mcarter";
		String password = "carter123";
		String phoneNumber = "843-555-0142";
		Location home = new Location("1 Vendue Range", "Charleston", "SC", "29401", 32.7781, -79.9250);
		ArrayList<FamilyMember> family = new ArrayList<FamilyMember>();
		family.add(new FamilyMember("Michael Carter", 42, ""));
		family.add(new FamilyMember("Laura Carter", 40, ""));
		family.add(new FamilyMember("Sophie Carter", 9, "Asthma"));
		ArrayList<Pet> pets = new ArrayList<Pet>();
		pets.add(new Pet("Buddy", "dog"));

		//Sign up
		if (!facade.createFamilyAccount(username, password, phoneNumber, home, family, pets)) {
			System.out.println("Account creation failed. Reset the JSON files and try again.");
			return null;
		}
		System.out.println("Account created for Michael Carter (" + username + ").");

		//Log in
		if (!facade.login(username, password)) {
			System.out.println("Login failed.");
			return null;
		}

		//Send the request
		ArrayList<Skill> needs = new ArrayList<Skill>();
		needs.add(Skill.TRANSPORTATION);
		needs.add(Skill.MEDICAL);
		ReliefRequest request = facade.submitFamilyRequest("Carter family needs a ride to a shelter",
				"Power is out and floodwater is rising. We cannot use our car.", needs,
				"Sophie Carter (9) has worsening asthma and needs a shelter with medical staff");
		facade.logout();
		if (request == null) {
			System.out.println("Request failed.");
			return null;
		}
		System.out.println("Relief request submitted:");
		printRequest(request);
		return request;
	}

	/** Prints a request's details */
	private void printRequest(ReliefRequest request) {
		Victim requester = request.getRequester();
		int adults = 0;
		int children = 0;
		System.out.println("  " + request.getSubject());
		System.out.println("  " + request.getBody());
		if (requester != null) {
			for (FamilyMember member : requester.getFamilyMembers()) {
				System.out.println("  - " + member.getName() + ", age " + member.getAge());
				if (member.getAge() < 18)
					children++;
				else
					adults++;
			}
		}
		for (Pet pet : request.getPets())
			System.out.println("  - " + pet.getName() + " (" + pet.getType() + ")");
		System.out.println("  Needs transport for: " + adults + " adults, " + children + " child, "
				+ request.getPets().size() + " dog");
		if (request.needsMedical())
			System.out.println("  Medical: " + request.getMedicalNote());
		if (requester != null)
			System.out.println("  Contact: " + requester.getPhoneNumber());
		System.out.println("  Location: " + request.getLocation().getFullAddress());
		System.out.println("  Status: " + request.getStatus());
	}

	/** Starts the UI */
	public static void main(String[] args) {
		UI userInterface = new UI();
		userInterface.run();

	}
}
