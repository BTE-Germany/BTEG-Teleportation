package de.btegermany.teleportation.TeleportationBungee.util;

import net.md_5.bungee.api.config.ServerInfo;

public class LastLocation {

    private final double x;
    private final double y;
    private final double z;
    private final Float yaw;
    private final Float pitch;
    private final ServerInfo serverInfo;

    // a location a player teleported from to another location
    public LastLocation(double x, double y, double z, Float yaw, Float pitch, ServerInfo serverInfo) {
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

    public Float getYaw() {
        return yaw;
    }

    public Float getPitch() {
        return pitch;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

}
