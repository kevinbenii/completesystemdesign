package com.hurricane.model;

import java.util.ArrayList;

// Takes the in-memory model objects and persists them (files, a database, ...)
public interface DataWriter {
	void saveUsers(ArrayList<User> users);

	void saveRequests(ArrayList<ReliefRequest> requests);

	void saveHurricanes(ArrayList<Hurricane> hurricanes);

	void saveShelters(ArrayList<Shelter> shelters);
}
