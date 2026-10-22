package com.hurricane.model;

// File names and JSON keys shared by the loader and writer
public final class DataConstants {
	private DataConstants() {
	}

	// Files
	public static final String USERS_FILE = "users.json";
	public static final String REQUESTS_FILE = "requests.json";
	public static final String HURRICANES_FILE = "hurricanes.json";
	public static final String SHELTERS_FILE = "shelters.json";

	// Shared
	public static final String ID = "id";
	public static final String LOCATION = "location";

	// Location
	public static final String ADDRESS = "address";
	public static final String CITY = "city";
	public static final String STATE = "state";
	public static final String ZIP_CODE = "zipCode";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";

	// Users
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String PHONE_NUMBER = "phoneNumber";
	public static final String ROLE = "role";
	public static final String ADMIN_ID = "adminId";
	public static final String SKILLS = "skills";
	public static final String SHELTER_ID = "shelterId";
	public static final String ACTIVE_REQUESTS = "activeRequests";
	public static final String BANNED = "banned";
	public static final String ROLE_ADMIN = "ADMIN";
	public static final String ROLE_VOLUNTEER = "VOLUNTEER";
	public static final String ROLE_VICTIM = "VICTIM";
	public static final String ROLE_SHELTER_STAFF = "SHELTER_STAFF";

	// Requests
	public static final String STATUS = "status";
	public static final String SUBJECT = "subject";
	public static final String BODY = "body";
	public static final String REQUIRED_SKILLS = "requiredSkills";
	public static final String REQUESTER = "requester";
	public static final String ASSIGNED_VOLUNTEER = "assignedVolunteer";

	// Hurricanes
	public static final String CATEGORY = "category";
	public static final String WIND_SPEED = "windSpeed";
	public static final String LOCATION_HISTORY = "locationHistory";
	public static final String AFFECTED_VICTIMS = "affectedVictims";

	// Shelters
	public static final String SHELTER_ID = "shelterId";
	public static final String NAME = "name";
	public static final String CAPACITY = "capacity";
	public static final String CURRENT_OCCUPANCY = "currentOccupancy";
	public static final String OCCUPANTS = "occupants";
	public static final String AVAILABLE_SKILLS = "availableSkills";
	public static final String IS_OPEN = "isOpen";
}
