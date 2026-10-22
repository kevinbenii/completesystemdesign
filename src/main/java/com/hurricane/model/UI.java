package com.hurricane.model;

import java.util.ArrayList;

/** Runs the driver scenarios through the Server facade */
public class UI {
	private Server facade;

	/** Gets the shared Server facade */
	UI() {
		facade = Server.getInstance();
	}

	/** Runs every scenario in order */
	public void run() {
		scenario1();
		scenario2();
		scenario3();
		scenario4();
		scenario5();
		scenario6();
		scenario7();
		scenario8();
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

	/** Creates a new account and logs in with it */
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

	/** Lists every shelter and its free beds */
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

	/** Logs in with a wrong password and checks it fails */
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

	/** Victim sends a relief request, which is saved */
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

	/** Lists hurricanes within 100 miles of Charleston */
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

	/** Finds the closest shelter and prints driving directions */
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

	/** Volunteer takes an open request, then tries to complete it and is blocked */
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
			System.out.println("Your " + facade.getCurrentUser().getRoleName()
					+ " role does not have permission to complete relief requests. The request stays "
					+ request.getStatus() + ".");
		}
		facade.logout();
	}

	/** Starts the UI and runs the scenarios */
	public static void main(String[] args) {
		UI userInterface = new UI();
		userInterface.run();

	}
}
