package de.btegermany.teleportation.TeleportationVelocity.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public class BukkitPlayer {

    private final Player proxiedPlayer;
    private RegisteredServer server;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String gameMode;

    // a player with location data and game mode
    public BukkitPlayer(Player proxiedPlayer, RegisteredServer server, double x, double y, double z, float yaw, float pitch, String gameMode) {
        this.proxiedPlayer = proxiedPlayer;
        this.server = server;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.gameMode = gameMode;
    }

    public Player getProxiedPlayer() {
        return proxiedPlayer;
    }

    public void setServer(RegisteredServer server) {
        this.server = server;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public RegisteredServer getServer() {
        return server;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public String getGameMode() {
        return gameMode;
    }
}
