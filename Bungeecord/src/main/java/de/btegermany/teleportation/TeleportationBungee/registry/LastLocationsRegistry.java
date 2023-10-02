package de.btegermany.teleportation.TeleportationBungee.registry;

import de.btegermany.teleportation.TeleportationBungee.util.LastLocation;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LastLocationsRegistry implements PlayerRegistry {

    Map<UUID, LastLocation> lastLocations;

    public LastLocationsRegistry() {
        this.lastLocations = new HashMap<>();
    }

    public void register(ProxiedPlayer player, LastLocation lastLocation) {
        this.register(player.getUniqueId(), lastLocation);
    }

    public void register(UUID playerUUID, LastLocation lastLocation) {
        lastLocations.put(playerUUID, lastLocation);
    }

    @Override
    public boolean isRegistered(UUID playerUUID) {
        return lastLocations.containsKey(playerUUID);
    }

    @Override
    public void unregister(UUID playerUUID) {
        lastLocations.remove(playerUUID);
    }

    public LastLocation getLastLocation(ProxiedPlayer player) {
        return this.getLastLocation(player.getUniqueId());
    }

    public LastLocation getLastLocation(UUID playerUUID) {
        return lastLocations.get(playerUUID);
    }

}
