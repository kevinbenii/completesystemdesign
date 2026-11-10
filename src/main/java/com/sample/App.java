package com.sample;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.hurricane.model.ReliefRequest;
import com.hurricane.model.ReliefRequestStatus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Stage stage;
    private static ReliefRequest selectedRequest;

    /** Opens the window on the log in screen */
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Hurricane Relief");
        show("login");
        stage.show();
    }

    /** Shows one screen by the name of its fxml file, for example "home" */
    public static void show(String screen) {
        try {
            Parent root = FXMLLoader.load(App.class.getResource("/com/sample/" + screen + ".fxml"));
            Scene scene = new Scene(root, 390, 760);
            scene.getStylesheets().add(App.class.getResource("/com/sample/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not open the " + screen + " screen", e);
        }
    }

    /** The request the details screen shows */
    public static ReliefRequest getSelectedRequest() {
        return selectedRequest;
    }

    /** Sets the request the details screen shows */
    public static void setSelectedRequest(ReliefRequest request) {
        selectedRequest = request;
    }

    /** Plain words for a request status */
    public static String statusText(ReliefRequestStatus status) {
        if (status == null)
            return "Unknown";
        switch (status) {
        case OPEN:
            return "Submitted";
        case ASSIGNED:
            return "Volunteer assigned";
        case IN_PROGRESS:
            return "In progress";
        case RESOLVED:
        case COMPLETE:
            return "Complete";
        case MODERATOR_REMOVED:
            return "Removed by a moderator";
        default:
            return "Unknown";
        }
    }

    /** Starts the JavaFX app */
    public static void main(String[] args) {
        launch();
    }

}
