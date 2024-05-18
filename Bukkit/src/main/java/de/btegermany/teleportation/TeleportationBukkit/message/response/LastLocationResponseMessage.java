package de.btegermany.teleportation.TeleportationBukkit.message.response;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class LastLocationResponseMessage extends PluginMessageResponse {

    public LastLocationResponseMessage(int requestId, UUID playerUUID) {
        super(requestId, "last_location");

        Player player = Bukkit.getPlayer(playerUUID);
        if(player == null || !player.isOnline()) return;
        Location location = player.getLocation();

        super.content.addAll(List.of(
                playerUUID.toString(),
                String.valueOf(location.getX()),
                String.valueOf(location.getY()),
                String.valueOf(location.getZ()),
                String.valueOf(location.getYaw()),
                String.valueOf(location.getPitch())));
    }

}
