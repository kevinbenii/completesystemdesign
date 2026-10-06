package com.hurricane.model;

import java.util.ArrayList;

public class UserManager {
	private ArrayList<User> users;
	private ArrayList<Admin> admins;
	private ArrayList<User> blacklist;

	public UserManager(DataManager dataManager) {
		users = dataManager.loadUsers();
		admins = new ArrayList<Admin>();
		blacklist = new ArrayList<User>();
	}

	public void registerUser(User user) {
		users.add(user);
	}

	public boolean authenticateUser(String username, String password) {
		return true;
	}

	public void banUser(User user) {
		blacklist.add(user);
	}

	public boolean isBlacklisted(User user) {
		return blacklist.contains(user);
	}
}
