package de.btegermany.teleportation.TeleportationVelocity.message;

import com.velocitypowered.api.proxy.Player;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;

public class PerformCommandMessage extends PluginMessage {

    public PerformCommandMessage(Player player, String command) {
        super("command_perform", MessageType.NORMAL);
        super.content.add(player.getUniqueId().toString());
        super.content.add(command);
    }

}
