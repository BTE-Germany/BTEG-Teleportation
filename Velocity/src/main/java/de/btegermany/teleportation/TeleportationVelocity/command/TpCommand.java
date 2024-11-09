package de.btegermany.teleportation.TeleportationVelocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class TpCommand {

    public static BrigadierCommand createTpCommand(final TeleportationVelocity plugin, final ProxyServer proxyServer, final Utils utils, final RegistriesProvider registriesProvider, final PluginMessenger pluginMessenger) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("tp")
                .requires(source -> (source instanceof Player) && source.hasPermission("teleportation.tp"))
                .executes(context -> {
                    context.getSource().sendMessage(Component.text(plugin.getPrefix() + " Bitte gib einen Spieler an!", NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            String targetName = context.getArguments().containsKey("player") ? context.getArgument("player", String.class) : "";

                            proxyServer.getAllPlayers().stream()
                                    .filter(player -> player.getUsername().toLowerCase().startsWith(targetName.toLowerCase()))
                                    .forEach(player -> builder.suggest(player.getUsername()));
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            Player player = (Player) context.getSource();
                            String targetName = context.getArgument("player", String.class);

                            proxyServer.getPlayer(targetName).ifPresentOrElse(target -> {
                                player.sendMessage(Component.text(plugin.getPrefix() + " Du wirst teleportiert...", NamedTextColor.GOLD));

                                RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, registriesProvider, () -> {
                                    utils.teleport(player, target);
                                });

                                Optional<ServerConnection> serverConnectionOptional = player.getCurrentServer();
                                if (serverConnectionOptional.isEmpty()) {
                                    return;
                                }
                                pluginMessenger.sendMessageToServers(requestLastLocationMessage, serverConnectionOptional.get().getServer());
                            }, () -> player.sendMessage(Component.text(plugin.getPrefix() + " Der Spieler wurde nicht gefunden!", NamedTextColor.GOLD)));

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build());
    }

}
