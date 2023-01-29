package de.btegermany.teleportation.TeleportationBungee.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PluginMessenger {

    public static void teleportToPlayer(ProxiedPlayer player, ProxiedPlayer target) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("teleport_player");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(target.getUniqueId().toString());
        send(player, target.getServer().getInfo(), out.toByteArray());
    }

    public static void teleportToCoords(ProxiedPlayer player, ServerInfo server, double x, double y, double z) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("teleport_coords");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(x + "," + y + "," + z);
        send(player, server, out.toByteArray());
    }

    private static void send(ProxiedPlayer player, ServerInfo server, byte[] bytes) {
        if(!player.getServer().getInfo().equals(server)) player.connect(server);
        server.sendData(TeleportationBungee.PLUGIN_CHANNEL, bytes);
    }

}
