package de.btegermany.teleportation.TeleportationBukkit.message;

public class ExecuteCommandMessage extends PluginMessage {

    public ExecuteCommandMessage(String playerUUID, String command) {
        byteOutput.writeUTF("execute_command");
        byteOutput.writeUTF(playerUUID);
        byteOutput.writeUTF(command);
    }

}
