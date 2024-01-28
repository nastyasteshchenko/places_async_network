package nsu.networks.lab.model;

public record Weather(String weather, String description, String temp, String feelsLike, String windSpeed) {
    @Override
    public String toString() {
        return String.format("weather: %s\ndescription: %s\ntemp: %s С\nfeels like: %s С\nwind speed: %s m/s\n",
                weather, description, temp, feelsLike, windSpeed);
    }
}


