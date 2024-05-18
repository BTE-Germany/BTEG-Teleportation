package de.btegermany.teleportation.TeleportationBukkit.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;

public class ExecuteCommandMessage extends PluginMessage {

    public ExecuteCommandMessage(String playerUUID, String command) {
        super("execute_command", MessageType.NORMAL);
        super.content.add(playerUUID);
        super.content.add(command);
    }

}
