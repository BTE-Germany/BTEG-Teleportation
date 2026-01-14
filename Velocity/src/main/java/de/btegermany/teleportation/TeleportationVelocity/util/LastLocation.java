package de.btegermany.teleportation.TeleportationVelocity.util;

import com.velocitypowered.api.proxy.server.RegisteredServer;

// a location a player teleported from to another location
public record LastLocation(double x, double y, double z, Float yaw, Float pitch, String world, RegisteredServer server) {

    public String getCoordinates() {
        return x + ", " + y + ", " + z;
    }

}