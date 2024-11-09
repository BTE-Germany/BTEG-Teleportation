package de.btegermany.teleportation.TeleportationVelocity.geo;

public class GeoLocation {

    double latitude;
    double longitude;
    String country;
    String state;
    String city;

    // represents a real life location and holds its data
    public GeoLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }



    // Setters, Getters
    public void setCountry(String country) {
        this.country = country;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }
}
