package com.controllers;

import com.hurricane.model.Server;
import com.sample.App;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controls the log in screen.
 */
public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    /** Logs in and opens the home screen */
    @FXML
    private void handleLogin() {
        if (Server.getInstance().login(usernameField.getText().trim(), passwordField.getText())) {
            App.show("home");
        } else {
            showError("Username or password is incorrect.");
        }
    }

    /** Opens the create account screen */
    @FXML
    private void handleCreateAccount() {
        App.show("create_account");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
