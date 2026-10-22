package com.hurricane.model;

import java.util.ArrayList;

/** Tracks all requests */
public class RequestManager {
	private ArrayList<ReliefRequest> requests;
	private DataManager dataManager;

	/** Loads the requests */
	public RequestManager(DataManager dataManager) {
		this.dataManager = dataManager;
		requests = dataManager.loadRequests();
	}

	/** Adds a request */
	public void submitRequest(ReliefRequest request) {
		requests.add(request);
	}

	/** Removes a request */
	public void deleteRequest(ReliefRequest request) {
		requests.remove(request);
	}

	/** Marks a request resolved */
	public void markAsResolved(ReliefRequest request) {
		request.setStatus(ReliefRequestStatus.RESOLVED);
	}

	/** Requests that need that skill */
	public ArrayList<ReliefRequest> filterBySkill(Skill skill) {
		ArrayList<ReliefRequest> matches = new ArrayList<ReliefRequest>();
		for (ReliefRequest request : requests) {
			if (request.needsSkill(skill))
				matches.add(request);
		}
		return matches;
	}

	/** Requests within radius miles */
	public ArrayList<ReliefRequest> filterByLocation(Location location, double radius) {
		ArrayList<ReliefRequest> matches = new ArrayList<ReliefRequest>();
		for (ReliefRequest request : requests) {
			if (request.isNear(location, radius))
				matches.add(request);
		}
		return matches;
	}

	/** All requests */
	public ArrayList<ReliefRequest> getAllRequests() {
		return requests;
	}

	/** Saves requests to JSON */
	public void saveRequests() {
		dataManager.saveRequests(requests);
	}
}
