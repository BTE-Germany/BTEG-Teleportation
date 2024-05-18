package de.btegermany.teleportation.TeleportationBukkit.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpAddTagMessage extends PluginMessage {

    public WarpAddTagMessage(Player player, String tag, int warpId) {
        super("tag_add", MessageType.NORMAL);
        super.content.addAll(List.of(
                player.getUniqueId().toString(),
                tag,
                String.valueOf(warpId)));
    }

}
