package com.hurricane.model;

import java.util.UUID;

public abstract class User {
	protected UUID id;
	protected String username;
	protected String password;
	protected String phoneNumber;
	protected Location location;

	public User(String username, String password, String phoneNumber, Location location) {
		this.id = UUID.randomUUID();
		this.username = username;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.location = location;
	}

	public boolean login(String username, String password) {
		return this.username.equals(username) && this.password.equals(password);
	}

	public void register() {

	}

	public UUID getId() {
		return id;
	}

	// Loader uses this to restore a saved id
	public void setId(UUID id) {
		this.id = id;
	}

	// Getters for the basic account info, used for login and when saving users to JSON.
	// The username is also how other records (requests, shelters, ...) refer to a user.
	// Merge note: getUsername() and getPhoneNumber() were added on both main and
	// my-experiement, which merged cleanly but compiled as duplicates; keep only one copy.
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public Location getLocation() {
		return location;
	}

	public void updateLocation(Location newLocation) {
		this.location = newLocation;
	}
}
