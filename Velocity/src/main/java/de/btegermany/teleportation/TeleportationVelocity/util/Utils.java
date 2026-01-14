package de.btegermany.teleportation.TeleportationVelocity.util;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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

}
