package de.btegermany.teleportation.TeleportationBungee.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONArray;
import org.json.JSONObject;

public class PluginMessenger {

    public void teleportToPlayer(ProxiedPlayer player, ProxiedPlayer target) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("teleport_player");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(target.getUniqueId().toString());
        send(player, target.getServer().getInfo(), out.toByteArray());
    }

    public void teleportToCoords(ProxiedPlayer player, ServerInfo server, double x, double y, double z, float yaw, float pitch) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("teleport_coords");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(x + "," + y + "," + z);
        out.writeUTF(String.valueOf(yaw));
        out.writeUTF(String.valueOf(pitch));
        send(player, server, out.toByteArray());
    }

    public void sendGuiData(ProxiedPlayer player, String title, ServerInfo server, JSONArray pagesData) {
        JSONObject object = new JSONObject();
        object.put("title", title);
        object.put("player_uuid", player.getUniqueId().toString());
        object.put("pagesData", pagesData);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("gui_data");
        out.writeUTF(object.toString());
        server.sendData(TeleportationBungee.PLUGIN_CHANNEL, out.toByteArray());
    }

    public void sendWarpInfo(ProxiedPlayer proxiedPlayer, Warp warp, int responseNumber) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("warp_info");
        out.writeUTF(proxiedPlayer.getUniqueId().toString());
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
        proxiedPlayer.getServer().sendData(TeleportationBungee.PLUGIN_CHANNEL, out.toByteArray());
    }

    private void send(ProxiedPlayer player, ServerInfo server, byte[] bytes) {
        if(!player.getServer().getInfo().equals(server)) player.connect(server);
        server.sendData(TeleportationBungee.PLUGIN_CHANNEL, bytes);
    }

}
