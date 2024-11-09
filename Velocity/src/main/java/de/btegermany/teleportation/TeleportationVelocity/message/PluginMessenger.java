package de.btegermany.teleportation.TeleportationVelocity.message;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageWithResponse;
import de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity;
import de.btegermany.teleportation.TeleportationVelocity.message.response.GuiDataResponseMessage;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.Warp;
import org.json.JSONArray;

import java.util.Optional;
import java.util.Set;

public class PluginMessenger {

    private final ProxyServer proxyServer;
    private final RegistriesProvider registriesProvider;

    public PluginMessenger(ProxyServer proxyServer, RegistriesProvider registriesProvider) {
        this.proxyServer = proxyServer;
        this.registriesProvider = registriesProvider;
    }

    public void sendMessageToServers(PluginMessage pluginMessage, RegisteredServer... servers) {
        if (pluginMessage instanceof PluginMessageWithResponse) {
            this.registriesProvider.getPluginMessagesWithResponseRegistry().register((PluginMessageWithResponse) pluginMessage);
        }
        for (RegisteredServer server : servers) {
            server.sendPluginMessage(TeleportationVelocity.PLUGIN_CHANNEL, pluginMessage.getBytes());
        }
    }

    public void sendMessageToServers(PluginMessage pluginMessage, String... serverNames) {
        for (String serverName : serverNames) {
            Optional<RegisteredServer> serverOptional = this.proxyServer.getServer(serverName);
            if (serverOptional.isEmpty()) {
                continue;
            }
            this.sendMessageToServers(pluginMessage, serverOptional.get());
        }
    }

    public void sendMessageToAllServers(PluginMessage pluginMessage) {
        this.proxyServer.getAllServers().forEach(server -> this.sendMessageToServers(pluginMessage, server));
    }

    // teleports a player to another player across the network
    public void teleportToPlayer(Player player, Player target) {
        if (target.getCurrentServer().isEmpty()) {
            return;
        }
        this.sendAndConnect(player, target.getCurrentServer().get().getServer(), new TeleportToPlayerMessage(player, target));
    }

    // teleports a player to the specified coordinates across the network
    public void teleportToCoords(Player player, RegisteredServer server, double x, double y, double z, float yaw, float pitch) {
        this.sendAndConnect(player, server, new TeleportToCoordsMessage(player, x, y, z, yaw, pitch));
    }

    // sends a Plugin Message in order to perform a command as a player on a server
    public void performCommand(Player player, String command) {
        if (player.getCurrentServer().isEmpty()) {
            return;
        }
        this.sendMessageToServers(new PerformCommandMessage(player, command), player.getCurrentServer().get().getServer());
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
    public void sendGuiData(int requestId, Player player, String title, JSONArray pagesData) {
        if (player.getCurrentServer().isEmpty()) {
            return;
        }
        this.sendMessageToServers(new GuiDataResponseMessage(requestId, player, title, pagesData), player.getCurrentServer().get().getServer());
    }

    // connects the player to the server if needed and sends a Plugin Message with the teleportation data to the specified server
    private void sendAndConnect(Player player, RegisteredServer server, PluginMessage pluginMessage) {
        Optional<ServerConnection> serverConnectionOptional = player.getCurrentServer();
        if (serverConnectionOptional.isEmpty()) {
            return;
        }
        if (!serverConnectionOptional.get().getServer().equals(server)) {
            player.createConnectionRequest(server).connect();
        }
        if (!server.getPlayersConnected().isEmpty()) {
            server.sendPluginMessage(TeleportationVelocity.PLUGIN_CHANNEL, pluginMessage.getBytes());
            return;
        }
        new Thread(() -> {
            while (player.getCurrentServer().isEmpty() || (player.getCurrentServer().isPresent() && !server.equals(player.getCurrentServer().get().getServer()))) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            server.sendPluginMessage(TeleportationVelocity.PLUGIN_CHANNEL, pluginMessage.getBytes());
        }).start();
    }

}
