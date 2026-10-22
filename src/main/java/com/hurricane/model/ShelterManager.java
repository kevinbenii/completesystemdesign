package com.hurricane.model;

import java.util.ArrayList;

/** Tracks all shelters */
public class ShelterManager {
	private ArrayList<Shelter> shelters;
	//Where to save
	private DataManager dataManager;

	/** Loads the shelters */
	public ShelterManager(DataManager dataManager) {
		this.dataManager = dataManager;
		shelters = dataManager.loadShelters();
	}

	/** Adds a shelter */
	public void addShelter(Shelter shelter) {
		shelters.add(shelter);
	}

	/** Removes a shelter */
	public void removeShelter(Shelter shelter) {
		shelters.remove(shelter);
	}

	/** Finds a shelter by id, or null */
	public Shelter getShelterById(String shelterId) {
		for (Shelter shelter : shelters) {
			if (shelter.getShelterId().equals(shelterId))
				return shelter;
		}
		return null;
	}

	/** Shelters within radius miles */
	public ArrayList<Shelter> filterByLocation(Location location, double radius) {
		ArrayList<Shelter> matches = new ArrayList<Shelter>();
		for (Shelter shelter : shelters) {
			if (shelter.isNear(location, radius))
				matches.add(shelter);
		}
		return matches;
	}

	/** Shelters that offer that help */
	public ArrayList<Shelter> filterBySkill(Skill skill) {
		ArrayList<Shelter> matches = new ArrayList<Shelter>();
		for (Shelter shelter : shelters) {
			if (shelter.offersSkill(skill))
				matches.add(shelter);
		}
		return matches;
	}

	/** Open shelters with space nearby */
	public ArrayList<Shelter> findAvailableShelters(Location location, double radius) {
		ArrayList<Shelter> matches = new ArrayList<Shelter>();
		for (Shelter shelter : filterByLocation(location, radius)) {
			if (shelter.hasSpace())
				matches.add(shelter);
		}
		return matches;
	}

	/** Moves a victim into a shelter */
	public boolean assignVictimToShelter(Victim victim, Shelter shelter) {
		if (!shelter.hasSpace())
			return false;

		for (Shelter other : shelters) {
			if (other != shelter)
				other.checkOut(victim);
		}
		return shelter.checkIn(victim);
	}

	/** All shelters */
	public ArrayList<Shelter> getAllShelters() {
		return shelters;
	}

	/** Saves shelters to JSON */
	public void saveShelters() {
		dataManager.saveShelters(shelters);
	}
}
