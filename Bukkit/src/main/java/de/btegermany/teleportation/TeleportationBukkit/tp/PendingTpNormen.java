package de.btegermany.teleportation.TeleportationBukkit.tp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PendingTpNormen extends PendingTeleportationAbstract {

    private final String normenWorld;
    private final float yaw;
    private final float pitch;

    public PendingTpNormen(UUID playerUUID, String normenWorld, String originServerName, float yaw, float pitch) {
        super(playerUUID, originServerName);

        this.normenWorld = normenWorld;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public boolean canTeleport() {
        return Bukkit.getPlayer(playerUUID) != null && Bukkit.getPlayer(playerUUID).isOnline();
    }

    @Override
    public boolean teleport() {
        Player player = Bukkit.getPlayer(playerUUID);
        assert player != null; // canTeleport() checked
        World world = Bukkit.getWorld(this.normenWorld);

        Location location = new Location(world, 0.5, 5, 0.5);
        location.setYaw(this.yaw);
        location.setPitch(this.pitch);

        return player.teleport(location);
    }

}
