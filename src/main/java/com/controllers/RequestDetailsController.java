package com.controllers;

import com.hurricane.model.ReliefRequest;
import com.hurricane.model.Server;
import com.sample.App;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controls the screen that shows one relief request.
 */
public class RequestDetailsController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label detailsLabel;
    @FXML
    private Label helpLabel;
    @FXML
    private Label locationLabel;

    private ReliefRequest request;

    /** Shows the request that was picked on the previous screen */
    @FXML
    private void initialize() {
        request = App.getSelectedRequest();
        if (request != null) {
            titleLabel.setText(request.getSubject());
            statusLabel.setText(App.statusText(request.getStatus()));
            detailsLabel.setText(request.getBody());
            helpLabel.setText("Type of help: " + request.getRequiredSkills());
            locationLabel.setText("Location: "
                    + (request.getLocation() == null ? "Not given" : request.getLocation().getFullAddress()));
        }
    }

    /** Removes the request and returns to the home screen */
    @FXML
    private void handleCancel() {
        if (request != null)
            Server.getInstance().deleteRequest(request);
        App.setSelectedRequest(null);
        App.show("home");
    }

    /** Goes back to the home screen */
    @FXML
    private void handleBack() {
        App.show("home");
    }
}
