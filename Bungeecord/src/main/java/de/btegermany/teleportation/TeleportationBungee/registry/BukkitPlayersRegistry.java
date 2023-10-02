package de.btegermany.teleportation.TeleportationBungee.registry;

import de.btegermany.teleportation.TeleportationBungee.util.BukkitPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BukkitPlayersRegistry implements PlayerRegistry {

    ConcurrentMap<UUID, BukkitPlayer> bukkitPlayers = new ConcurrentHashMap<>();

    public void register(BukkitPlayer bukkitPlayer) {
        bukkitPlayers.put(bukkitPlayer.getProxiedPlayer().getUniqueId(), bukkitPlayer);
    }

    @Override
    public boolean isRegistered(UUID playerUUID) {
        return bukkitPlayers.containsKey(playerUUID);
    }

    @Override
    public void unregister(UUID playerUUID) {
        bukkitPlayers.remove(playerUUID);
    }

    public BukkitPlayer getBukkitPlayer(UUID playerUUID) {
        return bukkitPlayers.get(playerUUID);
    }

    public void replace(BukkitPlayer bukkitPlayer) {
        bukkitPlayers.replace(bukkitPlayer.getProxiedPlayer().getUniqueId(), bukkitPlayer);
    }

    public Map<UUID, BukkitPlayer> getBukkitPlayers() {
        return bukkitPlayers;
    }
}
