package de.btegermany.teleportation.TeleportationBukkit.tp;

import de.btegermany.teleportation.TeleportationBukkit.message.LastLocationMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportationHandler {

    private final Map<UUID, PendingTeleportationAbstract> pendingTps = new HashMap<>();
    private final PluginMessenger pluginMessenger;

    public TeleportationHandler(PluginMessenger pluginMessenger) {
        this.pluginMessenger = pluginMessenger;
    }

    public void handle(PendingTeleportationAbstract teleportation) {
        if(teleportation.canTeleport()) {
            pluginMessenger.send(new LastLocationMessage(teleportation.getPlayerUUID()));
            teleportation.teleport();
        } else {
            pendingTps.put(teleportation.getPlayerUUID(), teleportation);
        }
    }

    public Map<UUID, PendingTeleportationAbstract> getPendingTps() {
        return pendingTps;
    }
}
