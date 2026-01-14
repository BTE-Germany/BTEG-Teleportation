package de.btegermany.teleportation.TeleportationVelocity.registry;

import com.velocitypowered.api.proxy.Player;

import java.util.UUID;

public interface PlayerRegistry {

    default boolean isRegistered(Player player) {
        return isRegistered(player.getUniqueId());
    }

    boolean isRegistered(UUID playerUUID);

    default void unregister(Player player) {
        this.unregister(player.getUniqueId());
    }

    void unregister(UUID playerUUID);

}
