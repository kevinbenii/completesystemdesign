package com.hurricane.model;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

// Tests for finding hurricanes by location and saving them.
// Author: Max Vogel
public class HurricaneTrackerTest {
    private static final double DELTA = 0.000001;

    private Path testFolder;
    private HurricaneTracker tracker;
    private Location columbia;
    private Location farNorth;

    private Location testLocation(double latitude, double longitude) {
        return new Location("123 Test Street", "Columbia", "SC", "29201", latitude, longitude);
    }

    // Saves with the tracker, then loads with a brand new data manager on the same folder
    private ArrayList<Hurricane> saveThenReload() {
        tracker.saveHurricanes();
        return new FileDataManager(testFolder.toString()).loadHurricanes();
    }

    // Empty folder for every test, so the real json files are never read or changed
    @Before
    public void setup() throws IOException {
        testFolder = Files.createTempDirectory("hurricanetrackertest");
        tracker = new HurricaneTracker(new FileDataManager(testFolder.toString()));
        columbia = testLocation(34.0, -81.0);
        // About 414 miles north of columbia
        farNorth = testLocation(40.0, -81.0);
    }

    // The tests save files into the folder, so remove them before removing the folder
    @After
    public void tearDown() throws IOException {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(testFolder)) {
            for (Path file : files)
                Files.deleteIfExists(file);
        }
        Files.deleteIfExists(testFolder);
    }

    @Test
    public void testFilterByLocationFindsHurricaneInsideRadius() {
        tracker.addHurricane(new Hurricane(columbia, 2, 100.0));
        assertEquals(1, tracker.filterByLocation(columbia, 100.0).size());
    }

    @Test
    public void testFilterByLocationLeavesOutHurricaneOutsideRadius() {
        tracker.addHurricane(new Hurricane(farNorth, 2, 100.0));
        assertEquals(0, tracker.filterByLocation(columbia, 100.0).size());
    }

    @Test
    public void testFilterByLocationWithNoHurricanesIsEmpty() {
        assertEquals(0, tracker.filterByLocation(columbia, 100.0).size());
    }

    @Test
    public void testSavedHurricaneKeepsCategoryAfterReload() {
        tracker.addHurricane(new Hurricane(columbia, 4, 140.0));
        assertEquals(4, saveThenReload().get(0).getCategory());
    }

    @Test
    public void testSavedHurricaneKeepsWindSpeedAfterReload() {
        tracker.addHurricane(new Hurricane(columbia, 4, 140.0));
        assertEquals(140.0, saveThenReload().get(0).getWindSpeed(), DELTA);
    }

    @Test
    public void testSavedHurricaneKeepsLocationHistoryAfterReload() {
        Hurricane hurricane = new Hurricane(columbia, 2, 100.0);
        hurricane.updateData(farNorth, 3, 120.0);
        tracker.addHurricane(hurricane);
        assertEquals(2, saveThenReload().get(0).getLocationHistory().size());
    }
}
