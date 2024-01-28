package nsu.networks.lab.model.place;

public record LocationDescription(String key, String value) {
    @Override
    public String toString() {
        return String.format("description: %s %s\n", key, value);
    }
}
