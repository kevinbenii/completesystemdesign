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
import java.util.UUID;
import java.util.function.Consumer;

import static com.hurricane.model.DataConstants.*;

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

	// Cached after the first loadUsers() so everyone shares the same User objects
	private ArrayList<User> users;

	// filePath is a folder, e.g. "../../json", not a single file
	public FileDataManager(String filePath) {
		super(filePath);
	}

	/**
	 * Finds the repo's json/ folder. Paths are relative to wherever the app was
	 * started: running from the repo root (e.g. VS Code's Run button) needs "json",
	 * while `mvn javafx:run` starts in javafx/relief_system and needs "../../json".
	 * Use this instead of hardcoding a path, so every entry point loads the same data.
	 */
	public static String defaultDataFolder() {
		if (Files.isDirectory(Paths.get("json")))
			return "json";
		return "../../json";
	}

	// ---------- loading ----------

	// Reads users.json once; later calls return the same list
	public ArrayList<User> loadUsers() {
		if (users != null)
			return users;

		users = new ArrayList<User>();
		for (Object entry : readArray(USERS_FILE)) {
			JSONObject json = (JSONObject) entry;
			User user = parseUser(json);
			restoreId(json, user::setId);
			// Older entries have no "banned" field, which means not banned
			user.setBanned(Boolean.TRUE.equals(json.get(BANNED)));
			users.add(user);
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
			Victim requester = asVictim(usersByName.get(json.get(REQUESTER)));

			ReliefRequest request = new ReliefRequest(
					parseLocation((JSONObject) json.get(LOCATION)),
					(String) json.get(SUBJECT),
					(String) json.get(BODY),
					parseSkills(getArray(json, REQUIRED_SKILLS)),
					requester);

			// The constructor always sets OPEN, so restore the saved status
			if (json.get(STATUS) != null)
				request.setStatus(ReliefRequestStatus.valueOf((String) json.get(STATUS)));

			// assignedVolunteer is null in the file until someone takes the request
			User volunteer = usersByName.get(json.get(ASSIGNED_VOLUNTEER));
			if (volunteer instanceof Volunteer)
				request.setAssignedVolunteer((Volunteer) volunteer);

			// Rebuild the victim's activeRequests list from here instead of trusting users.json
			if (requester != null && isActive(request) && !requester.getActiveRequests().contains(request))
				requester.getActiveRequests().add(request);

			restoreId(json, request::setId);
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
			Location location = parseLocation((JSONObject) json.get(LOCATION));
			Hurricane hurricane = new Hurricane(location,
					((Number) json.get(CATEGORY)).intValue(),
					((Number) json.get(WIND_SPEED)).doubleValue());

			// The constructor seeds the history with the current location; replace it
			// with the stored track, keeping the current location as the last point.
			ArrayList<Location> history = hurricane.getLocationHistory();
			history.clear();
			for (Object point : getArray(json, LOCATION_HISTORY)) {
				history.add(parseLocation((JSONObject) point));
			}
			if (history.isEmpty() || !sameCoordinates(history.get(history.size() - 1), location))
				history.add(location);

			// Link both ways: hurricane -> victims and victim -> hurricane
			for (Object name : getArray(json, AFFECTED_VICTIMS)) {
				Victim victim = asVictim(usersByName.get(name));
				if (victim != null) {
					hurricane.getAffectedVictims().add(victim);
					victim.setAffectedByHurricane(hurricane);
				}
			}

			restoreId(json, hurricane::setId);
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
					(String) json.get(SHELTER_ID),
					(String) json.get(NAME),
					parseLocation((JSONObject) json.get(LOCATION)),
					((Number) json.get(CAPACITY)).intValue());

			shelter.getAvailableSkills().addAll(parseSkills(getArray(json, AVAILABLE_SKILLS)));

			// Re-check everyone in before applying the open flag, since checkIn()
			// refuses closed shelters. currentOccupancy is rebuilt from the occupant
			// list rather than trusted from the file, so the two can't disagree.
			for (Object name : getArray(json, OCCUPANTS)) {
				Victim victim = asVictim(usersByName.get(name));
				if (victim != null)
					shelter.checkIn(victim);
			}

			if (json.get(IS_OPEN) != null)
				shelter.setOpen((Boolean) json.get(IS_OPEN));

			shelters.add(shelter);
		}
		return shelters;
	}

	// Builds the right User subclass (Admin, Volunteer, ShelterStaff or Victim) from one JSON entry
	private User parseUser(JSONObject json) {
		String username = (String) json.get(USERNAME);
		String password = (String) json.get(PASSWORD);
		String phoneNumber = (String) json.get(PHONE_NUMBER);
		Location location = parseLocation((JSONObject) json.get(LOCATION));

		switch (roleOf(json)) {
		case ROLE_ADMIN:
			Admin admin = new Admin(username, password, phoneNumber, location);
			// The Admin constructor defaults adminId to the username; keep the saved id instead
			if (json.get(ADMIN_ID) != null)
				admin.setAdminId((String) json.get(ADMIN_ID));
			return admin;
		case ROLE_VOLUNTEER:
			return new Volunteer(username, password, phoneNumber, location, parseSkills(getArray(json, SKILLS)));
		case ROLE_SHELTER_STAFF:
			return new ShelterStaff(username, password, phoneNumber, location, (String) json.get(SHELTER_ID));
		default:
			return new Victim(username, password, phoneNumber, location);
		}
	}

	// Older entries have no "role", so fall back to the fields that only one subclass has.
	private String roleOf(JSONObject json) {
		if (json.get(ROLE) != null)
			return ((String) json.get(ROLE)).toUpperCase();
		if (json.containsKey(ADMIN_ID))
			return ROLE_ADMIN;
		if (json.containsKey(SKILLS))
			return ROLE_VOLUNTEER;
		if (json.containsKey(SHELTER_ID))
			return ROLE_SHELTER_STAFF;
		return ROLE_VICTIM;
	}

	// Turns a {"address": ..., "latitude": ...} block into a Location
	private Location parseLocation(JSONObject json) {
		if (json == null)
			return null;

		// json-simple returns whole numbers as Long and decimals as Double, so read both as Number
		return new Location(
				(String) json.get(ADDRESS),
				(String) json.get(CITY),
				(String) json.get(STATE),
				(String) json.get(ZIP_CODE),
				((Number) json.get(LATITUDE)).doubleValue(),
				((Number) json.get(LONGITUDE)).doubleValue());
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
			json.put(ID, request.getId().toString());
			json.put(LOCATION, locationToJson(request.getLocation()));
			json.put(STATUS, request.getStatus().name());
			json.put(SUBJECT, request.getSubject());
			json.put(BODY, request.getBody());
			json.put(REQUIRED_SKILLS, skillsToJson(request.getRequiredSkills()));
			json.put(REQUESTER, usernameOf(request.getRequester()));
			json.put(ASSIGNED_VOLUNTEER, usernameOf(request.getAssignedVolunteer()));
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
			json.put(ID, hurricane.getId().toString());
			json.put(LOCATION, locationToJson(hurricane.getLocation()));
			json.put(CATEGORY, hurricane.getCategory());
			json.put(WIND_SPEED, hurricane.getWindSpeed());
			json.put(LOCATION_HISTORY, history);
			json.put(AFFECTED_VICTIMS, victims);
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
			json.put(SHELTER_ID, shelter.getShelterId());
			json.put(NAME, shelter.getName());
			json.put(LOCATION, locationToJson(shelter.getLocation()));
			json.put(CAPACITY, shelter.getCapacity());
			json.put(CURRENT_OCCUPANCY, shelter.getCurrentOccupancy());
			json.put(OCCUPANTS, occupants);
			json.put(AVAILABLE_SKILLS, skillsToJson(shelter.getAvailableSkills()));
			json.put(IS_OPEN, shelter.isOpen());
			out.add(json);
		}
		writeArray(SHELTERS_FILE, out);
	}

	// Shared fields first, then a "role" plus whatever is specific to that kind of user
	private Map<String, Object> userToJson(User user) {
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put(ID, user.getId().toString());
		json.put(USERNAME, user.getUsername());
		// Plain text for now; hash passwords before this holds real accounts
		json.put(PASSWORD, user.getPassword());
		json.put(PHONE_NUMBER, user.getPhoneNumber());
		json.put(LOCATION, locationToJson(user.getLocation()));
		json.put(BANNED, user.isBanned());

		if (user instanceof Admin) {
			json.put(ROLE, ROLE_ADMIN);
			json.put(ADMIN_ID, ((Admin) user).getAdminId());
		} else if (user instanceof Volunteer) {
			json.put(ROLE, ROLE_VOLUNTEER);
			json.put(SKILLS, skillsToJson(((Volunteer) user).getSkills()));
		} else if (user instanceof ShelterStaff) {
			json.put(ROLE, ROLE_SHELTER_STAFF);
			json.put(SHELTER_ID, ((ShelterStaff) user).getShelterId());
		} else {
			// The victim <-> hurricane link is stored on the hurricane (affectedVictims).
			// activeRequests is saved by subject only so the file is easy to read;
			// loadRequests() rebuilds the real list from requests.json.
			List<Object> subjects = new ArrayList<Object>();
			for (ReliefRequest request : ((Victim) user).getActiveRequests()) {
				subjects.add(request.getSubject());
			}
			json.put(ROLE, ROLE_VICTIM);
			json.put(ACTIVE_REQUESTS, subjects);
		}
		return json;
	}

	// The reverse of parseLocation()
	private Map<String, Object> locationToJson(Location location) {
		if (location == null)
			return null;

		Map<String, Object> json = new LinkedHashMap<String, Object>();
		json.put(ADDRESS, location.getAddress());
		json.put(CITY, location.getCity());
		json.put(STATE, location.getState());
		json.put(ZIP_CODE, location.getZipCode());
		json.put(LATITUDE, location.getLatitude());
		json.put(LONGITUDE, location.getLongitude());
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
		return request.getStatus() == ReliefRequestStatus.OPEN || request.getStatus() == ReliefRequestStatus.ASSIGNED
				|| request.getStatus() == ReliefRequestStatus.IN_PROGRESS;
	}

	// Location has no equals(), so compare the points by latitude/longitude
	private static boolean sameCoordinates(Location a, Location b) {
		return a.getLatitude() == b.getLatitude() && a.getLongitude() == b.getLongitude();
	}

	// Keeps the generated id if the entry has none yet
	private static void restoreId(JSONObject json, Consumer<UUID> setter) {
		if (json.get(ID) != null)
			setter.accept(UUID.fromString((String) json.get(ID)));
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
