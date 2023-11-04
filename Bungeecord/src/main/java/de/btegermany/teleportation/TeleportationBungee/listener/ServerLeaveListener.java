package de.btegermany.teleportation.TeleportationBungee.listener;

import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerLeaveListener implements Listener {

    private final RegistriesProvider registriesProvider;

    public ServerLeaveListener(RegistriesProvider registriesProvider) {
        this.registriesProvider = registriesProvider;
    }

    @EventHandler
    public void onServerDisconnect(ServerDisconnectEvent event) {
        // unregister Bukkit Player from registry
        registriesProvider.getBukkitPlayersRegistry().unregister(event.getPlayer());
    }

    @EventHandler
    public void onServerSwitch(ServerConnectedEvent event) {
        // update Bukkit Player data (server)
        if(registriesProvider.getBukkitPlayersRegistry().isRegistered(event.getPlayer().getUniqueId())) {
            registriesProvider.getBukkitPlayersRegistry().getBukkitPlayer(event.getPlayer().getUniqueId()).setServerInfo(event.getPlayer().getServer().getInfo());
        }
    }

}
