package de.btegermany.teleportation.TeleportationBungee.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;

import java.util.Set;

public class WarpTagsMessage extends PluginMessage {

    public WarpTagsMessage(Set<String> tags) {
        super("list_tags", MessageType.NORMAL);
        super.content.addAll(tags);
    }

}
