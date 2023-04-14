package de.btegermany.teleportation.TeleportationBukkit.message;

import org.bukkit.entity.Player;

public class DeleteWarpMessage extends PluginMessage {

    public DeleteWarpMessage(Player player, int warpId) {
        byteOutput.writeUTF("warp_delete");
        byteOutput.writeUTF(player.getUniqueId().toString());
        byteOutput.writeUTF(String.valueOf(warpId));
    }

}
