package com.hurricane.model;

import java.util.ArrayList;

/** Loads saved data */
public interface DataLoader {
	/** Loads users */
	ArrayList<User> loadUsers();

	/** Loads requests */
	ArrayList<ReliefRequest> loadRequests();

	/** Loads hurricanes */
	ArrayList<Hurricane> loadHurricanes();

	/** Loads shelters */
	ArrayList<Shelter> loadShelters();
}
