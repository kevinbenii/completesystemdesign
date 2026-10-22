package com.hurricane.model;

import com.location.Address;

/** Location */
public class Location {
	private String address;
	private String city;
	private String state;
	private String zipCode;
	private double latitude;
	private double longitude;

	/** Makes a location */
	public Location(String address, String city, String state, String zipCode, double latitude, double longitude) {
		this.address = address;
		this.city = city;
		this.state = state;
		this.zipCode = zipCode;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/** Street address */
	public String getAddress() {
		return address;
	}

	/** City */
	public String getCity() {
		return city;
	}

	/** State */
	public String getState() {
		return state;
	}

	/** Zip code */
	public String getZipCode() {
		return zipCode;
	}

	/** Latitude */
	public double getLatitude() {
		return latitude;
	}

	/** Longitude */
	public double getLongitude() {
		return longitude;
	}

	private static final double EARTH_RADIUS_MILES = 3958.8;

	/** Distance in miles */
	public double distanceTo(Location other) {
		double dLat = Math.toRadians(other.latitude - latitude);
		double dLon = Math.toRadians(other.longitude - longitude);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(other.latitude))
				* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		return 2 * EARTH_RADIUS_MILES * Math.asin(Math.sqrt(a));
	}

	/** Full address */
	public String getFullAddress() {
		return address + ", " + city + ", " + state + " " + zipCode;
	}

	/** Address for the map tools */
	public Address toAddress() {
		return new Address(address, city, state, zipCode);
	}
}
