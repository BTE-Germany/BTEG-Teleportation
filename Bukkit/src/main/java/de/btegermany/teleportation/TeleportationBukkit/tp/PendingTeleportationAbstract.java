package de.btegermany.teleportation.TeleportationBukkit.tp;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class PendingTeleportationAbstract {

    UUID playerUUID;
    private final LocalDateTime expirationTime;

    public PendingTeleportationAbstract(UUID playerUUID) {
        this.playerUUID = playerUUID;
        expirationTime = LocalDateTime.now().plusMinutes(1);
    }

    public abstract void teleport();
    public abstract boolean canTeleport();

    public boolean isValid() {
        return LocalDateTime.now().isBefore(expirationTime);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

}
