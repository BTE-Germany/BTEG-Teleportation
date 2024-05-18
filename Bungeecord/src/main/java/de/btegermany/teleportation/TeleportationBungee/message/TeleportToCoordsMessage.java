package de.btegermany.teleportation.TeleportationBungee.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class TeleportToCoordsMessage extends PluginMessage {

    public TeleportToCoordsMessage(ProxiedPlayer player, double x, double y, double z, float yaw, float pitch) {
        super("teleport_coords", MessageType.NORMAL);
        super.content.addAll(List.of(
                player.getUniqueId().toString(),
                x + "," + y + "," + z,
                String.valueOf(yaw),
                String.valueOf(pitch),
                player.getServer().getInfo().getName()));
    }

}
