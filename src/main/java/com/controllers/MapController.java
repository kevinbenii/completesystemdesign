package com.controllers;

import com.hurricane.model.ReliefRequest;
import com.hurricane.model.Server;
import com.hurricane.model.Shelter;
import com.location.Coordinates;
import com.location.DrivingRoute;
import com.location.MapTools;
import com.sample.App;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import javafx.util.Duration;

// Shows the driving route from the selected request to its shelter
public class MapController {
    @FXML
    private WebView webView;
    @FXML
    private Label shelterLabel;

    private DrivingRoute route;

    @FXML
    private void initialize() {
        // Once the map page has loaded, wait a moment for it to size itself, then draw the route
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                PauseTransition pause = new PauseTransition(Duration.millis(250));
                pause.setOnFinished(event -> drawRoute());
                pause.play();
            }
        });

        // The map service is slow, so look up the route in the background
        ReliefRequest request = App.getSelectedRequest();
        Thread thread = new Thread(() -> loadRoute(request));
        thread.setDaemon(true);
        thread.start();
    }

    private void loadRoute(ReliefRequest request) {
        try {
            Shelter shelter = Server.getInstance().findSuitableShelter(request);
            if (shelter == null) {
                Platform.runLater(() -> shelterLabel.setText("No suitable shelter found."));
                return;
            }
            route = MapTools.getInstance().getDrivingRoute(request.getLocation().toAddress(),
                    shelter.getLocation().toAddress());
            Platform.runLater(() -> {
                shelterLabel.setText("Shelter: " + shelter.getName());
                webView.getEngine().load(App.class.getResource("/com/sample/map.html").toExternalForm());
            });
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> shelterLabel.setText("Could not load the map. Check MAP_API_KEY and the internet."));
        }
    }

    // Calls the map page's JavaScript to add the two pins and the route line
    private void drawRoute() {
        String start = route.getStartingLocation().getCoordinates().getLatitude() + ", longitude: "
                + route.getStartingLocation().getCoordinates().getLongitude();
        String end = route.getEndingLocation().getCoordinates().getLatitude() + ", longitude: "
                + route.getEndingLocation().getCoordinates().getLongitude();
        webView.getEngine().executeScript("window.showLocations(["
                + "{latitude: " + start + ", address: 'Request'},"
                + "{latitude: " + end + ", address: 'Shelter'}]);");

        StringBuilder points = new StringBuilder();
        for (Coordinates point : route.getRoutePoints()) {
            if (points.length() > 0)
                points.append(",");
            points.append("[").append(point.getLatitude()).append(",").append(point.getLongitude()).append("]");
        }
        webView.getEngine().executeScript("window.showRoute([" + points + "]);");
    }

    // Goes back to the request the map was opened from
    @FXML
    private void handleBack() {
        App.show("request_details");
    }
}
