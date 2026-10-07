package com.hurricane.model;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Reads and writes the model as JSON files. filePath is the directory that
 * holds users.json, requests.json, hurricanes.json and shelters.json.
 *
 * Records reference each other by username (requests -> requester and
 * volunteer, hurricanes -> affected victims, shelters -> occupants), so users are loaded once and
 * shared; that way every manager links to the same User objects.
 */
public class FileDataManager extends DataManager {
	private static final String USERS_FILE = "users.json";
	private static final String REQUESTS_FILE = "requests.json";
	private static final String HURRICANES_FILE = "hurricanes.json";
	private static final String SHELTERS_FILE = "shelters.json";

	// Cached after the first loadUsers() so everyone shares the same User objects
	private ArrayList<User> users;

	// filePath is a folder, e.g. "../../json", not a single file
	public FileDataManager(String filePath) {
		super(filePath);
	}

	// ---------- loading ----------

	// Reads users.json once; later calls return the same list
	public ArrayList<User> loadUsers() {
		if (users != null)
			return users;

		users = new ArrayList<User>();
		for (Object entry : readArray(USERS_FILE)) {
			users.add(parseUser((JSONObject) entry));
		}
		return users;
	}

	// Reads requests.json and links each request to its victim and volunteer objects
	public ArrayList<ReliefRequest> loadRequests() {
		Map<String, User> usersByName = usersByName();
		ArrayList<ReliefRequest> requests = new ArrayList<ReliefRequest>();

		for (Object entry : readArray(REQUESTS_FILE)) {
			JSONObject json = (JSONObject) entry;
			// The file stores a username; swap it for the real Victim (null if not found)
			Victim requester = asVictim(usersByName.get(json.get("requester")));

			ReliefRequest request = new ReliefRequest(
					parseLocation((JSONObject) json.get("location")),
					(String) json.get("subject"),
					(String) json.get("body"),
					parseSkills(getArray(json, "requiredSkills")),
					requester);

			// The constructor always sets OPEN, so restore the saved status
			if (json.get("status") != null)
				request.setStatus(ReliefRequestStatus.valueOf((String) json.get("status")));

			// assignedVolunteer is null in the file until someone takes the request
			User volunteer = usersByName.get(json.get("assignedVolunteer"));
			if (volunteer instanceof Volunteer)
				request.setAssignedVolunteer((Volunteer) volunteer);

			// Rebuild the victim's activeRequests list from here instead of trusting users.json
			if (requester != null && isActive(request) && !requester.getActiveRequests().contains(request))
				requester.getActiveRequests().add(request);

			requests.add(request);
		}
		return requests;
	}

	// Reads hurricanes.json, restores each storm's track, and links it to affected victims
	public ArrayList<Hurricane> loadHurricanes() {
		Map<String, User> usersByName = usersByName();
		ArrayList<Hurricane> hurricanes = new ArrayList<Hurricane>();

		for (Object entry : readArray(HURRICANES_FILE)) {
			JSONObject json = (JSONObject) entry;
			Location location = parseLocation((JSONObject) json.get("location"));
			Hurricane hurricane = new Hurricane(location,
					((Number) json.get("category")).intValue(),
					((Number) json.get("windSpeed")).doubleValue());

			// The constructor seeds the history with the current location; replace it
			// with the stored track, keeping the current location as the last point.
			ArrayList<Location> history = hurricane.getLocationHistory();
			history.clear();
			for (Object point : getArray(json, "locationHistory")) {
				history.add(parseLocation((JSONObject) point));
			}
			if (history.isEmpty() || !sameCoordinates(history.get(history.size() - 1), location))
				history.add(location);

			// Link both ways: hurricane -> victims and victim -> hurricane
			for (Object name : getArray(json, "affectedVictims")) {
				Victim victim = asVictim(usersByName.get(name));
				if (victim != null) {
					hurricane.getAffectedVictims().add(victim);
					victim.setAffectedByHurricane(hurricane);
				}
			}

			hurricanes.add(hurricane);
		}
		return hurricanes;
	}

	// Reads shelters.json and checks each listed occupant back into their shelter
	public ArrayList<Shelter> loadShelters() {
		Map<String, User> usersByName = usersByName();
		ArrayList<Shelter> shelters = new ArrayList<Shelter>();

		for (Object entry : readArray(SHELTERS_FILE)) {
			JSONObject json = (JSONObject) entry;
			Shelter shelter = new Shelter(
					(String) json.get("shelterId"),
					(String) json.get("name"),
					parseLocation((JSONObject) json.get("location")),
					((Number) json.get("capacity")).intValue());

			shelter.getAvailableSkills().addAll(parseSkills(getArray(json, "availableSkills")));

			// Re-check everyone in before applying the open flag, since checkIn()
			// refuses closed shelters. currentOccupancy is rebuilt from the occupant
			// list rather than trusted from the file, so the two can't disagree.
			for (Object name : getArray(json, "occupants")) {
				Victim victim = asVictim(usersByName.get(name));
				if (victim != null)
					shelter.checkIn(victim);
			}

			if (json.get("isOpen") != null)
				shelter.setOpen((Boolean) json.get("isOpen"));

			shelters.add(shelter);
		}
		return shelters;
	}

	// Builds the right User subclass (Admin, Volunteer or Victim) from one JSON entry
	private User parseUser(JSONObject json) {
		String username = (String) json.get("username");
		String password = (String) json.get("password");
		String phoneNumber = (String) json.get("phoneNumber");
		Location location = parseLocation((JSONObject) json.get("location"));

		switch (roleOf(json)) {
		case "ADMIN":
			Admin admin = new Admin(username, password, phoneNumber, location);
			// The Admin constructor defaults adminId to the username; keep the saved id instead
			if (json.get("adminId") != null)
				admin.setAdminId((String) json.get("adminId"));
			return admin;
		case "VOLUNTEER":
			return new Volunteer(username, password, phoneNumber, location, parseSkills(getArray(json, "skills")));
		default:
			return new Victim(username, password, phoneNumber, location);
		}
	}

	// Older entries have no "role", so fall back to the fields that only one subclass has.
	private String roleOf(JSONObject json) {
		if (json.get("role") != null)
			return ((String) json.get("role")).toUpperCase();
		if (json.containsKey("adminId"))
			return "ADMIN";
		if (json.containsKey("skills"))
			return "VOLUNTEER";
		return "VICTIM";
	}

	// Turns a {"address": ..., "latitude": ...} block into a Location
	private Location parseLocation(JSONObject json) {
		if (json == null)
			return null;

		// json-simple returns whole numbers as Long and decimals as Double, so read both as Number
		return new Location(
				(String) json.get("address"),
				(String) json.get("city"),
				(String) json.get("state"),
				(String) json.get("zipCode"),
				((Number) json.get("latitude")).doubleValue(),
				((Number) json.get("longitude")).doubleValue());
	}

	// Turns ["FOOD", "MEDICAL"] into Skill enum values; an unknown name throws
	private ArrayList<Skill> parseSkills(JSONArray json) {
		ArrayList<Skill> skills = new ArrayList<Skill>();
		for (Object name : json) {
			skills.add(Skill.valueOf((String) name));
		}
		return skills;
	}

	// ---------- saving ----------
	// Each save rewrites its whole file. LinkedHashMap keeps the fields in the
	// order they're put, so the files look the same every time.

	public void saveUsers(ArrayList<User> users) {
		List<Object> out = new ArrayList<Object>();
		for (User user : users) {
			out.add(userToJson(user));
		}
		writeArray(USERS_FILE, out);
	}

	// The requester and volunteer are saved as usernames, not whole user objects
	public void saveRequests(ArrayList<ReliefRequest> requests) {
		List<Object> out = new ArrayList<Object>();
		for (ReliefRequest request : requests) {
			Map<String, Object> json = new LinkedHashMap<String, Object>();
			json.put("location", locationToJson(request.getLocation()));
			json.put("status", request.getStatus().name());
			json.put("subject", request.getSubject());
			json.put("body", request.getBody());
			json.put("requiredSkills", skillsToJson(request.getRequiredSkills()));
			json.put("requester", usernameOf(request.getRequester()));
			json.put("assignedVolunteer", usernameOf(request.getAssignedVolunteer()));
			out.add(json);
		}
		writeArray(REQUESTS_FILE, out);
	}

	// Saves each storm's full track and its affected victims (by username)
	public void saveHurricanes(ArrayList<Hurricane> hurricanes) {
		List<Object> out = new ArrayList<Object>();
		for (Hurricane hurricane : hurricanes) {
			List<Object> history = new ArrayList<Object>();
			for (Location point : hurricane.getLocationHistory()) {
				history.add(locationToJson(point));
			}
			List<Object> victims = new ArrayList<Object>();
			for (Victim victim : hurricane.getAffectedVictims()) {
				victims.add(victim.getUsername());
			}

			Map<String, Object> json = new LinkedHashMap<String, Object>();
			json.put("location", locationToJson(hurricane.getLocation()));
			json.put("category", hurricane.getCategory());
			json.put("windSpeed", hurricane.getWindSpeed());
			json.put("locationHistory", history);
			json.put("affectedVictims", victims);
			out.add(json);
		}
		writeArray(HURRICANES_FILE, out);
	}

	// currentOccupancy is written for anyone reading the file; loadShelters() recalculates it
	public void saveShelters(ArrayList<Shelter> shelters) {
		List<Object> out = new ArrayList<Object>();
		for (Shelter shelter : shelters) {
			// Occupants are stored by username, like every other cross-reference
			List<Object> occupants = new ArrayList<Object>();
			for (Victim victim : shelter.getOccupants()) {
				occupants.add(victim.getUsername());
			}

			Map<String, Object> json = new LinkedHashMap<String, Object>();
			json.put("shelterId", shelter.getShelterId());
			json.put("name", shelter.getName());
			json.put("location", locationToJson(shelter.getLocation()));
			json.put("capacity", shelter.getCapacity());
			json.put("currentOccupancy", shelter.getCurrentOccupancy());
			json.put("occupants", occupants);
			json.put("availableSkills", skillsToJson(shelter.getAvailableSkills()));
			json.put("isOpen", shelter.isOpen());
			out.add(json);
		}
		writeArray(SHELTERS_FILE, out);
	}

	// Shared fields first, then a "role" plus whatever is specific to that kind of user
	private Map<String, Object> userToJson(User user) {
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("username", user.getUsername());
		// Plain text for now; hash passwords before this holds real accounts
		json.put("password", user.getPassword());
		json.put("phoneNumber", user.getPhoneNumber());
		json.put("location", locationToJson(user.getLocation()));

		if (user instanceof Admin) {
			json.put("role", "ADMIN");
			json.put("adminId", ((Admin) user).getAdminId());
		} else if (user instanceof Volunteer) {
			json.put("role", "VOLUNTEER");
			json.put("skills", skillsToJson(((Volunteer) user).getSkills()));
		} else {
			// The victim <-> hurricane link is stored on the hurricane (affectedVictims).
			// activeRequests is saved by subject only so the file is easy to read;
			// loadRequests() rebuilds the real list from requests.json.
			List<Object> subjects = new ArrayList<Object>();
			for (ReliefRequest request : ((Victim) user).getActiveRequests()) {
				subjects.add(request.getSubject());
			}
			json.put("role", "VICTIM");
			json.put("activeRequests", subjects);
		}
		return json;
	}

	// The reverse of parseLocation()
	private Map<String, Object> locationToJson(Location location) {
		if (location == null)
			return null;

		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put("address", location.getAddress());
		json.put("city", location.getCity());
		json.put("state", location.getState());
		json.put("zipCode", location.getZipCode());
		json.put("latitude", location.getLatitude());
		json.put("longitude", location.getLongitude());
		return json;
	}

	// The reverse of parseSkills(): Skill values -> ["FOOD", "MEDICAL"]
	private List<Object> skillsToJson(List<Skill> skills) {
		List<Object> names = new ArrayList<Object>();
		for (Skill skill : skills) {
			names.add(skill.name());
		}
		return names;
	}

	// ---------- helpers ----------

	// Username -> User lookup table, used to turn the usernames in the files back into objects
	private Map<String, User> usersByName() {
		Map<String, User> byName = new HashMap<String, User>();
		for (User user : loadUsers()) {
			byName.put(user.getUsername(), user);
		}
		return byName;
	}

	// Returns the user as a Victim, or null if they're missing or a different kind of user
	private static Victim asVictim(User user) {
		return user instanceof Victim ? (Victim) user : null;
	}

	// Null-safe getUsername(), for optional links like assignedVolunteer
	private static String usernameOf(User user) {
		return user == null ? null : user.getUsername();
	}

	// Resolved and removed requests don't count as a victim's active requests
	private static boolean isActive(ReliefRequest request) {
		return request.getStatus() == ReliefRequestStatus.OPEN || request.getStatus() == ReliefRequestStatus.IN_PROGRESS;
	}

	// Location has no equals(), so compare the points by latitude/longitude
	private static boolean sameCoordinates(Location a, Location b) {
		return a.getLatitude() == b.getLatitude() && a.getLongitude() == b.getLongitude();
	}

	// Returns the list under key, or an empty list if it's missing, so callers can loop safely
	private static JSONArray getArray(JSONObject json, String key) {
		Object value = json.get(key);
		return value instanceof JSONArray ? (JSONArray) value : new JSONArray();
	}

	// e.g. "../../json" + "users.json" -> "../../json/users.json"
	private Path pathOf(String fileName) {
		return Paths.get(filePath, fileName);
	}

	// A missing file just means no data yet. A malformed one throws, so a later
	// save can't silently overwrite it with an empty list.
	private JSONArray readArray(String fileName) {
		Path path = pathOf(fileName);
		if (!Files.exists(path))
			return new JSONArray();

		try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			return (JSONArray) new JSONParser().parse(reader);
		} catch (IOException e) {
			throw new UncheckedIOException("Could not read " + path, e);
		} catch (ParseException | ClassCastException e) {
			throw new IllegalStateException(path + " is not a valid JSON array", e);
		}
	}

	// Formats the data as indented JSON and overwrites the file, creating the folder if needed
	private void writeArray(String fileName, List<Object> data) {
		Path path = pathOf(fileName);
		StringBuilder out = new StringBuilder();
		writeJson(data, out, "");
		out.append('\n');

		try {
			Files.createDirectories(path.toAbsolutePath().getParent());
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				writer.write(out.toString());
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Could not write " + path, e);
		}
	}

	// json-simple only writes single-line JSON, so indent it to keep the files readable in git.
	private static void writeJson(Object value, StringBuilder out, String indent) {
		String inner = indent + "  ";

		if (value instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) value;
			if (map.isEmpty()) {
				out.append("{}");
				return;
			}
			out.append("{\n");
			int i = 0;
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				out.append(inner).append('"').append(JSONValue.escape(entry.getKey().toString())).append("\": ");
				writeJson(entry.getValue(), out, inner);
				out.append(++i < map.size() ? ",\n" : "\n");
			}
			out.append(indent).append('}');
		} else if (value instanceof List) {
			List<?> list = (List<?>) value;
			if (list.isEmpty()) {
				out.append("[]");
				return;
			}
			out.append("[\n");
			for (int i = 0; i < list.size(); i++) {
				out.append(inner);
				writeJson(list.get(i), out, inner);
				out.append(i + 1 < list.size() ? ",\n" : "\n");
			}
			out.append(indent).append(']');
		} else {
			out.append(JSONValue.toJSONString(value));
		}
	}
}
