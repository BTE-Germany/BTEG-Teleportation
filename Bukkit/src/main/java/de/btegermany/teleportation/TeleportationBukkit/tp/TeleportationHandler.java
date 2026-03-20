package de.btegermany.teleportation.TeleportationBukkit.tp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportationHandler {

    private final Map<UUID, PendingTeleportationAbstract> pendingTps = new HashMap<>();

    // teleport if possible, otherwise store teleportation until it's possible
    public void handle(PendingTeleportationAbstract teleportation) {
        if (teleportation.canTeleport()) {
            teleportation.teleport();
        } else {
            pendingTps.put(teleportation.getPlayerUUID(), teleportation);
        }
    }

    public Map<UUID, PendingTeleportationAbstract> getPendingTps() {
        return pendingTps;
    }
}
