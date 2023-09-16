package de.btegermany.teleportation.TeleportationBungee.util;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static de.btegermany.teleportation.TeleportationBungee.TeleportationBungee.getFormattedMessage;

public class Utils {

    private final PluginMessenger pluginMessenger;
    private final RegistriesProvider registriesProvider;

    public Utils(PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
        this.pluginMessenger = pluginMessenger;
        this.registriesProvider = registriesProvider;
    }

    public void teleport(ProxiedPlayer p, ProxiedPlayer t) {
        p.sendMessage(TeleportationBungee.getFormattedMessage("Du wirst zu " + t.getName() + " teleportiert..."));
        pluginMessenger.teleportToPlayer(p, t);
    }

    public void cancelTpa(ProxiedPlayer player) {
        if(registriesProvider.getTpasRegistry().isRegistered(player)) {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(registriesProvider.getTpasRegistry().getTpa(player));
            registriesProvider.getTpasRegistry().unregister(player);
            player.sendMessage(getFormattedMessage("Die Anfrage wurde abgebrochen."));
            if(target != null) {
                target.sendMessage(getFormattedMessage(player.getDisplayName() + " hat die Anfrage abgebrochen!"));
            }
        } else {
            player.sendMessage(getFormattedMessage("Du hast keine Anfrage gestellt!"));
        }
    }

}
