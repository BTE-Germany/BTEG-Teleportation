package de.btegermany.teleportation.TeleportationVelocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;

public class ServerLeaveListener {

    private final RegistriesProvider registriesProvider;

    public ServerLeaveListener(RegistriesProvider registriesProvider) {
        this.registriesProvider = registriesProvider;
    }

    @Subscribe
    public void onServerDisconnect(DisconnectEvent event) {
        // unregister Bukkit Player from registry
        this.registriesProvider.getBukkitPlayersRegistry().unregister(event.getPlayer());
    }

    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        // update Bukkit Player data (server)
        if (this.registriesProvider.getBukkitPlayersRegistry().isRegistered(event.getPlayer().getUniqueId())) {
            this.registriesProvider.getBukkitPlayersRegistry().getBukkitPlayer(event.getPlayer().getUniqueId()).setServer(event.getServer());
        }
    }

}
