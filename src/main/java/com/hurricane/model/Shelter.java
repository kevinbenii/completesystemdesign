package com.hurricane.model;

import java.util.ArrayList;

/** A shelter for victims */
public class Shelter {
	private String shelterId;
	private String name;
	private Location location;
	private int capacity;
	private int currentOccupancy;
	private ArrayList<Victim> occupants;
	//Help offered, e.g. FOOD
	private ArrayList<Skill> availableSkills;
	private boolean isOpen;

	/** Makes an open, empty shelter */
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

	/** Shelter id */
	public String getShelterId() {
		return shelterId;
	}

	/** Name */
	public String getName() {
		return name;
	}

	/** Location */
	public Location getLocation() {
		return location;
	}

	/** Total beds */
	public int getCapacity() {
		return capacity;
	}

	/** People inside */
	public int getCurrentOccupancy() {
		return currentOccupancy;
	}

	/** Victims inside */
	public ArrayList<Victim> getOccupants() {
		return occupants;
	}

	/** Help offered */
	public ArrayList<Skill> getAvailableSkills() {
		return availableSkills;
	}

	/** True if open */
	public boolean isOpen() {
		return isOpen;
	}

	/** True if open and not full */
	public boolean hasSpace() {
		return isOpen && currentOccupancy < capacity;
	}

	/** Free beds */
	public int getAvailableSpace() {
		return Math.max(0, capacity - currentOccupancy);
	}

	/** True if it offers that help */
	public boolean offersSkill(Skill skill) {
		return availableSkills.contains(skill);
	}

	/** Checks a victim in */
	public boolean checkIn(Victim victim) {
		if (!hasSpace() || occupants.contains(victim))
			return false;

		occupants.add(victim);
		currentOccupancy++;
		return true;
	}

	/** Checks a victim out */
	public void checkOut(Victim victim) {
		if (occupants.remove(victim))
			currentOccupancy--;
	}

	/** Within radius miles */
	public boolean isNear(Location targetLocation, double radius) {
		return location.distanceTo(targetLocation) <= radius;
	}

	/** Opens or closes the shelter */
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
}
