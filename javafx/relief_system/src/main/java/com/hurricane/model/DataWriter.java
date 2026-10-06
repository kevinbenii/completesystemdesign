package com.hurricane.model;

import java.util.ArrayList;

public interface DataWriter {
	void saveUsers(ArrayList<User> users);

	void saveRequests(ArrayList<ReliefRequest> requests);

	void saveHurricanes(ArrayList<Hurricane> hurricanes);
}
