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

		//New name each run so signup never clashes
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

	// Lists the shelters, going through the facade like the other scenarios
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

	//Wrong password should be rejected
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

	public static void main(String[] args) {
		UI userInterface = new UI();
		userInterface.run();

	}
}
