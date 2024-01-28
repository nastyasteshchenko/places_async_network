package nsu.networks.lab.model.place;

import nsu.networks.lab.model.Weather;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record LocationInfo(CompletableFuture<Weather> weather, CompletableFuture<List<LocationFeature>> features) {
}
