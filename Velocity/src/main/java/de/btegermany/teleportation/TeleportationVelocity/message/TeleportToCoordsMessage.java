package de.btegermany.teleportation.TeleportationVelocity.message;

import com.velocitypowered.api.proxy.Player;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;

import java.util.List;

public class TeleportToCoordsMessage extends PluginMessage {

    public TeleportToCoordsMessage(Player player, double x, double y, double z, Float yaw, Float pitch, String world) {
        super("teleport_coords", MessageType.NORMAL);
        if (player.getCurrentServer().isEmpty()) {
            return;
        }
        super.content.addAll(List.of(
                player.getUniqueId().toString(),
                x + "," + y + "," + z,
                // default value is null. If not changed the player's current orientation will be used when teleporting to avoid confusion when yaw and pitch are set to 0
                yaw == null ? "null" : yaw.toString(),
                pitch == null ? "null" : pitch.toString(),
                // null to use the player's current world
                world == null ? "null" : world,
                player.getCurrentServer().get().getServerInfo().getName()));
    }

}
