package de.btegermany.teleportation.TeleportationBungee.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class TeleportToCoordsMessage extends PluginMessage {

    public TeleportToCoordsMessage(ProxiedPlayer player, double x, double y, double z, Float yaw, Float pitch, String world) {
        super("teleport_coords", MessageType.NORMAL);
        super.content.addAll(List.of(
                player.getUniqueId().toString(),
                x + "," + y + "," + z,
                // default value is null. If not changed the player's current orientation will be used when teleporting to avoid confusion when yaw and pitch are set to 0
                yaw == null ? "null" : yaw.toString(),
                pitch == null ? "null" : pitch.toString(),
                // null to use the player's current world
                world == null ? "null" : world,
                player.getServer().getInfo().getName()));
    }

}
