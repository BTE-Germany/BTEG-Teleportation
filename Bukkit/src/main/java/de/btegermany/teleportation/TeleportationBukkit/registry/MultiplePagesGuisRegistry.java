package de.btegermany.teleportation.TeleportationBukkit.registry;

import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.MultiPageWarpGuiAbstract;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MultiplePagesGuisRegistry implements Registry {

    private final Map<UUID, MultiPageWarpGuiAbstract> multiplePagesWarpGuis;

    public MultiplePagesGuisRegistry() {
        this.multiplePagesWarpGuis = new HashMap<>();
    }

    public void register(Player player, MultiPageWarpGuiAbstract multiPageWarpGuiAbstract) {
        this.register(player.getUniqueId(), multiPageWarpGuiAbstract);
    }

    public void register(UUID playerUUID, MultiPageWarpGuiAbstract multiPageWarpGuiAbstract) {
        multiplePagesWarpGuis.put(playerUUID, multiPageWarpGuiAbstract);
    }

    @Override
    public void unregister(UUID playerUUID) {
        multiplePagesWarpGuis.remove(playerUUID);
    }

    public MultiPageWarpGuiAbstract getGui(Player player) {
        return this.getGui(player.getUniqueId());
    }

    public MultiPageWarpGuiAbstract getGui(UUID playerUUID) {
        return multiplePagesWarpGuis.get(playerUUID);
    }

    @Override
    public boolean isRegistered(UUID playerUUID) {
        return multiplePagesWarpGuis.containsKey(playerUUID);
    }

    public Map<UUID, MultiPageWarpGuiAbstract> getMultiplePagesWarpGuis() {
        return multiplePagesWarpGuis;
    }
}
