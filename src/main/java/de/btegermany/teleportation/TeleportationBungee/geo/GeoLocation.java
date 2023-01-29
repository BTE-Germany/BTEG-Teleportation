package de.btegermany.teleportation.TeleportationBungee.geo;

public class GeoLocation {

    double lat;
    double lon;
    String country;
    String state;
    String city;

    public GeoLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
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

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
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
