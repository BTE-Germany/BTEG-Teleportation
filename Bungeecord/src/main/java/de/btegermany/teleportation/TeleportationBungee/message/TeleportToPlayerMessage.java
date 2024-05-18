package de.btegermany.teleportation.TeleportationBungee.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class TeleportToPlayerMessage extends PluginMessage {

    public TeleportToPlayerMessage(ProxiedPlayer player, ProxiedPlayer target) {
        super("teleport_player", MessageType.NORMAL);
        super.content.addAll(List.of(
                player.getUniqueId().toString(),
                target.getUniqueId().toString(),
                player.getServer().getInfo().getName()));
    }

}
