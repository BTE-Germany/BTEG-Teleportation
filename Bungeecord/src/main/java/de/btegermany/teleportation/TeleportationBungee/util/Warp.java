package de.btegermany.teleportation.TeleportationBungee.util;

import java.util.ArrayList;
import java.util.List;

public class Warp implements Comparable<Warp> {

    private final int id;
    private String name;
    private String city;
    private String state;
    private double latitude;
    private double longitude;
    private String headId;
    private float yaw;
    private float pitch;
    private double height;
    private final List<String> tags;

    public Warp(int id, String name, String city, String state, double latitude, double longitude, String headId, float yaw, float pitch, double height, List<String> tags) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.headId = headId;
        this.yaw = yaw;
        this.pitch = pitch;
        this.height = height;
        this.tags = tags;
    }

    public Warp(int id, String name, String city, String state, double latitude, double longitude, String headId, float yaw, float pitch, double height) {
        this(id, name, city, state, latitude, longitude, headId, yaw, pitch, height, new ArrayList<>());
    }

    public String getTpllCommand() {
        return "tpll " + latitude + " " + longitude + " " + height + " yaw=" + yaw + " pitch=" + pitch;
    }

    // Setters, Getters


    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setHeadId(String headId) {
        this.headId = headId;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getHeadId() {
        return headId;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public double getHeight() {
        return height;
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public int compareTo(Warp warp) {
        int compareCity = this.city.compareToIgnoreCase(warp.getCity());
        if (compareCity != 0) {
            return compareCity;
        }
        int compareState = this.state.compareToIgnoreCase(warp.getState());
        if (compareState != 0) {
            return compareState;
        }
        return this.name.compareToIgnoreCase(warp.getName());
    }
}
