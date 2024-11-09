package de.btegermany.teleportation.TeleportationVelocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationAPI.FederalState;
import de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class BlCommand {

    public static BrigadierCommand createBlCommand(final TeleportationVelocity plugin, final ProxyServer proxyServer) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("bl")
                .requires(source -> source instanceof Player)
                .executes(context -> {
                    context.getSource().sendMessage(Component.text(plugin.getPrefix() + " Du musst ein Bundesland angeben!", NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("state", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            String stateArg = context.getArguments().containsKey("state") ? context.getArgument("state", String.class) : "";

                            for (FederalState state : FederalState.values()) {
                                if (state.displayName.toLowerCase().startsWith(stateArg.toLowerCase()) || state.displayName.replace("ü", "ue").toLowerCase().startsWith(stateArg.toLowerCase())) {
                                    builder.suggest(state.displayName.replace("ü", "ue"));
                                }
                                if (state.abbreviation.toLowerCase().startsWith(stateArg.toLowerCase())) {
                                    builder.suggest(state.abbreviation);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            Player player = (Player) context.getSource();
                            String stateArg = context.getArgument("state", String.class).replace("ue", "ü");

                            FederalState state = FederalState.getStateFromInput(stateArg);
                            if (state == null) {
                                player.sendMessage(Component.text(plugin.getPrefix() + " Bundesland wurde nicht gefunden.", NamedTextColor.RED));
                                return Command.SINGLE_SUCCESS;
                            }

                            Optional<ServerConnection> serverConnectionOptional = player.getCurrentServer();
                            if (serverConnectionOptional.isEmpty()) {
                                return Command.SINGLE_SUCCESS;
                            }

                            if (serverConnectionOptional.get().getServer().getServerInfo().getName().equals(state.server)) {
                                player.sendMessage(Component.text(plugin.getPrefix() + " Du bist bereits auf dem richtigen Server!", NamedTextColor.GOLD));
                                return Command.SINGLE_SUCCESS;
                            }

                            Optional<RegisteredServer> serverOptional = proxyServer.getServer(state.server);
                            if (serverOptional.isEmpty()) {
                                player.sendMessage(Component.text(plugin.getPrefix() + " Der Server ist gerade nicht verfügbar.", NamedTextColor.RED));
                                return Command.SINGLE_SUCCESS;
                            }
                            RegisteredServer server = serverOptional.get();

                            server.ping().orTimeout(1, TimeUnit.SECONDS)
                                    .exceptionally(throwable -> {
                                        player.sendMessage(Component.text(plugin.getPrefix() + " Server " + server.getServerInfo().getName() + " is offline.", NamedTextColor.RED));
                                        return null;
                                    })
                                    .thenAccept(pingResult -> {
                                        if (pingResult == null) {
                                            return;
                                        }

                                        player.sendMessage(Component.text(plugin.getPrefix() + " Verbinde zum richtigen Server.", NamedTextColor.GOLD));

                                        player.createConnectionRequest(server).connect();
                                    });

                            return Command.SINGLE_SUCCESS;
                        })
                )

                .build());
    }
}
