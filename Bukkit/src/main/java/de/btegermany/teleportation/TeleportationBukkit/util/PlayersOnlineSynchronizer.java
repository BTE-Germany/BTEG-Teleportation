package de.btegermany.teleportation.TeleportationBukkit.util;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.message.BukkitPlayersMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PlayersOnlineSynchronizer {

    private final PluginMessenger pluginMessenger;
    private final Server server;
    private ScheduledExecutorService scheduledExecutorService;
    private Collection<? extends Player> lastPlayersSent;

    public PlayersOnlineSynchronizer(PluginMessenger pluginMessenger, TeleportationBukkit plugin) {
        this.pluginMessenger = pluginMessenger;
        this.server = plugin.getServer();
        this.lastPlayersSent = Collections.emptySet();
    }

    public void startProxyPlayerSynchronization() {
        if (this.scheduledExecutorService != null) {
            this.scheduledExecutorService.shutdownNow();
        }

        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorService.scheduleAtFixedRate(() -> {
            Set<Player> playersOnline = new HashSet<>(this.server.getOnlinePlayers());

            Set<Player> playersToSend = this.lastPlayersSent.stream()
                    // skip sending the first time for new players as they might be at a default location like x=0 z=0 and will soon be teleported (they probably used /tpll or similar)
                    .filter(playersOnline::contains)
                    .collect(Collectors.toSet());
            this.pluginMessenger.send(new BukkitPlayersMessage(playersToSend));

            this.lastPlayersSent = playersOnline;
        }, 0, 3, TimeUnit.SECONDS);
    }

    public void shutdownNow() {
        this.scheduledExecutorService.shutdownNow();
    }

}
