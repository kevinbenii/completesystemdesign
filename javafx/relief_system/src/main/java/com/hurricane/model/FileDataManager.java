package com.model;

import java.util.ArrayList;

public class FileDataManager extends DataManager {

	public FileDataManager(String filePath) {
		super(filePath);
	}

	public ArrayList<User> loadUsers() {
		return new ArrayList<User>();
	}

	public ArrayList<ReliefRequest> loadRequests() {
		return new ArrayList<ReliefRequest>();
	}

	public ArrayList<Hurricane> loadHurricanes() {
		return new ArrayList<Hurricane>();
	}

	public void saveUsers(ArrayList<User> users) {
		// write the users to the file
	}

	public void saveRequests(ArrayList<ReliefRequest> requests) {
		// write the requests to the file
	}

	public void saveHurricanes(ArrayList<Hurricane> hurricanes) {
		// write the hurricanes to the file
	}
}
