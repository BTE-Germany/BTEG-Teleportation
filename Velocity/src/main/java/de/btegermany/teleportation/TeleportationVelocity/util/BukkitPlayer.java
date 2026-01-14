package de.btegermany.teleportation.TeleportationVelocity.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Getter;
import lombok.Setter;

// a player with location data and game mode
@Getter
public class BukkitPlayer {
    private final Player proxiedPlayer;
    @Setter
    private RegisteredServer server;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final String gameMode;

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

}