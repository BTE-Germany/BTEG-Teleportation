package de.btegermany.teleportation.TeleportationBukkit.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import org.bukkit.entity.Player;

public class ExecuteCommandMessage extends PluginMessage {

    public ExecuteCommandMessage(Player player, String command) {
        super("execute_command", MessageType.NORMAL);
        super.content.add(player.getUniqueId().toString());
        super.content.add(command);
    }

}
