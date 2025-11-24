package de.btegermany.teleportation.TeleportationBukkit.tp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PendingTpLocation extends PendingTeleportationAbstract {

    private final World world;
    private final double x;
    private final double y;
    private final double z;
    private final Float yaw;
    private final Float pitch;

    public PendingTpLocation(UUID playerUUID, World world, double x, double y, double z, Float yaw, Float pitch, String originServerName) {
        super(playerUUID, originServerName);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public boolean canTeleport() {
        return Bukkit.getPlayer(super.playerUUID) != null && Bukkit.getPlayer(super.playerUUID).isOnline();
    }

    @Override
    public void teleport() {
        Player player = Bukkit.getPlayer(super.playerUUID);
        Location location;
        location = new Location(world, x, y < -64 ? 3000 : y, z);
        location.setYaw(yaw != null ? yaw : player.getLocation().getYaw());
        location.setPitch(pitch != null ? pitch : player.getLocation().getPitch());
        player.teleport(location);
    }

}
