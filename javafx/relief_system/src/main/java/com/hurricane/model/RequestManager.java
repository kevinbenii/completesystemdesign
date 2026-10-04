package com.model;

import java.util.ArrayList;

public class RequestManager {
	private ArrayList<ReliefRequest> requests;

	public RequestManager(DataManager dataManager) {
		requests = dataManager.loadRequests();
	}

	public void submitRequest(ReliefRequest request) {
		requests.add(request);
	}

	public void deleteRequest(ReliefRequest request) {
		requests.remove(request);
	}

	public void markAsResolved(ReliefRequest request) {
		request.setStatus(ReliefRequestStatus.RESOLVED);
	}

	public ArrayList<ReliefRequest> filterBySkill(Skill skill) {
		ArrayList<ReliefRequest> matches = new ArrayList<ReliefRequest>();
		for (ReliefRequest request : requests) {
			if (request.needsSkill(skill))
				matches.add(request);
		}
		return matches;
	}

	public ArrayList<ReliefRequest> filterByLocation(Location location, double radius) {
		ArrayList<ReliefRequest> matches = new ArrayList<ReliefRequest>();
		for (ReliefRequest request : requests) {
			if (request.isNear(location, radius))
				matches.add(request);
		}
		return matches;
	}
}
