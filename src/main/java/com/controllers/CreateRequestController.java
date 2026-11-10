package com.controllers;

import java.util.ArrayList;

import com.hurricane.model.ReliefRequest;
import com.hurricane.model.Server;
import com.hurricane.model.Skill;
import com.sample.App;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;

/**
 * Controls the create relief request screen.
 */
public class CreateRequestController {
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea detailsArea;
    @FXML
    private FlowPane skillBox;
    @FXML
    private TextField medicalField;
    @FXML
    private Label errorLabel;

    /** Adds a toggle button for each type of help */
    @FXML
    private void initialize() {
        for (Skill skill : Skill.values()) {
            ToggleButton button = new ToggleButton(skill.name());
            button.getStyleClass().add("chip");
            button.setUserData(skill);
            skillBox.getChildren().add(button);
        }
    }

    /** Sends the request and opens its details */
    @FXML
    private void handleSubmit() {
        ArrayList<Skill> skills = new ArrayList<Skill>();
        for (Node node : skillBox.getChildren()) {
            ToggleButton button = (ToggleButton) node;
            if (button.isSelected())
                skills.add((Skill) button.getUserData());
        }

        ReliefRequest request = Server.getInstance().submitFamilyRequest(subjectField.getText().trim(),
                detailsArea.getText().trim(), skills, medicalField.getText().trim());
        if (request == null) {
            errorLabel.setText("Could not send the request.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return;
        }
        App.setSelectedRequest(request);
        App.show("request_details");
    }

    /** Goes back to the home screen */
    @FXML
    private void handleBack() {
        App.show("home");
    }
}
