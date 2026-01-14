package de.btegermany.teleportation.TeleportationVelocity.command;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class TpHereCommand {

    public static BrigadierCommand createTpHereCommand(final ProxyServer proxyServer, final Utils utils, final RegistriesProvider registriesProvider, final PluginMessenger pluginMessenger) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("TpHere")
                .requires(source -> (source instanceof Player) && source.hasPermission("teleportation.tphere"))
                .executes(context -> {
                    sendMessage(context.getSource(), Component.text("Bitte gib einen Spieler an!", NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            String targetName = context.getArguments().containsKey("player") ? StringArgumentType.getString(context, "player") : "";

                            proxyServer.getAllPlayers().stream()
                                    .filter(player -> player.getUsername().toLowerCase().startsWith(targetName.toLowerCase()))
                                    .forEach(player -> builder.suggest(player.getUsername()));
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            Player player = (Player) context.getSource();
                            String targetName = StringArgumentType.getString(context, "player");

                            proxyServer.getPlayer(targetName).ifPresentOrElse(target -> {
                                sendMessage(player, Component.text("%s wird zu dir teleportiert...".formatted(target.getUsername()), NamedTextColor.GOLD));

                                RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(target, registriesProvider, () -> {
                                    utils.teleport(target, player);
                                });

                                Optional<ServerConnection> serverConnectionOptional = target.getCurrentServer();
                                if (serverConnectionOptional.isEmpty()) {
                                    return;
                                }
                                pluginMessenger.sendMessageToServers(requestLastLocationMessage, serverConnectionOptional.get().getServer());
                            }, () -> sendMessage(player, Component.text("Der Spieler wurde nicht gefunden!", NamedTextColor.GOLD)));

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build());
    }

}
