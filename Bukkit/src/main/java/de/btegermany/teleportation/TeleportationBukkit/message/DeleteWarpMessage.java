package de.btegermany.teleportation.TeleportationBukkit.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import org.bukkit.entity.Player;

public class DeleteWarpMessage extends PluginMessage {

    public DeleteWarpMessage(Player player, int warpId) {
        super("warp_delete", MessageType.NORMAL);
        super.content.add(player.getUniqueId().toString());
        super.content.add(String.valueOf(warpId));
    }

}
