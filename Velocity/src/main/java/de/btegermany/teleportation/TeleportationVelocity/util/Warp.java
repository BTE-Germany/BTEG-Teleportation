package de.btegermany.teleportation.TeleportationVelocity.util;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Warp implements Comparable<Warp> {

    private final int id;
    @Setter
    private String name;
    @Setter
    private String city;
    @Setter
    private String state;
    @Setter
    private double latitude;
    @Setter
    private double longitude;
    @Setter
    private String headId;
    @Setter
    private float yaw;
    @Setter
    private float pitch;
    @Setter
    private double height;
    @Setter
    private String world;
    private final List<String> tags;

    public Warp(int id, String name, String city, String state, double latitude, double longitude, String headId, float yaw, float pitch, double height, String world, List<String> tags) {
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
        this.world = world;
        this.tags = tags;
    }

    public Warp(int id, String name, String city, String state, double latitude, double longitude, String headId, float yaw, float pitch, double height, String world) {
        this(id, name, city, state, latitude, longitude, headId, yaw, pitch, height, world, new ArrayList<>());
    }

    public String getTpllCommand() {
        return "tpll " + latitude + " " + longitude + " " + height + " yaw=" + yaw + " pitch=" + pitch + " world=" + world;
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
