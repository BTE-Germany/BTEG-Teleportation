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
import de.btegermany.teleportation.TeleportationVelocity.registry.TpasRegistry;
import de.btegermany.teleportation.TeleportationVelocity.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TpacceptCommand {

    public static BrigadierCommand createTpacceptCommand(final ProxyServer proxyServer, final Utils utils, final RegistriesProvider registriesProvider, final PluginMessenger pluginMessenger) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("Tpaccept")
                .requires(source -> (source instanceof Player) && source.hasPermission("teleportation.tpa"))
                .executes(context -> {
                    Player player = (Player) context.getSource();
                    TpasRegistry tpasRegistry = registriesProvider.getTpasRegistry();
                    // get amount of tpas the player received
                    long requests = tpasRegistry.getTpas().values().stream().filter(uuid -> uuid.equals(player.getUniqueId())).count();

                    if (requests == 0) {
                        sendMessage(player, Component.text("Du hast keine Anfragen erhalten.", NamedTextColor.GOLD));
                    } else if (requests == 1) {
                        // will accept the only tpa the player received
                        for(Map.Entry<UUID, UUID> entry : tpasRegistry.getTpas().entrySet()) {
                            if(!entry.getValue().equals(player.getUniqueId())) {
                                continue;
                            }

                            proxyServer.getPlayer(entry.getKey()).ifPresentOrElse(target -> {
                                sendMessage(target, Component.text("Deine Anfrage wurde angenommen!", NamedTextColor.GOLD));

                                RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(target, registriesProvider, () -> {
                                    utils.teleport(target, player);
                                    tpasRegistry.unregister(target);
                                });

                                Optional<ServerConnection> serverConnectionOptional = target.getCurrentServer();
                                if (serverConnectionOptional.isEmpty()) {
                                    return;
                                }
                                pluginMessenger.sendMessageToServers(requestLastLocationMessage, serverConnectionOptional.get().getServer());
                            }, () -> sendMessage(player, Component.text("Der Spieler ist nicht mehr online.", NamedTextColor.GOLD)));
                            break;
                        }
                    } else {
                        sendMessage(player, Component.text("Bitte gib den Spieler an, der die Anfrage gesendet hat!", NamedTextColor.RED));
                    }

                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            String targetName = context.getArguments().containsKey("player") ? StringArgumentType.getString(context, "player") : "";

                            Player player = (Player) context.getSource();
                            registriesProvider.getTpasRegistry().getTpas().entrySet().stream()
                                    .filter(entry -> player.getUniqueId().equals(entry.getValue()))
                                    .forEach(entry -> {
                                        proxyServer.getPlayer(entry.getKey()).ifPresent(target -> {
                                            if (!target.getUsername().toLowerCase().startsWith(targetName.toLowerCase())) {
                                                return;
                                            }
                                            builder.suggest(target.getUsername());
                                        });
                                    });
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            Player player = (Player) context.getSource();
                            String targetName = StringArgumentType.getString(context, "player");
                            TpasRegistry tpasRegistry = registriesProvider.getTpasRegistry();

                            // will accept the tpa the target player sent
                            proxyServer.getPlayer(targetName).ifPresentOrElse(target -> {
                                if(tpasRegistry.isRegistered(target) && tpasRegistry.getTpa(target).equals(player.getUniqueId())) {
                                    sendMessage(target, Component.text("Deine Anfrage wurde angenommen!", NamedTextColor.GOLD));

                                    RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(target, registriesProvider, () -> {
                                        utils.teleport(target, player);
                                        tpasRegistry.unregister(target);
                                    });

                                    Optional<ServerConnection> serverConnectionOptional = target.getCurrentServer();
                                    if (serverConnectionOptional.isEmpty()) {
                                        return;
                                    }
                                    pluginMessenger.sendMessageToServers(requestLastLocationMessage, serverConnectionOptional.get().getServer());
                                } else {
                                    sendMessage(player, Component.text("Du hast keine Anfrage von diesem Spieler erhalten!", NamedTextColor.GOLD));
                                }
                            }, () -> sendMessage(player, Component.text("Der Spieler wurde nicht gefunden!", NamedTextColor.GOLD)));

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build());
    }

}