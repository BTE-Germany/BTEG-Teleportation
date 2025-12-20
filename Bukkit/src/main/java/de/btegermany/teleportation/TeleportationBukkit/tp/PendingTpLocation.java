package de.btegermany.teleportation.TeleportationBukkit.tp;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.util.UUID;

public class PendingTpLocation extends PendingTeleportationAbstract {

    private final double x;
    private final double y;
    private final double z;
    private final Float yaw;
    private final Float pitch;
    private final String worldName;

    public PendingTpLocation(UUID playerUUID, double x, double y, double z, Float yaw, Float pitch, String worldName, String originServerName) {
        super(playerUUID, originServerName);
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldName = worldName;
    }

    @Override
    public boolean canTeleport() {
        return Bukkit.getPlayer(super.playerUUID) != null && Bukkit.getPlayer(super.playerUUID).isOnline();
    }

    @Override
    public void teleport() {
        Player player = Bukkit.getPlayer(super.playerUUID);
        World world = (worldName == null) ? player.getWorld() : Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.getWorld("world");
        }

        double yWorld = y;
        if (Double.isNaN(yWorld) || !NumberConversions.isFinite(yWorld)) {
            yWorld = world.getHighestBlockYAt((int) x, (int) z) + 1;
        }
        Location location = new Location(world, x, yWorld < -64 ? 3000 : yWorld, z);
        location.setYaw(yaw != null ? yaw : player.getLocation().getYaw());
        location.setPitch(pitch != null ? pitch : player.getLocation().getPitch());

        player.teleport(location);
    }

}
