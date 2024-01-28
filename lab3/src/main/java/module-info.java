module nsu.networks.lab {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;

    opens nsu.networks.lab to javafx.fxml;
    exports nsu.networks.lab;
}