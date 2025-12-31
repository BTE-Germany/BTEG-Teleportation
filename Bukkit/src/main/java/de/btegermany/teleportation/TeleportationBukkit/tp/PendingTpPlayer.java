package de.btegermany.teleportation.TeleportationBukkit.tp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PendingTpPlayer extends PendingTeleportationAbstract {

    UUID targetUUID;

    public PendingTpPlayer(UUID playerUUID, UUID targetUUID, String originServerName) {
        super(playerUUID, originServerName);
        this.targetUUID = targetUUID;
    }

    @Override
    public boolean canTeleport() {
        return Bukkit.getPlayer(playerUUID) != null && Bukkit.getPlayer(playerUUID).isOnline() && Bukkit.getPlayer(targetUUID) != null && Bukkit.getPlayer(targetUUID).isOnline();
    }

    @Override
    public boolean teleport() {
        Player player = Bukkit.getPlayer(playerUUID);
        Player target = Bukkit.getPlayer(targetUUID);
        return player.teleport(target);
    }

}
