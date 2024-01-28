package nsu.networks.lab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import nsu.networks.lab.model.place.LocationFeature;
import nsu.networks.lab.model.place.Location;
import nsu.networks.lab.model.RequestSender;
import nsu.networks.lab.model.place.LocationInfo;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Controller {

    @FXML
    private Label infoText;

    @FXML
    private TextField searchText;

    @FXML
    private ListView<Location> searchLocationList;

    @FXML
    private ListView<LocationFeature> featuresList;

    @FXML
    private Label weatherLabel;

    @FXML
    private void search() {
        try {
            CompletableFuture<List<Location>> searchPlaces = RequestSender.getInstance().sendSearchLocationRequest(searchText.getText());
            if (RequestSender.getInstance().isErrorForReceivingLocations()) {
                inform("Error while loading");
            } else {
                List<Location> places = searchPlaces.get();
                if (places.isEmpty()) {
                    inform("Nothing was found");
                } else {
                    ObservableList<Location> observableList = FXCollections.observableArrayList(places);
                    searchLocationList.setItems(observableList);
                }

            }
        } catch (URISyntaxException | ExecutionException | InterruptedException e) {
            alert(e);
        }
    }

    @FXML
    private void chooseFeature() {
        try {
            LocationFeature chosenPlace = featuresList.getSelectionModel().getSelectedItem();
            if (chosenPlace != null) {
                if (RequestSender.getInstance().isErrorForReceivingInfo()) {
                    infoText.setText("Error while loading");
                } else {
                    infoText.setText(chosenPlace.info().get());
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            alert(e);
        }
    }

    @FXML
    private void choosePlace() {
        try {
            Location chosenLocation = searchLocationList.getSelectionModel().getSelectedItem();
            if (chosenLocation != null) {
                LocationInfo locationInfo = RequestSender.getInstance().sendChosenLocationInfoRequest(chosenLocation);

                if (RequestSender.getInstance().isErrorForReceivingWeather()) {
                    weatherLabel.setText("Error while loading");
                } else {
                    weatherLabel.setText(locationInfo.weather().get().toString());
                }

                if (RequestSender.getInstance().isErrorForReceivingFeatures()) {
                    inform("Error while loading");
                } else {
                    ObservableList<LocationFeature> observableList = FXCollections.observableArrayList(locationInfo.features().get());
                    featuresList.setItems(observableList);
                }

            }
        } catch (URISyntaxException | InterruptedException | ExecutionException e) {
            alert(e);
        }
    }

    private void inform(String information) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Information");
        alert.setContentText(information);
        alert.showAndWait();
    }

    private void alert(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText("Ooops, something went wrong(\nError: " + e.getMessage());
        alert.showAndWait();
    }
}
