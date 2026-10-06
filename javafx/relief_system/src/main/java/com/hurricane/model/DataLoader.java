package com.hurricane.model;

import java.util.ArrayList;

public interface DataLoader {
	ArrayList<User> loadUsers();

	ArrayList<ReliefRequest> loadRequests();

	ArrayList<Hurricane> loadHurricanes();
}
