package com.hurricane.model;

import java.util.ArrayList;

/** Tracks all hurricanes */
public class HurricaneTracker {
	private ArrayList<Hurricane> hurricanes;
	private DataManager dataManager;

	/** Loads the hurricanes */
	public HurricaneTracker(DataManager dataManager) {
		this.dataManager = dataManager;
		hurricanes = dataManager.loadHurricanes();
	}

	/** Adds a hurricane */
	public void addHurricane(Hurricane hurricane) {
		hurricanes.add(hurricane);
	}

	/** Hurricanes within radius miles */
	public ArrayList<Hurricane> filterByLocation(Location location, double radius) {
		ArrayList<Hurricane> matches = new ArrayList<Hurricane>();
		for (Hurricane hurricane : hurricanes) {
			if (hurricane.getLocation().distanceTo(location) <= radius)
				matches.add(hurricane);
		}
		return matches;
	}

	/** Saves hurricanes to JSON */
	public void saveHurricanes() {
		dataManager.saveHurricanes(hurricanes);
	}
}
