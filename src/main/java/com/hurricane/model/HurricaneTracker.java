package com.hurricane.model;

import java.util.ArrayList;

public class HurricaneTracker {
	private ArrayList<Hurricane> hurricanes;
	private DataManager dataManager;

	public HurricaneTracker(DataManager dataManager) {
		this.dataManager = dataManager;
		hurricanes = dataManager.loadHurricanes();
	}

	public void addHurricane(Hurricane hurricane) {
		hurricanes.add(hurricane);
	}

	//Hurricanes nearby
	public ArrayList<Hurricane> filterByLocation(Location location, double radius) {
		ArrayList<Hurricane> matches = new ArrayList<Hurricane>();
		for (Hurricane hurricane : hurricanes) {
			if (hurricane.getLocation().distanceTo(location) <= radius)
				matches.add(hurricane);
		}
		return matches;
	}

	public void saveHurricanes() {
		dataManager.saveHurricanes(hurricanes);
	}
}
