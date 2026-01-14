package de.btegermany.teleportation.TeleportationVelocity.command;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoData;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationVelocity.message.withresponse.RequestPlayerWorldMessage;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.Utils;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;
import java.util.function.Consumer;

public class TpCommand {

    public static BrigadierCommand createTpCommand(final TeleportationVelocity plugin, final ProxyServer proxyServer, final Utils utils, final RegistriesProvider registriesProvider, final PluginMessenger pluginMessenger) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("tp")
                .requires(source -> (source instanceof Player) && (source.hasPermission("teleportation.tp.coords") || source.hasPermission("teleportation.tp.player")))
                .executes(context -> {
                    sendMessage(context.getSource(), Component.text("Bitte gib einen Spieler an.", NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("x", DoubleArgumentType.doubleArg())
                        .then(BrigadierCommand.requiredArgumentBuilder("y", DoubleArgumentType.doubleArg())
                                .then(BrigadierCommand.requiredArgumentBuilder("z", DoubleArgumentType.doubleArg())
                                        .executes(context -> {
                                            if (!context.getSource().hasPermission("teleportation.tp.coords")) {
                                                sendMessage(context.getSource(), Component.text("Du bist nicht berechtigt, diesen Command auszuführen!", NamedTextColor.RED));
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            Player player = (Player) context.getSource();
                                            double x = DoubleArgumentType.getDouble(context, "x");
                                            double y = DoubleArgumentType.getDouble(context, "y");
                                            double z = DoubleArgumentType.getDouble(context, "z");

                                            teleportToCoordinates(player, x, y, z, plugin, pluginMessenger);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
                .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            String targetName = context.getArguments().containsKey("player") ? StringArgumentType.getString(context, "player") : "";

                            proxyServer.getAllPlayers().stream()
                                    .filter(player -> player.getUsername().toLowerCase().startsWith(targetName.toLowerCase()))
                                    .forEach(player -> builder.suggest(player.getUsername()));
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            if (!context.getSource().hasPermission("teleportation.tp.player")) {
                                sendMessage(context.getSource(), Component.text("Du bist nicht berechtigt, diesen Command auszuführen!", NamedTextColor.RED));
                                return Command.SINGLE_SUCCESS;
                            }

                            Player player = (Player) context.getSource();
                            String targetName = StringArgumentType.getString(context, "player");

                            // will teleport player to target player if target player exists
                            proxyServer.getPlayer(targetName).ifPresentOrElse(target -> {
                                sendMessage(player, Component.text("Du wirst teleportiert...", NamedTextColor.GOLD));

                                RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, registriesProvider, () -> {
                                    utils.teleport(player, target);
                                });

                                Optional<ServerConnection> serverConnectionOptional = player.getCurrentServer();
                                if (serverConnectionOptional.isEmpty()) {
                                    return;
                                }
                                pluginMessenger.sendMessageToServers(requestLastLocationMessage, serverConnectionOptional.get().getServer());
                            }, () -> sendMessage(player, Component.text("Der Spieler wurde nicht gefunden.", NamedTextColor.RED)));

                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("x", DoubleArgumentType.doubleArg())
                                .then(BrigadierCommand.requiredArgumentBuilder("y", DoubleArgumentType.doubleArg())
                                        .then(BrigadierCommand.requiredArgumentBuilder("z", DoubleArgumentType.doubleArg())
                                                .executes(context -> {
                                                    if (!context.getSource().hasPermission("teleportation.tp.coords")) {
                                                        sendMessage(context.getSource(), Component.text("Du bist nicht berechtigt, diesen Command auszuführen!", NamedTextColor.RED));
                                                        return Command.SINGLE_SUCCESS;
                                                    }

                                                    Player player = (Player) context.getSource();
                                                    String targetName = StringArgumentType.getString(context, "player");
                                                    double x = DoubleArgumentType.getDouble(context, "x");
                                                    double y = DoubleArgumentType.getDouble(context, "y");
                                                    double z = DoubleArgumentType.getDouble(context, "z");

                                                    proxyServer.getPlayer(targetName).ifPresentOrElse(target -> {
                                                        if (!player.getUsername().equals(target.getUsername())) {
                                                            sendMessage(player, Component.text("Du kannst nur dich selbst zu Koordinaten teleportieren.", NamedTextColor.RED));
                                                            return;
                                                        }
                                                        teleportToCoordinates(target, x, y, z, plugin, pluginMessenger);
                                                    }, () -> sendMessage(player, Component.text("Der Spieler wurde nicht gefunden.", NamedTextColor.RED)));
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                )
                .build());
    }

    private static void teleportToCoordinates(Player player, double x, double y, double z, TeleportationVelocity plugin, PluginMessenger pluginMessenger) {
        try {
            x += x % 1 == 0 ? 0.5 : 0;
            z += z % 1 == 0 ? 0.5 : 0;

            final double finalX = x;
            final double finalZ = z;
            Consumer<RegisteredServer> teleportPlayer = server -> {
                sendMessage(player, Component.text("Du wirst zu ", NamedTextColor.GOLD),
                                    Component.text("%s %s %s ".formatted(finalX, y, finalZ), NamedTextColor.DARK_GREEN),
                                    Component.text("teleportiert", NamedTextColor.GOLD));
                pluginMessenger.teleportToCoords(player, server, finalX, y, finalZ, null, null, null);
            };

            Optional<ServerConnection> playerConnectionOptional = player.getCurrentServer();
            if (playerConnectionOptional.isEmpty()) {
                return;
            }
            RegisteredServer playerServer = playerConnectionOptional.get().getServer();

            RequestPlayerWorldMessage requestPlayerWorldMessage = new RequestPlayerWorldMessage(player, world -> {
                if (playerServer.getServerInfo().getName().equalsIgnoreCase("Lobby-1") || (playerServer.getServerInfo().getName().equalsIgnoreCase("Plot-1") && world.equals(Utils.WORLD_PLOT_LOBBY))) {
                    sendMessage(player, Component.text("Du kannst dich nicht innerhalb der Lobby teleportieren.", NamedTextColor.RED));
                    return;
                }

                if (!world.equals(Utils.WORLD_TERRA)) {
                    teleportPlayer.accept(playerServer);
                    return;
                }

                try {
                    double[] geoCoordinates = GeoData.BTE_GENERATOR_SETTINGS.projection().toGeo(finalX, finalZ);

                    Optional<RegisteredServer> serverOptional = plugin.getGeoData().getServerFromLocation(geoCoordinates[1], geoCoordinates[0]);
                    if (serverOptional.isEmpty()) {
                        sendMessage(player, Component.text("Der Server für dieses Bundesland ist nicht erreichbar.", NamedTextColor.RED));
                        return;
                    }
                    RegisteredServer server = serverOptional.get();

                    teleportPlayer.accept(server);
                } catch (OutOfProjectionBoundsException e) {
                    sendMessage(player, Component.text("Bitte überprüfe die Koordinaten.", NamedTextColor.RED));
                }
            });
            pluginMessenger.sendMessageToServers(requestPlayerWorldMessage, playerServer);
        } catch (NumberFormatException e) {
            sendMessage(player, Component.text("Bitte überprüfe die Koordinaten!", NamedTextColor.RED));
        }
    }

}