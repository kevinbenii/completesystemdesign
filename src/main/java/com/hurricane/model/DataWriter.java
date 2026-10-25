package com.hurricane.model;

import java.util.ArrayList;

/** Saves data */
public interface DataWriter {
	/** Saves users */
	void saveUsers(ArrayList<User> users);

	/** Saves requests */
	void saveRequests(ArrayList<ReliefRequest> requests);

	/** Saves hurricanes */
	void saveHurricanes(ArrayList<Hurricane> hurricanes);

	/** Saves shelters */
	void saveShelters(ArrayList<Shelter> shelters);
}
