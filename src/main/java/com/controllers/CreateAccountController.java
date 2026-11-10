package com.controllers;

import com.hurricane.model.Location;
import com.hurricane.model.Server;
import com.sample.App;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controls the create account screen.
 */
public class CreateAccountController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField streetField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField stateField;
    @FXML
    private TextField zipField;
    @FXML
    private Label errorLabel;

    /** Creates the victim account and returns to log in */
    @FXML
    private void handleCreateAccount() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            showError("Enter a username and a password.");
            return;
        }
        if (!password.equals(confirmField.getText())) {
            showError("Passwords do not match.");
            return;
        }

        // Map position is left at 0, 0 for now
        Location location = new Location(streetField.getText().trim(), cityField.getText().trim(),
                stateField.getText().trim(), zipField.getText().trim(), 0, 0);
        if (Server.getInstance().createVictimAccount(username, password, phoneField.getText().trim(), location)) {
            App.show("login");
        } else {
            showError("Could not create the account. Try another username.");
        }
    }

    /** Goes back to log in */
    @FXML
    private void handleBack() {
        App.show("login");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
