package com.hurricane.model;

import java.util.ArrayList;

public class UserManager {
	private ArrayList<User> users;
	private ArrayList<Admin> admins;
	private ArrayList<User> blacklist;
	private User currentUser;

	public UserManager(DataManager dataManager) {
		users = dataManager.loadUsers();
		admins = new ArrayList<Admin>();
		blacklist = new ArrayList<User>();
		if (users.isEmpty())
			addSampleUsers();
	}

	// Sample data from json/users.json to validate against until the DataLoader reads the file
	private void addSampleUsers() {
		ArrayList<Skill> skills = new ArrayList<Skill>();
		skills.add(Skill.FOOD);
		skills.add(Skill.TRANSPORTATION);
		users.add(new Volunteer("volunteer1", "password123", "555-111-1111",
				new Location("100 Main Street", "Columbia", "SC", "29201", 34.0007, -81.0348), skills));
		users.add(new Victim("victim1", "password456", "555-222-2222",
				new Location("200 Oak Street", "Columbia", "SC", "29205", 33.9900, -81.0200)));
		Admin admin = new Admin("admin1", "adminPassword123", "555-333-3333",
				new Location("300 State Street", "Columbia", "SC", "29201", 34.0020, -81.0300));
		users.add(admin);
		admins.add(admin);
	}

	public boolean registerUser(User user) {
		for (User existing : users) {
			if (existing.getUsername().equalsIgnoreCase(user.getUsername()))
				return false;
		}
		users.add(user);
		return true;
	}

	public boolean authenticateUser(String username, String password) {
		for (User user : users) {
			if (user.login(username, password) && !isBlacklisted(user)) {
				currentUser = user;
				return true;
			}
		}
		return false;
	}

	public void logout() {
		currentUser = null;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void banUser(User user) {
		blacklist.add(user);
	}

	public boolean isBlacklisted(User user) {
		return blacklist.contains(user);
	}
}
