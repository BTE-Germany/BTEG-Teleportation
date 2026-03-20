package de.btegermany.teleportation.TeleportationBukkit.registry;

import de.btegermany.teleportation.TeleportationBukkit.gui.base.PagedCustomGui;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PagedGuisRegistry implements Registry {

    private final Map<UUID, PagedCustomGui> pagedGuis;

    public PagedGuisRegistry() {
        this.pagedGuis = new HashMap<>();
    }

    public void register(Player player, PagedCustomGui gui) {
        this.register(player.getUniqueId(), gui);
    }

    public void register(UUID playerUUID, PagedCustomGui gui) {
        pagedGuis.put(playerUUID, gui);
    }

    @Override
    public void unregister(UUID playerUUID) {
        pagedGuis.remove(playerUUID);
    }

    public PagedCustomGui getGui(Player player) {
        return this.getGui(player.getUniqueId());
    }

    public PagedCustomGui getGui(UUID playerUUID) {
        return pagedGuis.get(playerUUID);
    }

    @Override
    public boolean isRegistered(UUID playerUUID) {
        return pagedGuis.containsKey(playerUUID);
    }

    public Map<UUID, PagedCustomGui> getPagedGuis() {
        return pagedGuis;
    }
}
