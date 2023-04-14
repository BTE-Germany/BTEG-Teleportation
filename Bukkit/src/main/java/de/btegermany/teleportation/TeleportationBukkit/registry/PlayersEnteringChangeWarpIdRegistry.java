package de.btegermany.teleportation.TeleportationBukkit.registry;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayersEnteringChangeWarpIdRegistry implements Registry {

    private final Set<UUID> playersChangingWarps;

    public PlayersEnteringChangeWarpIdRegistry() {
        this.playersChangingWarps = new HashSet<>();
    }

    public void register(Player player) {
        this.register(player.getUniqueId());
    }

    public void register(UUID playerUUID) {
        playersChangingWarps.add(playerUUID);
    }

    @Override
    public void unregister(UUID playerUUID) {
        playersChangingWarps.remove(playerUUID);
    }

    @Override
    public boolean isRegistered(UUID playerUUID) {
        return playersChangingWarps.contains(playerUUID);
    }

    public Set<UUID> getPlayersChangingWarps() {
        return playersChangingWarps;
    }

}
