package de.btegermany.teleportation.TeleportationBukkit.registry;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface Registry {

    default boolean isRegistered(Player player) {
        return isRegistered(player.getUniqueId());
    }

    boolean isRegistered(UUID playerUUID);

    default void unregister(Player player) {
        this.unregister(player.getUniqueId());
    }

    void unregister(UUID playerUUID);

}
