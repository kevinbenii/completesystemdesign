package com.hurricane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    /** Opens a placeholder window until the app has real screens */
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new StackPane(new Label("Hurricane Relief System")), 640, 480);
        stage.setTitle("Hurricane Relief System");
        stage.setScene(scene);
        stage.show();
    }

    /** Starts the JavaFX app */
    public static void main(String[] args) {
        launch();
    }

}
