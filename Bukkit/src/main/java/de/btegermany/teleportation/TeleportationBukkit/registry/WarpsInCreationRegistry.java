package de.btegermany.teleportation.TeleportationBukkit.registry;

import de.btegermany.teleportation.TeleportationBukkit.util.WarpInCreation;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarpsInCreationRegistry implements Registry {

    private final Map<UUID, WarpInCreation> warpsInCreation;

    public WarpsInCreationRegistry() {
        this.warpsInCreation = new HashMap<>();
    }

    public void register(Player player) {
        warpsInCreation.put(player.getUniqueId(), new WarpInCreation(player));
    }

    @Override
    public void unregister(UUID playerUUID) {
        warpsInCreation.remove(playerUUID);
    }

    public WarpInCreation getWarpInCreation(Player player) {
        return this.getWarpInCreation(player.getUniqueId());
    }

    public WarpInCreation getWarpInCreation(UUID playerUUID) {
        return warpsInCreation.get(playerUUID);
    }

    @Override
    public boolean isRegistered(UUID playerUUID) {
        return warpsInCreation.containsKey(playerUUID);
    }

    public Map<UUID, WarpInCreation> getWarpsInCreation() {
        return warpsInCreation;
    }
}
