package de.btegermany.teleportation.TeleportationVelocity.command;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class TpaCommand {

    public static BrigadierCommand createTpaCommand(final ProxyServer proxyServer, final Utils utils, final RegistriesProvider registriesProvider) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("tpa")
                .requires(source -> (source instanceof Player) && source.hasPermission("teleportation.tpa"))
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
                                // if the player sent another tpa it gets cancelled
                                if (registriesProvider.getTpasRegistry().isRegistered(player)) {
                                    utils.cancelTpa(player);
                                }

                                // store tpa
                                registriesProvider.getTpasRegistry().register(player, target);

                                TextComponent textComponent = Component.text("Du hast eine Teleport-Anfrage von " + player.getUsername() + " erhalten. Nutze ", NamedTextColor.GOLD)
                                        .append(Component.text("/tpaccept " + player.getUsername(), NamedTextColor.GREEN).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + player.getUsername())))
                                        .append(Component.text(" zum Akzeptieren und ", NamedTextColor.GOLD))
                                        .append(Component.text("/tpadeny " + player.getUsername(), NamedTextColor.RED).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + player.getUsername())))
                                        .append(Component.text(" zum Ablehnen der Anfrage."));
                                sendMessage(target, textComponent);
                                sendMessage(player, Component.text("Die Anfrage wurde gesendet! Um sie abzubrechen, nutze /tpacancel.", NamedTextColor.GOLD));
                            }, () -> sendMessage(player, Component.text("Der Spieler wurde nicht gefunden!", NamedTextColor.GOLD)));

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build());
    }

}
