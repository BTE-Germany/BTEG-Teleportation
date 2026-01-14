package de.btegermany.teleportation.TeleportationVelocity.command;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.registry.TpasRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;

public class TpaDenyCommand {

    public static BrigadierCommand createTpaDenyCommand(final ProxyServer proxyServer, final RegistriesProvider registriesProvider) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("TpaDeny")
                .requires(source -> (source instanceof Player) && source.hasPermission("teleportation.tpa"))
                .executes(context -> {
                    Player player = (Player) context.getSource();
                    TpasRegistry tpasRegistry = registriesProvider.getTpasRegistry();
                    // get amount of tpas the player received
                    long requests = tpasRegistry.getTpas().values().stream().filter(uuid -> uuid.equals(player.getUniqueId())).count();

                    if (requests == 0) {
                        sendMessage(player, Component.text("Du hast keine Anfragen erhalten.", NamedTextColor.GOLD));
                    } else if (requests == 1) {
                        // will deny the only tpa the player received
                        for(Map.Entry<UUID, UUID> entry : tpasRegistry.getTpas().entrySet()) {
                            if(!entry.getValue().equals(player.getUniqueId())) {
                                continue;
                            }

                            proxyServer.getPlayer(entry.getKey()).ifPresentOrElse(target -> {
                                sendMessage(target, Component.text("Deine Anfrage wurde abgelehnt!", NamedTextColor.GOLD));
                                sendMessage(player, Component.text("Du hast die Anfrage von " + target.getUsername() + " abgelehnt.", NamedTextColor.GOLD));
                                tpasRegistry.unregister(target);
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

                            // will deny the tpa the target player sent
                            proxyServer.getPlayer(targetName).ifPresentOrElse(target -> {
                                if(tpasRegistry.isRegistered(target) && tpasRegistry.getTpa(target).equals(player.getUniqueId())) {
                                    sendMessage(target, Component.text("Deine Anfrage wurde abgelehnt!", NamedTextColor.GOLD));
                                    sendMessage(player, Component.text("Du hast die Anfrage von %s abgelehnt.".formatted(target.getUsername()), NamedTextColor.GOLD));
                                    tpasRegistry.unregister(target);
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