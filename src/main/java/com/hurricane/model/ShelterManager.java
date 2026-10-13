package com.hurricane.model;

import java.util.ArrayList;

/**
 * Holds every shelter in memory while the app runs and answers questions
 * like "which open shelters near me have space?". Loads its data from the
 * DataManager at startup and writes it back with saveShelters().
 */
public class ShelterManager {
	private ArrayList<Shelter> shelters;
	// Kept so saveShelters() knows where to write
	private DataManager dataManager;

	public ShelterManager(DataManager dataManager) {
		this.dataManager = dataManager;
		shelters = dataManager.loadShelters();
	}

	// add/remove only change the in-memory list; call saveShelters() to persist
	public void addShelter(Shelter shelter) {
		shelters.add(shelter);
	}

	public void removeShelter(Shelter shelter) {
		shelters.remove(shelter);
	}

	// Returns null if no shelter has that id
	public Shelter getShelterById(String shelterId) {
		for (Shelter shelter : shelters) {
			if (shelter.getShelterId().equals(shelterId))
				return shelter;
		}
		return null;
	}

	// All shelters within radius miles of location, open or not
	public ArrayList<Shelter> filterByLocation(Location location, double radius) {
		ArrayList<Shelter> matches = new ArrayList<Shelter>();
		for (Shelter shelter : shelters) {
			if (shelter.isNear(location, radius))
				matches.add(shelter);
		}
		return matches;
	}

	// All shelters that offer the given kind of help
	public ArrayList<Shelter> filterBySkill(Skill skill) {
		ArrayList<Shelter> matches = new ArrayList<Shelter>();
		for (Shelter shelter : shelters) {
			if (shelter.offersSkill(skill))
				matches.add(shelter);
		}
		return matches;
	}

	// Shelters near location that are open and not full, i.e. where someone could go right now
	public ArrayList<Shelter> findAvailableShelters(Location location, double radius) {
		ArrayList<Shelter> matches = new ArrayList<Shelter>();
		for (Shelter shelter : filterByLocation(location, radius)) {
			if (shelter.hasSpace())
				matches.add(shelter);
		}
		return matches;
	}

	/**
	 * Moves the victim into the given shelter. A victim can only be in one
	 * shelter at a time, so they are checked out of any other shelter first.
	 * Returns false if the target shelter is closed or full.
	 */
	public boolean assignVictimToShelter(Victim victim, Shelter shelter) {
		if (!shelter.hasSpace())
			return false;

		for (Shelter other : shelters) {
			if (other != shelter)
				other.checkOut(victim);
		}
		return shelter.checkIn(victim);
	}

	public ArrayList<Shelter> getAllShelters() {
		return shelters;
	}

	// Writes every shelter to storage (shelters.json for FileDataManager)
	public void saveShelters() {
		dataManager.saveShelters(shelters);
	}
}
