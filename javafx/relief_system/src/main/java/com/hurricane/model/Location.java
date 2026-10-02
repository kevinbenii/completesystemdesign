package com.model;

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

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getFullAddress() {
		return address + ", " + city + ", " + state + " " + zipCode;
	}
}
