package nsu.networks.lab.model.place;

public record Location(LocationDescription locationDescription, String name, LocationAddress address, Point coords) {
    @Override
    public String toString() {
        return locationDescription.toString() +
                String.format("name: %s\n", name) +
                address.toString();
    }
}
