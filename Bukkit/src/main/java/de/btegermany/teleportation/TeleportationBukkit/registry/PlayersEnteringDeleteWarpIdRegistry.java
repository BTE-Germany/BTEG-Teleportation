package de.btegermany.teleportation.TeleportationBukkit.registry;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayersEnteringDeleteWarpIdRegistry implements Registry {

    private final Set<UUID> playersEnteringWarpId;

    public PlayersEnteringDeleteWarpIdRegistry() {
        this.playersEnteringWarpId = new HashSet<>();
    }

    public void register(Player player) {
        this.register(player.getUniqueId());
    }

    public void register(UUID playerUUID) {
        playersEnteringWarpId.add(playerUUID);
    }

    @Override
    public void unregister(UUID playerUUID) {
        playersEnteringWarpId.remove(playerUUID);
    }

    @Override
    public boolean isRegistered(UUID playerUUID) {
        return playersEnteringWarpId.contains(playerUUID);
    }

    public Set<UUID> getPlayersEnteringWarpId() {
        return playersEnteringWarpId;
    }
}
