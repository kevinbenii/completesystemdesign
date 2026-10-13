package com.hurricane.model;

import java.util.ArrayList;

public class UserManager {
	private ArrayList<User> users;
	private ArrayList<Admin> admins;
	private ArrayList<User> blacklist;
	private User currentUser;
	private DataManager dataManager;

	public UserManager(DataManager dataManager) {
		this.dataManager = dataManager;
		users = dataManager.loadUsers();
		admins = new ArrayList<Admin>();
		blacklist = new ArrayList<User>();
		if (users.isEmpty())
			addSampleUsers();
		for (User user : users) {
			if (user.isBanned())
				blacklist.add(user);
		}
	}

	// Sample data from json/users.json to validate against until the DataLoader reads the file
	// Note: FileDataManager now loads users.json, so this only runs if that file is missing or
	// empty. If these sample users show up, the data folder path is probably wrong
	// (see FileDataManager.defaultDataFolder()). Safe to remove once everyone has merged.
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

	// Only a logged-in admin can ban. A banned user who is logged in gets logged out.
	// Returns false if the ban was refused; the caller saves users when it returns true.
	public boolean banUser(User user) {
		if (!(currentUser instanceof Admin) || user == null)
			return false;
		user.setBanned(true);
		if (!blacklist.contains(user))
			blacklist.add(user);
		if (currentUser == user)
			logout();
		return true;
	}

	public boolean isBlacklisted(User user) {
		return blacklist.contains(user);
	}

	public void saveUsers() {
		dataManager.saveUsers(users);
	}
}
