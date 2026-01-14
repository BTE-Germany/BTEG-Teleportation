package de.btegermany.teleportation.TeleportationVelocity.command;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.Warp;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class EventCommand {

    public static BrigadierCommand createEventCommand(final TeleportationVelocity plugin, final RegistriesProvider registriesProvider, final PluginMessenger pluginMessenger, final ProxyServer proxyServer) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("event")
                .requires(source -> source instanceof Player)
                .executes(context -> {
                    Player player = (Player) context.getSource();

                    Warp warp = plugin.getEventWarp();
                    if (warp == null) {
                        sendMessage(player, Component.text("Gerade findet kein Event statt.", NamedTextColor.GOLD));
                        return Command.SINGLE_SUCCESS;
                    }

                    Optional<ServerConnection> serverConnectionOptional = player.getCurrentServer();
                    if (serverConnectionOptional.isEmpty()) {
                        return Command.SINGLE_SUCCESS;
                    }

                    RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, registriesProvider, () -> {
                        proxyServer.getCommandManager().executeAsync(player, warp.getTpllCommand());
                    });
                    pluginMessenger.sendMessageToServers(requestLastLocationMessage, serverConnectionOptional.get().getServer());

                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("action", StringArgumentType.word())
                        .requires(source -> source.hasPermission("bteg.warps.manage"))
                        .suggests((context, builder) -> {
                            String actionArg = context.getArguments().containsKey("action") ? StringArgumentType.getString(context, "action") : "";

                            if ("cancel".startsWith(actionArg.toLowerCase())) {
                                builder.suggest("cancel");
                            }
                            if ("set".startsWith(actionArg.toLowerCase())) {
                                builder.suggest("set");
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            Player player = (Player) context.getSource();
                            String action = StringArgumentType.getString(context, "action");

                            if (!action.equalsIgnoreCase("cancel")) {
                                sendMessage(player, Component.text("Usage: /event, /event cancel or /event set [id]", NamedTextColor.RED));
                                return Command.SINGLE_SUCCESS;
                            }

                            plugin.setEventWarp(null);
                            sendMessage(player, Component.text("Das Event ist nicht mehr aktiv.", NamedTextColor.GOLD));

                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("warpId", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                .requires(source -> source.hasPermission("bteg.warps.manage"))
                                .executes(context -> {
                                    Player player = (Player) context.getSource();
                                    String action = StringArgumentType.getString(context, "action");
                                    int warpId = IntegerArgumentType.getInteger(context, "warpId");

                                    if (!action.equalsIgnoreCase("set")) {
                                        sendMessage(player, Component.text("Usage: /event, /event cancel or /event set [id]", NamedTextColor.RED));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    Warp warp = registriesProvider.getWarpsRegistry().getWarp(warpId);
                                    if (warp == null) {
                                        sendMessage(player, Component.text("Es wurde kein Warp mit dieser Id gefunden.", NamedTextColor.RED));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    plugin.setEventWarp(warp);
                                    sendMessage(player, Component.text("Der Event Warp wurde geändert.", NamedTextColor.GOLD));

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .build());
    }

}