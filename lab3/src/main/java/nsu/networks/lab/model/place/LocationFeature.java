package nsu.networks.lab.model.place;

import java.util.concurrent.CompletableFuture;

public record LocationFeature(String xid, String name, CompletableFuture<String> info) {
    @Override
    public String toString() {
        return String.format("%s\n", name);
    }
}
