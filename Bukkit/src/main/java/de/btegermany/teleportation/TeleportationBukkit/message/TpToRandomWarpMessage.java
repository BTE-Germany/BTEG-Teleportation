package de.btegermany.teleportation.TeleportationBukkit.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import org.bukkit.entity.Player;

public class TpToRandomWarpMessage extends PluginMessage {

    public TpToRandomWarpMessage(Player player) {
        super("tp_random_warp", MessageType.NORMAL);
        super.content.add(player.getUniqueId().toString());
    }

}
