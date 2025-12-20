package de.btegermany.teleportation.TeleportationBukkit.message.response;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerWorldResponseMessage extends PluginMessageResponse {

    public PlayerWorldResponseMessage(int requestId, UUID playerUUID) {
        super(requestId, "player_world");

        Player player = Bukkit.getPlayer(playerUUID);
        if(player == null || !player.isOnline()) return;

        super.content.add(player.getWorld().getName());
    }

}
