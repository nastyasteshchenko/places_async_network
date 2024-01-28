package nsu.networks.lab.model;

import nsu.networks.lab.model.place.LocationFeature;
import nsu.networks.lab.model.place.Location;
import nsu.networks.lab.model.place.LocationInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RequestSender {
    private static final String GRAPH_HOPPER_KEY = "f24e3326-37e5-4eb6-ae31-ca1f22befdc0";
    private static final String OPEN_WEATHER_KEY = "670cb9e37e3d64080b1887dd5412b88c";
    private static final String OPEN_TRIP_KEY = "5ae2e3f221c38a28845f05b606fa7cd6d698bdb5ad17d40de2b8007b";
    private static final String GRAPH_HOPPER_URL = "https://graphhopper.com/api/1/geocode";
    private static final String OPEN_WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String OPEN_TRIP_INFO_URL = "https://api.opentripmap.com/0.1/ru/places/xid";
    private static final String OPEN_TRIP_PLACES_URL = "https://api.opentripmap.com/0.1/ru/places/radius";
    private static final String RADIUS = "1000";
    private static final String LANG = "en";
    private static final String LIMIT = "10";
    private static final RequestSender INSTANCE = new RequestSender();
    private final HttpClient client = HttpClient.newHttpClient();
    private boolean isErrorForReceivingLocations = false;
    private boolean isErrorForReceivingInfo = false;
    private boolean isErrorForReceivingWeather = false;
    private boolean isErrorForReceivingFeatures = false;

    private RequestSender() {
    }

    public static RequestSender getInstance() {
        return INSTANCE;
    }

    public boolean isErrorForReceivingLocations() {
        return isErrorForReceivingLocations;
    }

    public boolean isErrorForReceivingInfo() {
        return isErrorForReceivingInfo;
    }

    public boolean isErrorForReceivingWeather() {
        return isErrorForReceivingWeather;
    }

    public boolean isErrorForReceivingFeatures() {
        return isErrorForReceivingFeatures;
    }

    public CompletableFuture<List<Location>> sendSearchLocationRequest(String locationName) throws URISyntaxException {
        String locationNameUTF8 = URLEncoder.encode(locationName, StandardCharsets.UTF_8);

        String url = String.format("%s?q=%s&key=%s&limit=%s",
                GRAPH_HOPPER_URL, locationNameUTF8, GRAPH_HOPPER_KEY, LIMIT);

        return sendAsync(url, "l").thenApply(ResponseParser.getInstance()::parseToSearchLocationsList);
    }

    public LocationInfo sendChosenLocationInfoRequest(Location location) throws URISyntaxException {
        return new LocationInfo(sendWeatherRequest(location), sendFeaturesRequest(location));
    }

    CompletableFuture<String> sendFeatureInfoRequest(String xid) throws URISyntaxException {

        String url = String.format("%s/%s?lang=%s&apikey=%s",
                OPEN_TRIP_INFO_URL, xid, LANG, OPEN_TRIP_KEY);

        return sendAsync(url, "i").thenApply(ResponseParser.getInstance()::parseToFeatureInfo);
    }

    private CompletableFuture<Weather> sendWeatherRequest(Location location) throws URISyntaxException {

        String url = String.format("%s?lat=%s&lon=%s&appid=%s&units=metric",
                OPEN_WEATHER_URL, location.coords().lat(), location.coords().lon(), OPEN_WEATHER_KEY);

        return sendAsync(url, "w").thenApply(ResponseParser.getInstance()::parseToWeather);
    }

    private CompletableFuture<List<LocationFeature>> sendFeaturesRequest(Location location) throws URISyntaxException {

        String url = String.format("%s?lang=%s&radius=%s&lat=%s&lon=%s&limit=%s&apikey=%s",
                OPEN_TRIP_PLACES_URL, LANG, RADIUS, location.coords().lat(), location.coords().lon(), LIMIT, OPEN_TRIP_KEY);

        return sendAsync(url, "f").thenApply(ResponseParser.getInstance()::parseToFeaturesList);
    }

    private CompletableFuture<String> sendAsync(String url, String reqKind) throws URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(r -> checkResponse(r, reqKind));
    }

    private String checkResponse(HttpResponse<String> response, String kind){
        if (response.statusCode()!=200){
           switch (kind){
               case "l" -> isErrorForReceivingLocations = true;
               case "i" -> isErrorForReceivingInfo = true;
               case "w" -> isErrorForReceivingWeather = true;
               case "f" -> isErrorForReceivingFeatures = true;
           }
        }
        return response.body();
    }
}
