package de.btegermany.teleportation.TeleportationBukkit.tp;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PendingTpNormen extends PendingTeleportationAbstract {

    private final String normenWorld;
    private final float yaw;
    private final float pitch;
    private final TeleportationBukkit plugin;

    public PendingTpNormen(UUID playerUUID, String normenWorld, String originServerName, float yaw, float pitch, TeleportationBukkit plugin) {
        super(playerUUID, originServerName);

        this.normenWorld = normenWorld;
        this.yaw = yaw;
        this.pitch = pitch;
        this.plugin = plugin;
    }

    @Override
    public boolean canTeleport() {
        Player player = Bukkit.getPlayer(this.playerUUID);
        return player != null && player.isOnline();
    }

    @Override
    public void teleport() {
        Player player = Bukkit.getPlayer(this.playerUUID);
        assert player != null; // canTeleport() checked
        World world = Bukkit.getWorld(this.normenWorld);

        Location location = new Location(world, 0.5, 5, 0.5);
        location.setYaw(this.yaw);
        location.setPitch(this.pitch);

        Bukkit.getScheduler().runTask(this.plugin, () -> player.teleport(location));
    }

}
