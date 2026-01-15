package de.btegermany.teleportation.TeleportationVelocity.command;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.mojang.brigadier.Command;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationVelocity.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;


public class HubCommand {

    public static BrigadierCommand createHubCommand(final ProxyServer proxyServer) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("lobby")
                .requires(source -> source instanceof Player)
                .executes(context -> {
                    Player player = (Player) context.getSource();

                    Optional<ServerConnection> serverConnectionOptional = player.getCurrentServer();
                    if (serverConnectionOptional.isEmpty()) {
                        return Command.SINGLE_SUCCESS;
                    }

                    if (serverConnectionOptional.get().getServer().getServerInfo().getName().equals("Lobby-1")) {
                        sendMessage(player, Component.text("Du bist bereits in der Lobby!", NamedTextColor.GOLD));
                        return Command.SINGLE_SUCCESS;
                    }

                    Optional<RegisteredServer> lobbyServerOptional = proxyServer.getServer("Lobby-1");
                    if (lobbyServerOptional.isEmpty()) {
                        sendMessage(player, Component.text("Die Lobby ist gerade nicht verfügbar.", NamedTextColor.RED));
                        return Command.SINGLE_SUCCESS;
                    }
                    RegisteredServer lobbyServer = lobbyServerOptional.get();

                    Utils.connectIfOnline(player, lobbyServer, "Verbinde zur Lobby.");

                    return Command.SINGLE_SUCCESS;
                })
                .build());
    }

}