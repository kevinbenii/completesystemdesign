package com.hurricane.model;

import java.util.ArrayList;

public class Hurricane {
	private Location location;
	private int category;
	private double windSpeed;
	private ArrayList<Location> locationHistory;
	private ArrayList<Victim> affectedVictims;

	public Hurricane(Location location, int category, double windSpeed) {
		this.location = location;
		this.category = category;
		this.windSpeed = windSpeed;
		this.locationHistory = new ArrayList<Location>();
		this.affectedVictims = new ArrayList<Victim>();
		locationHistory.add(location);
	}

	public void updateData(Location newLocation, int category, double windSpeed) {
		this.location = newLocation;
		this.category = category;
		this.windSpeed = windSpeed;
		locationHistory.add(newLocation);
	}

	public Vector2 calculateDirection() {
		return new Vector2(1, 0);
	}

	public ArrayList<Location> getAffectedArea(double radius) {
		return new ArrayList<Location>();
	}
}
