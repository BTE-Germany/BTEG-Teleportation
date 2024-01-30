package de.btegermany.teleportation.TeleportationBukkit.message;

import org.bukkit.entity.Player;

public class WarpAddTagMessage extends PluginMessage {

    public WarpAddTagMessage(Player player, String tag, int warpId) {
        byteOutput.writeUTF("tag_add");
        byteOutput.writeUTF(player.getUniqueId().toString());
        byteOutput.writeUTF(tag);
        byteOutput.writeUTF(String.valueOf(warpId));
    }

}
