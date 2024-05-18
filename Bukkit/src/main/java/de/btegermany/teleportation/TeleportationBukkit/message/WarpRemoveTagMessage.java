
package de.btegermany.teleportation.TeleportationBukkit.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpRemoveTagMessage extends PluginMessage {

    public WarpRemoveTagMessage(Player player, String tag, int warpId) {
        super("tag_remove", MessageType.NORMAL);
        super.content.addAll(List.of(
                player.getUniqueId().toString(),
                tag,
                String.valueOf(warpId)));
    }

}
