package com.hurricane.model;

import java.util.UUID;

/** System's user */
public abstract class User {
	protected UUID id;
	protected String username;
	protected String password;
	protected String phoneNumber;
	protected Location location;
	protected boolean banned;

	/** Makes a user */
	public User(String username, String password, String phoneNumber, Location location) {
		this.id = UUID.randomUUID();
		this.username = username;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.location = location;
	}

	/** True if the login matches */
	public boolean login(String username, String password) {
		return this.username.equals(username) && this.password.equals(password);
	}

	/** Id */
	public UUID getId() {
		return id;
	}

	/** Restores a saved id */
	public void setId(UUID id) {
		this.id = id;
	}

	/** Username */
	public String getUsername() {
		return username;
	}

	/** Password */
	public String getPassword() {
		return password;
	}

	/** Phone number */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/** Location */
	public Location getLocation() {
		return location;
	}

	/** Moves the user */
	public void updateLocation(Location newLocation) {
		this.location = newLocation;
	}

	/** True if banned */
	public boolean isBanned() {
		return banned;
	}

	/** Bans or unbans the user */
	public void setBanned(boolean banned) {
		this.banned = banned;
	}
}
