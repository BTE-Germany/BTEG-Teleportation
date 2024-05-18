package de.btegermany.teleportation.TeleportationBukkit.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import de.btegermany.teleportation.TeleportationBukkit.util.WarpGettingChanged;
import org.bukkit.entity.Player;

import java.util.List;

public class ChangeWarpMessage extends PluginMessage {

    public ChangeWarpMessage(Player player, WarpGettingChanged warp) {
        super("warp_change", MessageType.NORMAL);
        super.content.addAll(List.of(
                player.getUniqueId().toString(),
                String.valueOf(warp.getId()),
                warp.getColumn(),
                warp.getValue() != null ? warp.getValue() : "null"));
    }

}
