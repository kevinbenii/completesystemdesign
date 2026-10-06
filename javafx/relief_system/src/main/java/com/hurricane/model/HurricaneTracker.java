package com.hurricane.model;

import java.util.ArrayList;

public class HurricaneTracker {
	private ArrayList<Hurricane> hurricanes;

	public HurricaneTracker(DataManager dataManager) {
		hurricanes = dataManager.loadHurricanes();
	}

	public void addHurricane(Hurricane hurricane) {
		hurricanes.add(hurricane);
	}

	public ArrayList<Hurricane> filterByLocation(Location location) {
		return new ArrayList<Hurricane>(hurricanes);
	}
}
