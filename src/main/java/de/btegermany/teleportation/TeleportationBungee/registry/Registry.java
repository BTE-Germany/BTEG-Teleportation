package de.btegermany.teleportation.TeleportationBungee.registry;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public interface Registry {

    default boolean isRegistered(ProxiedPlayer player) {
        return isRegistered(player.getUniqueId());
    }

    boolean isRegistered(UUID playerUUID);

    default void unregister(ProxiedPlayer player) {
        this.unregister(player.getUniqueId());
    }

    void unregister(UUID playerUUID);

}
