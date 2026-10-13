package com.hurricane.model;

import java.util.ArrayList;

// Reads saved data (files, a database, ...) and turns it back into model objects
public interface DataLoader {
	ArrayList<User> loadUsers();

	ArrayList<ReliefRequest> loadRequests();

	ArrayList<Hurricane> loadHurricanes();

	ArrayList<Shelter> loadShelters();
}
