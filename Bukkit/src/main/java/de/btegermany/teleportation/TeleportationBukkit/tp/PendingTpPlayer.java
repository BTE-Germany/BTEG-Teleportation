package de.btegermany.teleportation.TeleportationBukkit.tp;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PendingTpPlayer extends PendingTeleportationAbstract {

    private final UUID targetUUID;
    private final TeleportationBukkit plugin;

    public PendingTpPlayer(UUID playerUUID, UUID targetUUID, String originServerName, TeleportationBukkit plugin) {
        super(playerUUID, originServerName);
        this.targetUUID = targetUUID;
        this.plugin = plugin;
    }

    @Override
    public boolean canTeleport() {
        Player player = Bukkit.getPlayer(this.playerUUID);
        Player target = Bukkit.getPlayer(this.targetUUID);
        return (player != null) && player.isOnline() && target != null && target.isOnline();
    }

    @Override
    public void teleport() {
        Player player = Bukkit.getPlayer(this.playerUUID);
        Player target = Bukkit.getPlayer(this.targetUUID);
        assert player != null; // canTeleport() checked
        assert target != null; // canTeleport() checked
        Bukkit.getScheduler().runTask(this.plugin, () -> player.teleport(target));
    }

}
