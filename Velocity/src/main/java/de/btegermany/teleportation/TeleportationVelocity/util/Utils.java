package de.btegermany.teleportation.TeleportationVelocity.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Utils {

    private final TeleportationVelocity plugin;
    private final ProxyServer proxyServer;
    private final PluginMessenger pluginMessenger;
    private final RegistriesProvider registriesProvider;

    public Utils(TeleportationVelocity plugin, ProxyServer proxyServer, PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
        this.plugin = plugin;
        this.proxyServer = proxyServer;
        this.pluginMessenger = pluginMessenger;
        this.registriesProvider = registriesProvider;
    }

    // teleports the player to the target player
    public void teleport(Player player, Player target) {
        player.sendMessage(Component.text(this.plugin.getPrefix() + " Du wirst zu " + target.getUsername() + " teleportiert...", NamedTextColor.GOLD));
        this.pluginMessenger.teleportToPlayer(player, target);
    }

    // cancels the tpa the player sent
    public void cancelTpa(Player player) {
        if (!this.registriesProvider.getTpasRegistry().isRegistered(player)) {
            player.sendMessage(Component.text(this.plugin.getPrefix() + " Du hast keine Anfrage gestellt!", NamedTextColor.GOLD));
            return;
        }

        registriesProvider.getTpasRegistry().unregister(player);
        player.sendMessage(Component.text(this.plugin.getPrefix() + " Die Anfrage wurde abgebrochen.", NamedTextColor.GOLD));
        this.proxyServer.getPlayer(this.registriesProvider.getTpasRegistry().getTpa(player)).ifPresent(target -> {
            target.sendMessage(Component.text(this.plugin.getPrefix() + " " + player.getUsername() + " hat die Anfrage abgebrochen!", NamedTextColor.GOLD));
        });
    }

}
