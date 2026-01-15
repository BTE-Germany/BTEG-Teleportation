package de.btegermany.teleportation.TeleportationVelocity.command;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationAPI.State;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoData;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoServer;
import de.btegermany.teleportation.TeleportationVelocity.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class BlCommand {

    public static BrigadierCommand createBlCommand(final GeoData geoData) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("bl")
                .requires(source -> source instanceof Player)
                .executes(context -> {
                    sendMessage(context.getSource(), Component.text("Du musst ein Bundesland angeben!", NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("state", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            String stateArg = context.getArguments().containsKey("state") ? StringArgumentType.getString(context, "state") : "";

                            for (State state : State.values()) {
                                if (state.displayName.toLowerCase().startsWith(stateArg.toLowerCase()) || state.displayName.replace("ü", "ue").toLowerCase().startsWith(stateArg.toLowerCase())) {
                                    // replace ü because of error
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
                            String stateArg = StringArgumentType.getString(context, "state").replace("ue", "ü");

                            State state = State.getStateFromInput(stateArg);
                            if (state == null) {
                                sendMessage(player, Component.text("Bundesland wurde nicht gefunden.", NamedTextColor.RED));
                                return Command.SINGLE_SUCCESS;
                            }

                            Optional<ServerConnection> serverConnectionOptional = player.getCurrentServer();
                            if (serverConnectionOptional.isEmpty()) {
                                return Command.SINGLE_SUCCESS;
                            }
                            RegisteredServer currentServer = serverConnectionOptional.get().getServer();

                            Optional<GeoServer> stateGeoServerOptional = geoData.getGeoServers().stream().filter(geoServer -> geoServer.states().contains(state)).findFirst();
                            if (stateGeoServerOptional.isEmpty()) {
                                sendMessage(player, Component.text("Dieses Bundesland wurde noch nicht konfiguriert.", NamedTextColor.RED));
                                return Command.SINGLE_SUCCESS;
                            }
                            RegisteredServer stateServer = stateGeoServerOptional.get().server();
                            if (currentServer.equals(stateGeoServerOptional.get().server())) {
                                sendMessage(player, Component.text("Du bist bereits auf dem richtigen Server.", NamedTextColor.GOLD));
                                return Command.SINGLE_SUCCESS;
                            }

                            Utils.connectIfOnline(player, stateServer, "Verbinde zum richtigen Server.");

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build());
    }

}
