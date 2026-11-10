package com.controllers;

import com.hurricane.model.ReliefRequest;
import com.hurricane.model.Server;
import com.hurricane.model.User;
import com.sample.App;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

/**
 * Controls the home screen.
 */
public class HomeController {
    @FXML
    private Label greetingLabel;
    @FXML
    private ListView<ReliefRequest> requestList;

    /** Shows the greeting and the victim's requests */
    @FXML
    private void initialize() {
        User user = Server.getInstance().getCurrentUser();
        if (user != null) {
            greetingLabel.setText("Hello, " + user.getUsername());
            for (ReliefRequest request : Server.getInstance().getAllRequests()) {
                if (request.getRequester() != null
                        && user.getUsername().equals(request.getRequester().getUsername()))
                    requestList.getItems().add(request);
            }
        }
    }

    /** Opens the request that was clicked */
    @FXML
    private void handleOpenRequest(MouseEvent event) {
        ReliefRequest selected = requestList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            App.setSelectedRequest(selected);
            App.show("request_details");
        }
    }

    /** Opens the create request screen */
    @FXML
    private void handleRequestHelp() {
        App.show("create_request");
    }

    /** Logs out and returns to log in */
    @FXML
    private void handleLogout() {
        Server.getInstance().logout();
        App.show("login");
    }
}
