package de.btegermany.teleportation.TeleportationBukkit.message;

import org.bukkit.entity.Player;

public class TpToRandomWarpMessage extends PluginMessage {

    public TpToRandomWarpMessage(Player player) {
        byteOutput.writeUTF("tp_random_warp");
        byteOutput.writeUTF(player.getUniqueId().toString());
    }

}
