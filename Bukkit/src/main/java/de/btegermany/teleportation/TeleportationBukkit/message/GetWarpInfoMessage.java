package de.btegermany.teleportation.TeleportationBukkit.message;

import org.bukkit.entity.Player;

public class GetWarpInfoMessage extends PluginMessage {

    public GetWarpInfoMessage(Player player, int warpId, int responseNumber) {
        byteOutput.writeUTF("get_warp_info");
        byteOutput.writeUTF(player.getUniqueId().toString());
        byteOutput.writeUTF(String.valueOf(warpId));
        byteOutput.writeUTF(String.valueOf(responseNumber));
    }

}
