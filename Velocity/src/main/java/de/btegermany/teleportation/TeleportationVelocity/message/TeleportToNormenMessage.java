package de.btegermany.teleportation.TeleportationVelocity.message;

import com.velocitypowered.api.proxy.Player;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;

import java.util.List;

public class TeleportToNormenMessage extends PluginMessage {

    public TeleportToNormenMessage(Player player, String normenWorld, float yaw, float pitch) {
        super("teleport_normen", MessageType.NORMAL);
        if (player.getCurrentServer().isEmpty()) {
            return;
        }
        super.content.addAll(List.of(
                player.getUniqueId().toString(),
                normenWorld,
                String.valueOf(yaw),
                String.valueOf(pitch),
                player.getCurrentServer().get().getServerInfo().getName()));
    }

}
