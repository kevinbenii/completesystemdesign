module com.hurricane {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires junit;

    opens com.sample to javafx.fxml;
    exports com.sample;

    opens com.controllers to javafx.fxml;
    exports com.controllers;

    opens com.hurricane.model to javafx.fxml;
    exports com.hurricane.model;
}
