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
    private final TeleportationBukkit plugin;

    public PendingTpLocation(UUID playerUUID, double x, double y, double z, Float yaw, Float pitch, String worldName, String originServerName, TeleportationBukkit plugin) {
        super(playerUUID, originServerName);
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldName = worldName;
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

        Bukkit.getScheduler().runTask(this.plugin, () -> player.teleport(location));
    }

}
