module com.hurricane {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;

    opens com.hurricane to javafx.fxml;
    exports com.hurricane;

    opens com.hurricane.model to javafx.fxml;
    exports com.hurricane.model;
}
