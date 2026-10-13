package com.hurricane.model;

public class Location {
	private String address;
	private String city;
	private String state;
	private String zipCode;
	private double latitude;
	private double longitude;

	public Location(String address, String city, String state, String zipCode, double latitude, double longitude) {
		this.address = address;
		this.city = city;
		this.state = state;
		this.zipCode = zipCode;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	// Getters for each address part, used when saving a Location to JSON
	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	private static final double EARTH_RADIUS_MILES = 3958.8;

	/**
	 * Straight-line ("as the crow flies") distance in miles, using the haversine
	 * formula, which accounts for the Earth being round. Not driving distance.
	 * Used by Shelter.isNear() for radius searches.
	 */
	public double distanceTo(Location other) {
		double dLat = Math.toRadians(other.latitude - latitude);
		double dLon = Math.toRadians(other.longitude - longitude);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(other.latitude))
				* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		return 2 * EARTH_RADIUS_MILES * Math.asin(Math.sqrt(a));
	}

	public String getFullAddress() {
		return address + ", " + city + ", " + state + " " + zipCode;
	}
}
