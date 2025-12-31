package de.btegermany.teleportation.TeleportationBukkit.tp;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class PendingTeleportationAbstract {

    UUID playerUUID;
    private final LocalDateTime expirationTime;
    private final String originServerName;

    public PendingTeleportationAbstract(UUID playerUUID, String originServerName) {
        this.playerUUID = playerUUID;
        this.expirationTime = LocalDateTime.now().plusMinutes(1);
        this.originServerName = originServerName;
    }

    public abstract boolean teleport();
    public abstract boolean canTeleport();

    public boolean isValid() {
        return LocalDateTime.now().isBefore(expirationTime);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getOriginServerName() {
        return originServerName;
    }
}
