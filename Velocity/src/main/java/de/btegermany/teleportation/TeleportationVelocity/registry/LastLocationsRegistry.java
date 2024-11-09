package de.btegermany.teleportation.TeleportationVelocity.registry;

import com.velocitypowered.api.proxy.Player;
import de.btegermany.teleportation.TeleportationVelocity.util.LastLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LastLocationsRegistry implements PlayerRegistry {

    Map<UUID, LastLocation> lastLocations;

    public LastLocationsRegistry() {
        this.lastLocations = new HashMap<>();
    }

    public void register(Player player, LastLocation lastLocation) {
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

    public LastLocation getLastLocation(Player player) {
        return this.getLastLocation(player.getUniqueId());
    }

    public LastLocation getLastLocation(UUID playerUUID) {
        return lastLocations.get(playerUUID);
    }

}
