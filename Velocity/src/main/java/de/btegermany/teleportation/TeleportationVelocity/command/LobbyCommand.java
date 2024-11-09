package de.btegermany.teleportation.TeleportationVelocity.command;

import com.mojang.brigadier.Command;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class LobbyCommand {

    public static BrigadierCommand createLobbyCommand(final TeleportationVelocity plugin, final ProxyServer proxyServer) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("lobby")
                .requires(source -> source instanceof Player)
                .executes(context -> {
                    Player player = (Player) context.getSource();

                    Optional<ServerConnection> serverConnectionOptional = player.getCurrentServer();
                    if (serverConnectionOptional.isEmpty()) {
                        return Command.SINGLE_SUCCESS;
                    }

                    if (serverConnectionOptional.get().getServer().getServerInfo().getName().equals("Lobby-1")) {
                        player.sendMessage(Component.text(plugin.getPrefix() + " Du bist bereits in der Lobby!", NamedTextColor.GOLD));
                        return Command.SINGLE_SUCCESS;
                    }

                    Optional<RegisteredServer> lobbyServerOptional = proxyServer.getServer("Lobby-1");
                    if (lobbyServerOptional.isEmpty()) {
                        player.sendMessage(Component.text(plugin.getPrefix() + " Die Lobby ist gerade nicht verfügbar.", NamedTextColor.RED));
                        return Command.SINGLE_SUCCESS;
                    }
                    RegisteredServer lobbyServer = lobbyServerOptional.get();

                    lobbyServer.ping().orTimeout(1, TimeUnit.SECONDS)
                            .exceptionally(throwable -> {
                                player.sendMessage(Component.text(plugin.getPrefix() + " Server " + lobbyServer.getServerInfo().getName() + " is offline.", NamedTextColor.RED));
                                return null;
                            })
                            .thenAccept(pingResult -> {
                                if (pingResult == null) {
                                    return;
                                }

                                player.sendMessage(Component.text(plugin.getPrefix() + " Verbinde zur Lobby.", NamedTextColor.GOLD));

                                player.createConnectionRequest(lobbyServer).connect();
                            });

                    return Command.SINGLE_SUCCESS;
                })
                .build());
    }
}
