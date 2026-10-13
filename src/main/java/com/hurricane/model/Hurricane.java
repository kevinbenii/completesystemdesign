package com.hurricane.model;

import java.util.ArrayList;
import java.util.UUID;

public class Hurricane {
	private UUID id;
	private Location location;
	private int category;
	private double windSpeed;
	private ArrayList<Location> locationHistory;
	private ArrayList<Victim> affectedVictims;

	public Hurricane(Location location, int category, double windSpeed) {
		this.id = UUID.randomUUID();
		this.location = location;
		this.category = category;
		this.windSpeed = windSpeed;
		this.locationHistory = new ArrayList<Location>();
		this.affectedVictims = new ArrayList<Victim>();
		locationHistory.add(location);
	}

	public UUID getId() {
		return id;
	}

	// Loader uses this to restore a saved id
	public void setId(UUID id) {
		this.id = id;
	}

	// Getters used by the writer to save a hurricane.
	// locationHistory is the storm's track, oldest first; its last point is the current location.
	public Location getLocation() {
		return location;
	}

	public int getCategory() {
		return category;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public ArrayList<Location> getLocationHistory() {
		return locationHistory;
	}

	public ArrayList<Victim> getAffectedVictims() {
		return affectedVictims;
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
