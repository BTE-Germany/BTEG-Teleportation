package de.btegermany.teleportation.TeleportationVelocity.util;

import com.velocitypowered.api.proxy.server.RegisteredServer;

public class LastLocation {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final RegisteredServer server;

    // a location a player teleported from to another location
    public LastLocation(double x, double y, double z, float yaw, float pitch, RegisteredServer server) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.server = server;
    }

    public String getCoordinates() {
        return x + ", " + y + ", " + z;
    }

    // Getters

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public RegisteredServer getServer() {
        return server;
    }

}
