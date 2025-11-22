package de.btegermany.teleportation.TeleportationBungee.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class TeleportToNormenMessage extends PluginMessage {

    public TeleportToNormenMessage(ProxiedPlayer player) {
        super("teleport_normen", MessageType.NORMAL);
        super.content.addAll(List.of(
                player.getUniqueId().toString(),
                player.getServer().getInfo().getName()));
    }

}
