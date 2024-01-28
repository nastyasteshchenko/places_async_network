package nsu.networks.lab.model.place;

public record LocationAddress(String houseNumber, String street, String city, String state, String country) {
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (houseNumber != null) {
            stringBuilder.append("house number: ").append(houseNumber).append("\n");
        }
        if (street != null) {
            stringBuilder.append("street: ").append(street).append("\n");
        }
        if (city != null) {
            stringBuilder.append("city: ").append(city).append("\n");
        }
        if (state != null) {
            stringBuilder.append("state: ").append(state).append("\n");
        }
        if (country != null) {
            stringBuilder.append("country: ").append(country).append("\n");
        }
        return stringBuilder.toString();
    }
}
