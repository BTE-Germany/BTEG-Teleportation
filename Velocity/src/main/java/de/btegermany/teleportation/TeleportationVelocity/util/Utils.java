package de.btegermany.teleportation.TeleportationVelocity.util;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
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
        connectIfOnline(player, server, null);
    }

    public static void connectIfOnline(Player player, RegisteredServer server, String messageError) {
        Runnable sendErrorMessage = () -> sendMessage(player, Component.text(messageError == null ? "Server %s is offline.".formatted(server.getServerInfo().getName()) : messageError, NamedTextColor.RED));

        // without ping the player would "join" again and e.g. the maintenances notice will be sent again
        server.ping().orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    sendErrorMessage.run();
                    return null;
                })
                .thenAccept(pingResult -> {
                    if (pingResult == null) {
                        return;
                    }

                    player.createConnectionRequest(server).connect()
                            .exceptionally(throwable -> {
                                sendErrorMessage.run();
                                return null;
                            })
                            .thenAccept(result -> {
                                if (result.isSuccessful() || result.getStatus() == ConnectionRequestBuilder.Status.CONNECTION_CANCELLED) {
                                    return;
                                }
                                sendErrorMessage.run();
                            });
                });
    }

}
