package de.btegermany.teleportation.TeleportationVelocity.util;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.TimeUnit;

public class Utils {

    public static final String WORLD_TERRA = "world";
    public static final String WORLD_PLOT_LOBBY = "plot-lobby";
    private final ProxyServer proxyServer;
    private final PluginMessenger pluginMessenger;
    private final RegistriesProvider registriesProvider;

    public Utils(ProxyServer proxyServer, PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
        this.proxyServer = proxyServer;
        this.pluginMessenger = pluginMessenger;
        this.registriesProvider = registriesProvider;
    }

    // teleports the player to the target player
    public void teleport(Player player, Player target) {
        sendMessage(player, Component.text("Du wirst zu " + target.getUsername() + " teleportiert...", NamedTextColor.GOLD));
        this.pluginMessenger.teleportToPlayer(player, target);
    }

    // cancels the tpa the player sent
    public void cancelTpa(Player player) {
        if(!this.registriesProvider.getTpasRegistry().isRegistered(player)) {
            sendMessage(player, Component.text("Du hast keine Anfrage gestellt!", NamedTextColor.GOLD));
            return;
        }

        registriesProvider.getTpasRegistry().unregister(player);
        sendMessage(player, Component.text("Die Anfrage wurde abgebrochen.", NamedTextColor.GOLD));
        this.proxyServer.getPlayer(this.registriesProvider.getTpasRegistry().getTpa(player)).ifPresent(target -> {
            sendMessage(target, Component.text(player.getUsername() + " hat die Anfrage abgebrochen!", NamedTextColor.GOLD));
        });
    }

    public static void connectIfOnline(Player player, RegisteredServer server) {
        connectIfOnline(player, server, null, null);
    }

    public static void connectIfOnline(Player player, RegisteredServer server, String messageOk) {
        connectIfOnline(player, server, messageOk, null);
    }

    public static void connectIfOnline(Player player, RegisteredServer server, String messageOk, String messageTimeout) {
            server.ping().orTimeout(3, TimeUnit.SECONDS)
                    .exceptionally(throwable -> {
                        sendMessage(player, Component.text(messageTimeout == null ? "Server %s is offline.".formatted(server.getServerInfo().getName()) : messageTimeout, NamedTextColor.RED));
                        return null;
                    })
                    .thenAccept(pingResult -> {
                        if (pingResult == null) {
                            return;
                        }

                        if (messageOk != null) {
                            sendMessage(player, Component.text(messageOk, NamedTextColor.GOLD));
                        }

                        player.createConnectionRequest(server).connect();
                    });
    }

}
