package de.btegermany.teleportation.TeleportationBukkit.registry;

import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.MultiplePagesWarpGuiAbstract;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MultiplePagesGuisRegistry implements Registry {

    private final Map<UUID, MultiplePagesWarpGuiAbstract> multiplePagesWarpGuis;

    public MultiplePagesGuisRegistry() {
        this.multiplePagesWarpGuis = new HashMap<>();
    }

    public void register(Player player, MultiplePagesWarpGuiAbstract multiplePagesWarpGuiAbstract) {
        this.register(player.getUniqueId(), multiplePagesWarpGuiAbstract);
    }

    public void register(UUID playerUUID, MultiplePagesWarpGuiAbstract multiplePagesWarpGuiAbstract) {
        multiplePagesWarpGuis.put(playerUUID, multiplePagesWarpGuiAbstract);
    }

    @Override
    public void unregister(UUID playerUUID) {
        multiplePagesWarpGuis.remove(playerUUID);
    }

    public MultiplePagesWarpGuiAbstract getGui(Player player) {
        return this.getGui(player.getUniqueId());
    }

    public MultiplePagesWarpGuiAbstract getGui(UUID playerUUID) {
        return multiplePagesWarpGuis.get(playerUUID);
    }

    @Override
    public boolean isRegistered(UUID playerUUID) {
        return multiplePagesWarpGuis.containsKey(playerUUID);
    }

    public Map<UUID, MultiplePagesWarpGuiAbstract> getMultiplePagesWarpGuis() {
        return multiplePagesWarpGuis;
    }
}
