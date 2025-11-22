package de.btegermany.teleportation.TeleportationBukkit.tp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PendingTpNormen extends PendingTeleportationAbstract {

    public PendingTpNormen(UUID playerUUID, String originServerName) {
        super(playerUUID, originServerName);
    }

    @Override
    public boolean canTeleport() {
        return Bukkit.getPlayer(playerUUID) != null && Bukkit.getPlayer(playerUUID).isOnline();
    }

    @Override
    public void teleport() {
        Player player = Bukkit.getPlayer(playerUUID);
        assert player != null; // canTeleport() checked
        World world = Bukkit.getWorld("normen-hub");

        player.teleport(new Location(world, 0.5, 5, 0.5));
    }

}
