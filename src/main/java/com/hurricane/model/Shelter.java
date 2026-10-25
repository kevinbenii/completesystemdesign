package com.hurricane.model;

import java.util.ArrayList;

/** Shelter for victims */
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
	private boolean acceptsDogs;
	private boolean hasMedicalStaff;
	//How many pets it has room for
	private int petCapacity;
	//Pets staying here
	private ArrayList<Pet> pets;

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
		this.acceptsDogs = false;
		this.hasMedicalStaff = false;
		this.petCapacity = 0;
		this.pets = new ArrayList<Pet>();
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

	/** True if open and there is room for that many people and pets */
	public boolean hasSpaceFor(int people, int petCount) {
		return isOpen && capacity - currentOccupancy >= people && petCapacity - pets.size() >= petCount;
	}

	/** Pets staying here */
	public ArrayList<Pet> getPets() {
		return pets;
	}

	/** Records a pet staying here */
	public void addPet(Pet pet) {
		pets.add(pet);
	}

	/** True if it offers that help */
	public boolean offersSkill(Skill skill) {
		return availableSkills.contains(skill);
	}

	/** Checks a victim in */
	public boolean checkIn(Victim victim) {
		int people = groupSize(victim);
		if (!isOpen || capacity - currentOccupancy < people || occupants.contains(victim))
			return false;

		occupants.add(victim);
		currentOccupancy += people;
		return true;
	}

	/** Checks a victim out */
	public void checkOut(Victim victim) {
		if (occupants.remove(victim))
			currentOccupancy -= groupSize(victim);
	}

	//Victim plus family, at least 1
	private int groupSize(Victim victim) {
		return Math.max(1, victim.getFamilyMembers().size());
	}

	/** Within radius miles */
	public boolean isNear(Location targetLocation, double radius) {
		return location.distanceTo(targetLocation) <= radius;
	}

	/** Opens or closes the shelter */
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	/** True if dogs are allowed */
	public boolean acceptsDogs() {
		return acceptsDogs;
	}

	/** Sets whether dogs are allowed */
	public void setAcceptsDogs(boolean acceptsDogs) {
		this.acceptsDogs = acceptsDogs;
	}

	/** True if it has medical staff */
	public boolean hasMedicalStaff() {
		return hasMedicalStaff;
	}

	/** Sets whether it has medical staff */
	public void setHasMedicalStaff(boolean hasMedicalStaff) {
		this.hasMedicalStaff = hasMedicalStaff;
	}

	/** Room for pets */
	public int getPetCapacity() {
		return petCapacity;
	}

	/** Sets the room for pets */
	public void setPetCapacity(int petCapacity) {
		this.petCapacity = petCapacity;
	}
}
