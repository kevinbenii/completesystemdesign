package com.model;

public abstract class User {
	protected String username;
	protected String password;
	protected String phoneNumber;
	protected Location location;

	public User(String username, String password, String phoneNumber, Location location) {
		this.username = username;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.location = location;
	}

	public boolean login(String username, String password) {
		return true;
	}

	public void register() {

	}

	public Location getLocation() {
		return location;
	}

	public void updateLocation(Location newLocation) {
		this.location = newLocation;
	}
}
