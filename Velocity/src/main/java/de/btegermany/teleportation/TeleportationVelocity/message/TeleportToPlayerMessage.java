package de.btegermany.teleportation.TeleportationVelocity.message;

import com.velocitypowered.api.proxy.Player;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;

import java.util.List;

public class TeleportToPlayerMessage extends PluginMessage {

    public TeleportToPlayerMessage(Player player, Player target) {
        super("teleport_player", MessageType.NORMAL);
        if (player.getCurrentServer().isEmpty()) {
            return;
        }
        super.content.addAll(List.of(
                player.getUniqueId().toString(),
                target.getUniqueId().toString(),
                player.getCurrentServer().get().getServerInfo().getName()));
    }

}
