package de.btegermany.teleportation.TeleportationBungee.util;

import net.md_5.bungee.api.config.ServerInfo;

// a location a player teleported from to another location
public record LastLocation(double x, double y, double z, Float yaw, Float pitch, String world, ServerInfo serverInfo) {

    public String getCoordinates() {
        return x + ", " + y + ", " + z;
    }

}