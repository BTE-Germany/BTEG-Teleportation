package de.btegermany.teleportation.TeleportationBukkit.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpEditTagMessage extends PluginMessage {

    public WarpEditTagMessage(Player player, String tagOld, String tagNew) {
        super("tag_edit", MessageType.NORMAL);
        super.content.addAll(List.of(
                player.getUniqueId().toString(),
                tagOld,
                tagNew));
    }

}
