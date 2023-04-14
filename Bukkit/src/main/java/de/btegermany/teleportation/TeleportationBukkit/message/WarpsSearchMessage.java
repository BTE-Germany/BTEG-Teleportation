package de.btegermany.teleportation.TeleportationBukkit.message;

import org.bukkit.entity.Player;

public class WarpsSearchMessage extends PluginMessage {

    public WarpsSearchMessage(Player player, String search) {
        byteOutput.writeUTF("warps_search");
        byteOutput.writeUTF(player.getUniqueId().toString());
        byteOutput.writeUTF(search);
    }

}
