package de.btegermany.teleportation.TeleportationBungee.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageWithResponse;
import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.message.response.GuiDataResponseMessage;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.util.Warp;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PluginMessenger {

    private final RegistriesProvider registriesProvider;

    public PluginMessenger(RegistriesProvider registriesProvider) {
        this.registriesProvider = registriesProvider;
    }

    public void sendMessageToServers(PluginMessage pluginMessage, ServerInfo... serverInfos) {
        if(pluginMessage instanceof PluginMessageWithResponse) {
            this.registriesProvider.getPluginMessagesWithResponseRegistry().register((PluginMessageWithResponse) pluginMessage);
        }
        for(ServerInfo serverInfo : serverInfos) {
            serverInfo.sendData(TeleportationBungee.PLUGIN_CHANNEL, pluginMessage.getBytes());
        }
    }

    public void sendMessageToServers(PluginMessage pluginMessage, String... serverNames) {
        List<String> serverNamesList = Arrays.stream(serverNames).toList();
        this.sendMessageToServers(pluginMessage, ProxyServer.getInstance().getServers().values().stream().filter(serverInfo -> serverNamesList.contains(serverInfo.getName())).toArray(ServerInfo[]::new));
    }

    public void sendMessageToAllServers(PluginMessage pluginMessage) {
        ProxyServer.getInstance().getServers().values().forEach(serverInfo -> this.sendMessageToServers(pluginMessage, serverInfo));
    }

    // teleports a player to another player across the network
    public void teleportToPlayer(ProxiedPlayer player, ProxiedPlayer target) {
        this.send(player, target.getServer().getInfo(), new TeleportToPlayerMessage(player, target));
    }

    // teleports a player to the specified coordinates across the network
    public void teleportToCoords(ProxiedPlayer player, ServerInfo server, double x, double y, double z, Float yaw, Float pitch) {
        this.send(player, server, new TeleportToCoordsMessage(player, x, y, z, yaw, pitch));
    }

    // sends a Plugin Message in order to perform a command as a player on a server
    public void performCommand(ProxiedPlayer player, String command) {
        this.sendMessageToServers(new PerformCommandMessage(player, command), player.getServer().getInfo());
    }

    // sends a list of all cities warps are located in to all servers (for tab completion)
    public void sendWarpCitiesToServers(Set<Warp> warps) {
        this.sendMessageToAllServers(new WarpCitiesMessage(warps));
    }

    // sends a list of all warp tags to all servers (for tab completion)
    public void sendWarpTagsToServers(Set<String> tags) {
        this.sendMessageToAllServers(new WarpTagsMessage(tags));
    }

    // sends gui data (JSON format) consisting of data for the requested pages
    public void sendGuiData(int requestId, ProxiedPlayer player, String title, JSONArray pagesData) {
        this.sendMessageToServers(new GuiDataResponseMessage(requestId, player, title, pagesData), player.getServer().getInfo());
    }

    // connects the player to the server if needed and sends a Plugin Message with the teleportation data to the specified server
    private void send(ProxiedPlayer player, ServerInfo server, PluginMessage pluginMessage) {
        if(!player.getServer().getInfo().equals(server)) {
            player.connect(server);
        }
        if(!server.getPlayers().isEmpty()) {
            server.sendData(TeleportationBungee.PLUGIN_CHANNEL, pluginMessage.getBytes());
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
            server.sendData(TeleportationBungee.PLUGIN_CHANNEL, pluginMessage.getBytes());
        }).start();
    }

}
