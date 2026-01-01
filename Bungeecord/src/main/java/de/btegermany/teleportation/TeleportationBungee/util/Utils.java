package de.btegermany.teleportation.TeleportationBungee.util;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static de.btegermany.teleportation.TeleportationBungee.TeleportationBungee.getFormattedMessage;

public class Utils {

    public static final String WORLD_TERRA = "world";
    public static final String WORLD_PLOT_LOBBY = "plot-lobby";
    private final PluginMessenger pluginMessenger;
    private final RegistriesProvider registriesProvider;

    public Utils(PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
        this.pluginMessenger = pluginMessenger;
        this.registriesProvider = registriesProvider;
    }

    // teleports the player to the target player
    public void teleport(ProxiedPlayer player, ProxiedPlayer target) {
        player.sendMessage(TeleportationBungee.getFormattedMessage("Du wirst zu " + target.getName() + " teleportiert..."));
        pluginMessenger.teleportToPlayer(player, target);
    }

    // cancels the tpa the player sent
    public void cancelTpa(ProxiedPlayer player) {
        if(!registriesProvider.getTpasRegistry().isRegistered(player)) {
            player.sendMessage(getFormattedMessage("Du hast keine Anfrage gestellt!"));
            return;
        }
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(registriesProvider.getTpasRegistry().getTpa(player));
        registriesProvider.getTpasRegistry().unregister(player);
        player.sendMessage(getFormattedMessage("Die Anfrage wurde abgebrochen."));
        if(target != null) {
            target.sendMessage(getFormattedMessage(player.getDisplayName() + " hat die Anfrage abgebrochen!"));
        }
    }

}
