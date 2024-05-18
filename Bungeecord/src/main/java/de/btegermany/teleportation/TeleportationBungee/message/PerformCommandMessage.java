package de.btegermany.teleportation.TeleportationBungee.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PerformCommandMessage extends PluginMessage {

    public PerformCommandMessage(ProxiedPlayer player, String command) {
        super("command_perform", MessageType.NORMAL);
        super.content.add(player.getUniqueId().toString());
        super.content.add(command);
    }

}
