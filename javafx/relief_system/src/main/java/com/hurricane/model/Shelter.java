package com.hurricane.model;

import java.util.ArrayList;

/**
 * A physical shelter that victims can check into. Tracks how many people it
 * can hold, who is currently there, and which kinds of help (skills) it offers.
 */
public class Shelter {
	private String shelterId;
	private String name;
	private Location location;
	private int capacity;
	private int currentOccupancy;
	private ArrayList<Victim> occupants;
	// The kinds of help this shelter provides, e.g. FOOD or MEDICAL
	private ArrayList<Skill> availableSkills;
	private boolean isOpen;

	// New shelters start open and empty
	public Shelter(String shelterId, String name, Location location, int capacity) {
		this.shelterId = shelterId;
		this.name = name;
		this.location = location;
		this.capacity = capacity;
		this.currentOccupancy = 0;
		this.occupants = new ArrayList<Victim>();
		this.availableSkills = new ArrayList<Skill>();
		this.isOpen = true;
	}

	// Getters, used by the UI to display a shelter and by the writer to save it
	public String getShelterId() {
		return shelterId;
	}

	public String getName() {
		return name;
	}

	public Location getLocation() {
		return location;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getCurrentOccupancy() {
		return currentOccupancy;
	}

	public ArrayList<Victim> getOccupants() {
		return occupants;
	}

	public ArrayList<Skill> getAvailableSkills() {
		return availableSkills;
	}

	public boolean isOpen() {
		return isOpen;
	}

	// A closed shelter never has space, even if it has empty beds
	public boolean hasSpace() {
		return isOpen && currentOccupancy < capacity;
	}

	// Empty beds left (never negative)
	public int getAvailableSpace() {
		return Math.max(0, capacity - currentOccupancy);
	}

	// True if this shelter provides that kind of help
	public boolean offersSkill(Skill skill) {
		return availableSkills.contains(skill);
	}

	/**
	 * Adds the victim to this shelter. Returns false (and changes nothing) if the
	 * shelter is closed, full, or the victim is already checked in here.
	 */
	public boolean checkIn(Victim victim) {
		if (!hasSpace() || occupants.contains(victim))
			return false;

		occupants.add(victim);
		currentOccupancy++;
		return true;
	}

	// Removes the victim if they are here; does nothing otherwise
	public void checkOut(Victim victim) {
		if (occupants.remove(victim))
			currentOccupancy--;
	}

	// True if the shelter is within radius miles of targetLocation
	public boolean isNear(Location targetLocation, double radius) {
		return location.distanceTo(targetLocation) <= radius;
	}

	// Closing a shelter doesn't remove the people inside; it only blocks new check-ins
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
}
