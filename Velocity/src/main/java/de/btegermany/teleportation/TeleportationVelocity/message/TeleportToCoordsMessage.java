package de.btegermany.teleportation.TeleportationVelocity.message;

import com.velocitypowered.api.proxy.Player;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;

import java.util.List;

public class TeleportToCoordsMessage extends PluginMessage {

    public TeleportToCoordsMessage(Player player, double x, double y, double z, float yaw, float pitch) {
        super("teleport_coords", MessageType.NORMAL);
        if (player.getCurrentServer().isEmpty()) {
            return;
        }
        super.content.addAll(List.of(
                player.getUniqueId().toString(),
                x + "," + y + "," + z,
                String.valueOf(yaw),
                String.valueOf(pitch),
                player.getCurrentServer().get().getServerInfo().getName()));
    }

}
