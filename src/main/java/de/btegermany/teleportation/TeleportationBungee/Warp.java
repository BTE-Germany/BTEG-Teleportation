package de.btegermany.teleportation.TeleportationBungee;

public class Warp {

    private final int id;
    private final String name;
    private final String city;
    private final String state;
    private final double latitude;
    private final double longitude;
    private final String headId;
    private final float yaw;
    private final float pitch;
    private final double height;

    public Warp(int id, String name, String city, String state, double latitude, double longitude, String headId, float yaw, float pitch, double height) {
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
    }

    public String getTpllCommand() {
        return "tpll " + latitude + " " + longitude + " " + height + " yaw=" + yaw + " pitch=" + pitch;
    }

    // Getters

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
}
