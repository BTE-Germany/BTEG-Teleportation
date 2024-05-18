package de.btegermany.teleportation.TeleportationBukkit.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

public class BukkitPlayersMessage extends PluginMessage {

    public BukkitPlayersMessage(Collection<? extends Player> players) {
        super("players_online", MessageType.NORMAL);

        JSONArray jsonArray = new JSONArray();
        players.forEach(player -> {
            JSONObject playerObject = new JSONObject();
            playerObject.put("player_uuid", player.getUniqueId().toString());
            playerObject.put("x", player.getLocation().getX());
            playerObject.put("y", player.getLocation().getY());
            playerObject.put("z", player.getLocation().getZ());
            playerObject.put("yaw", Double.parseDouble(String.valueOf(player.getLocation().getYaw())));
            playerObject.put("pitch", Double.parseDouble(String.valueOf(player.getLocation().getPitch())));
            playerObject.put("gamemode", player.getGameMode().toString());
            jsonArray.put(playerObject);
        });
        super.content.add(Bukkit.getServer().getIp() + ":" + Bukkit.getPort());
        super.content.add(jsonArray.toString());
    }

}
