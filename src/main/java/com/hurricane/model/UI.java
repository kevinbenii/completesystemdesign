package com.hurricane.model;

import java.util.ArrayList;

public class UI {
	private Server facade;

	UI() {
		facade = Server.getInstance();
	}

	public void run() {
		scenario1();
		scenario2();
		scenario3();
		scenario4();
		scenario5();
		scenario6();
		scenario7();
	}

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

	//List shelters
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

	//Wrong password
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

	//Send a request
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

	//Hurricanes nearby
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

	//Closest shelter and directions
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

	public static void main(String[] args) {
		UI userInterface = new UI();
		userInterface.run();

	}
}
