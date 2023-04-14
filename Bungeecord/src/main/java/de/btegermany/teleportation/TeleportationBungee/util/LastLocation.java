package de.btegermany.teleportation.TeleportationBungee.util;

import net.md_5.bungee.api.config.ServerInfo;

public class LastLocation {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final ServerInfo serverInfo;

    public LastLocation(double x, double y, double z, float yaw, float pitch, ServerInfo serverInfo) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.serverInfo = serverInfo;
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

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

}
