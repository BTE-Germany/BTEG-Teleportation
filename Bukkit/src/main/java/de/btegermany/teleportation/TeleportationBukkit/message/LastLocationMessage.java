package de.btegermany.teleportation.TeleportationBukkit.message;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LastLocationMessage extends PluginMessage {

    public LastLocationMessage(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if(!player.isOnline()) return;
        Location location = player.getLocation();

        byteOutput.writeUTF("last_location");
        byteOutput.writeUTF(playerUUID.toString());
        byteOutput.writeUTF(String.valueOf(location.getX()));
        byteOutput.writeUTF(String.valueOf(location.getY()));
        byteOutput.writeUTF(String.valueOf(location.getZ()));
        byteOutput.writeUTF(String.valueOf(location.getYaw()));
        byteOutput.writeUTF(String.valueOf(location.getPitch()));
    }

}
