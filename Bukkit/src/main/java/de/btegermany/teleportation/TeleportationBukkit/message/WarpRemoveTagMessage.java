
package de.btegermany.teleportation.TeleportationBukkit.message;

import org.bukkit.entity.Player;

public class WarpRemoveTagMessage extends PluginMessage {
    public WarpRemoveTagMessage(Player player, String tag, int warpId) {
        byteOutput.writeUTF("tag_remove");
        byteOutput.writeUTF(player.getUniqueId().toString());
        byteOutput.writeUTF(tag);
        byteOutput.writeUTF(String.valueOf(warpId));
    }

}
