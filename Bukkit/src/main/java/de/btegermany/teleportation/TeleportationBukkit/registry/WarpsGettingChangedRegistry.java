package de.btegermany.teleportation.TeleportationBukkit.registry;

import de.btegermany.teleportation.TeleportationBukkit.util.WarpGettingChanged;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarpsGettingChangedRegistry implements Registry {

    private final Map<UUID, WarpGettingChanged> warpsGettingChanged;

    public WarpsGettingChangedRegistry() {
        this.warpsGettingChanged = new HashMap<>();
    }

    public void register(Player player, WarpGettingChanged warpGettingChanged) {
        this.register(player.getUniqueId(), warpGettingChanged);
    }

    public void register(UUID playerUUID, WarpGettingChanged warpGettingChanged) {
        warpsGettingChanged.put(playerUUID, warpGettingChanged);
    }

    @Override
    public boolean isRegistered(UUID playerUUID) {
        return warpsGettingChanged.containsKey(playerUUID);
    }

    @Override
    public void unregister(UUID playerUUID) {
        warpsGettingChanged.remove(playerUUID);
    }

    public WarpGettingChanged getWarpGettingChanged(Player player) {
        return this.getWarpGettingChanged(player.getUniqueId());
    }

    public WarpGettingChanged getWarpGettingChanged(UUID playerUUID) {
        return warpsGettingChanged.get(playerUUID);
    }

    public Map<UUID, WarpGettingChanged> getWarpsGettingChanged() {
        return warpsGettingChanged;
    }

}
