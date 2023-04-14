package de.btegermany.teleportation.TeleportationBungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BukkitPlayer {

    private final ProxiedPlayer proxiedPlayer;
    private ServerInfo serverInfo;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String gameMode;

    public BukkitPlayer(ProxiedPlayer proxiedPlayer, ServerInfo serverInfo, double x, double y, double z, float yaw, float pitch, String gameMode) {
        this.proxiedPlayer = proxiedPlayer;
        this.serverInfo = serverInfo;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.gameMode = gameMode;
    }

    public ProxiedPlayer getProxiedPlayer() {
        return proxiedPlayer;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
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

    public ServerInfo getServerInfo() {
        return serverInfo;
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
