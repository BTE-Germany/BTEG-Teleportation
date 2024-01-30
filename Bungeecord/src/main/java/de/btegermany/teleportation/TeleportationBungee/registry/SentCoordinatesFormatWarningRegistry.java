package de.btegermany.teleportation.TeleportationBungee.registry;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SentCoordinatesFormatWarningRegistry {

    private final Set<UUID> playerUUIDs;

    public SentCoordinatesFormatWarningRegistry() {
        this.playerUUIDs = new HashSet<>();
    }

    public void register(ProxiedPlayer player) {
        this.playerUUIDs.add(player.getUniqueId());
    }

    public void unregister(ProxiedPlayer player) {
        this.playerUUIDs.remove(player.getUniqueId());
    }

    public boolean isRegistered(ProxiedPlayer player) {
        return this.playerUUIDs.contains(player.getUniqueId());
    }

}
