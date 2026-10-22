package com.hurricane.model;

/**
 * The stages a relief request goes through, from being submitted to being finished.
 */
public enum ReliefRequestStatus {
	/** Submitted and waiting for a volunteer */
	OPEN,
	/** A volunteer has taken the request and is handling it */
	ASSIGNED,
	/** A volunteer is actively working on the request */
	IN_PROGRESS,
	/** Marked finished (the older name for a completed request) */
	RESOLVED,
	/** Finished and closed by someone with permission to complete requests */
	COMPLETE,
	/** Removed by an admin */
	MODERATOR_REMOVED
}
