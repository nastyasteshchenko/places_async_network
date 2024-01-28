package nsu.networks.lab.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.scene.control.Alert;
import nsu.networks.lab.model.place.*;

import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

class ResponseParser {

    private static final ResponseParser INSTANCE = new ResponseParser();

    private ResponseParser() {
    }

    public static ResponseParser getInstance() {
        return INSTANCE;
    }

    List<Location> parseToSearchLocationsList(String json) {
        JsonElement jsonElement = JsonParser.parseString(json);
        JsonArray hits = jsonElement.getAsJsonObject().get("hits").getAsJsonArray();

        List<Location> searchPlaces = new ArrayList<>();

        hits.forEach(je -> {
            JsonElement stateJE = je.getAsJsonObject().get("state");
            JsonElement countryJE = je.getAsJsonObject().get("country");
            JsonElement cityJE = je.getAsJsonObject().get("city");
            JsonElement streetJE = je.getAsJsonObject().get("street");
            JsonElement houseNumberJE = je.getAsJsonObject().get("housenumber");

            String state = stateJE == null ? null : stateJE.getAsString();
            String city = cityJE == null ? null : cityJE.getAsString();
            String street = streetJE == null ? null : streetJE.getAsString();
            String country = stateJE == null ? null : countryJE.getAsString();
            String houseNumber = houseNumberJE == null ? null : houseNumberJE.getAsString();

            String value = je.getAsJsonObject().get("osm_value").getAsString();
            String name = je.getAsJsonObject().get("name").getAsString();
            String key = je.getAsJsonObject().get("osm_key").getAsString();

            JsonElement point = je.getAsJsonObject().get("point");
            double lat = point.getAsJsonObject().get("lat").getAsDouble();
            double lng = point.getAsJsonObject().get("lng").getAsDouble();

            LocationDescription locationDescription = new LocationDescription(key, value);
            LocationAddress address = new LocationAddress(houseNumber, street, city, state, country);
            Point coords = new Point(lat, lng);
            searchPlaces.add(new Location(locationDescription, name, address, coords));
        });

        return searchPlaces;
    }

    List<LocationFeature> parseToFeaturesList(String json) {
        JsonElement jsonElement = JsonParser.parseString(json);
        JsonArray features = jsonElement.getAsJsonObject().get("features").getAsJsonArray();

        List<LocationFeature> interestingPlaces = new ArrayList<>();

        features.forEach(je -> {
            JsonElement properties = je.getAsJsonObject().get("properties");

            String xid = properties.getAsJsonObject().get("xid").getAsString();
            String name = properties.getAsJsonObject().get("name").getAsString();

            if (!name.isEmpty()) {
                try {
                    interestingPlaces.add(new LocationFeature(xid, name, RequestSender.getInstance().sendFeatureInfoRequest(xid)));
                } catch (URISyntaxException e) {
                    wrongUriAlert(name);
                }
            }
        });

        return interestingPlaces;
    }

    Weather parseToWeather(String json) {

        JsonElement jsonElement = JsonParser.parseString(json);
        JsonArray weather = jsonElement.getAsJsonObject().get("weather").getAsJsonArray();
        JsonElement weatherJE = weather.get(0);

        String weatherName = weatherJE.getAsJsonObject().get("main").getAsString();
        String description = weatherJE.getAsJsonObject().get("description").getAsString();

        JsonElement main = jsonElement.getAsJsonObject().get("main");
        String temp = main.getAsJsonObject().get("temp").getAsString();
        String feelsLike = main.getAsJsonObject().get("feels_like").getAsString();

        JsonElement wind = jsonElement.getAsJsonObject().get("wind");
        String speed = wind.getAsJsonObject().get("speed").getAsString();

        return new Weather(weatherName, description, temp, feelsLike, speed);
    }

    String parseToFeatureInfo(String json) {
        JsonElement jsonElement = JsonParser.parseString(json);
        JsonElement wiki = jsonElement.getAsJsonObject().get("wikipedia_extracts");
        if (wiki == null) {
            return "Sorry, no info is here :(";
        }

        String info = wiki.getAsJsonObject().get("text").getAsString();
        return removeAccents(info);
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    private String removeAccents(String info) {
        return Normalizer.normalize(info, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
    }

    private void wrongUriAlert(String name) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Warning!");
        alert.setContentText("Something wrong with getting info about: " + name);
        alert.showAndWait();
    }
}
