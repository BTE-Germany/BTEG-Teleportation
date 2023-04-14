package de.btegermany.teleportation.TeleportationBukkit.message;

import de.btegermany.teleportation.TeleportationBukkit.util.WarpGettingChanged;
import org.bukkit.entity.Player;

public class ChangeWarpMessage extends PluginMessage {

    public ChangeWarpMessage(Player player, WarpGettingChanged warp) {
        byteOutput.writeUTF("warp_change");
        byteOutput.writeUTF(player.getUniqueId().toString());
        byteOutput.writeUTF(String.valueOf(warp.getId()));
        byteOutput.writeUTF(warp.getColumn());
        byteOutput.writeUTF(warp.getValue() != null ? warp.getValue() : "null");
    }

}
