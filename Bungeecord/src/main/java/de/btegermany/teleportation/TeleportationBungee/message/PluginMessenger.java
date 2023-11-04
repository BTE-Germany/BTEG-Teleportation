package de.btegermany.teleportation.TeleportationBungee.message;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.util.Warp;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;
import java.util.stream.Collectors;

public class PluginMessenger {

    // teleports a player to another player across the network
    public void teleportToPlayer(ProxiedPlayer player, ProxiedPlayer target) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("teleport_player");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(target.getUniqueId().toString());
        send(player, target.getServer().getInfo(), out.toByteArray());
    }

    // teleports a player to the specified coordinates across the network
    public void teleportToCoords(ProxiedPlayer player, ServerInfo server, double x, double y, double z, float yaw, float pitch) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("teleport_coords");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(x + "," + y + "," + z);
        out.writeUTF(String.valueOf(yaw));
        out.writeUTF(String.valueOf(pitch));
        send(player, server, out.toByteArray());
    }

    // sends gui data (JSON format) consisting of data for the requested pages
    public void sendGuiData(ProxiedPlayer player, String title, JSONArray pagesData) {
        JSONObject object = new JSONObject();
        object.put("title", title);
        object.put("player_uuid", player.getUniqueId().toString());
        object.put("pagesData", pagesData);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("gui_data");
        out.writeUTF(object.toString());
        player.getServer().sendData(TeleportationBungee.PLUGIN_CHANNEL, out.toByteArray());
    }

    // sends warp data
    public void sendWarpInfo(ProxiedPlayer player, Warp warp, int responseNumber) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("warp_info");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(String.valueOf(responseNumber));
        out.writeUTF(String.valueOf(warp.getId()));
        out.writeUTF(warp.getName());
        out.writeUTF(warp.getCity());
        out.writeUTF(warp.getState());
        out.writeUTF(String.valueOf(warp.getLatitude()));
        out.writeUTF(String.valueOf(warp.getLongitude()));
        out.writeUTF(warp.getHeadId() != null ? warp.getHeadId() : "null");
        out.writeUTF(String.valueOf(warp.getYaw()));
        out.writeUTF(String.valueOf(warp.getPitch()));
        out.writeUTF(String.valueOf(warp.getHeight()));
        player.getServer().sendData(TeleportationBungee.PLUGIN_CHANNEL, out.toByteArray());
    }

    // sends a Plugin Message in order to perform a command as a player on a server
    public void performCommand(ProxiedPlayer player, String command) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("command_perform");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(command);
        player.getServer().sendData(TeleportationBungee.PLUGIN_CHANNEL, out.toByteArray());
    }

    // sends a list of all cities warps are located in to all server (for tab completion)
    public void sendCitiesToServers(Set<Warp> warps) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("list_cities");
        warps.stream().map(Warp::getCity).collect(Collectors.toSet()).forEach(out::writeUTF);
        for(ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
            serverInfo.sendData(TeleportationBungee.PLUGIN_CHANNEL, out.toByteArray());
        }
    }

    // connects the player to the server if needed and sends a Plugin Message with the teleportation data to the specified server
    private void send(ProxiedPlayer player, ServerInfo server, byte[] bytes) {
        if(!player.getServer().getInfo().equals(server)) {
            player.connect(server);
        }
        if(server.getPlayers().size() > 0) {
            server.sendData(TeleportationBungee.PLUGIN_CHANNEL, bytes);
            return;
        }
        new Thread(() -> {
            while(!server.equals(player.getServer().getInfo())) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            server.sendData(TeleportationBungee.PLUGIN_CHANNEL, bytes);
        }).start();
    }

}
