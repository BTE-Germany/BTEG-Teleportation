package de.btegermany.teleportation.TeleportationVelocity.command;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.mojang.brigadier.Command;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.LastLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TpBackCommand {

    public static BrigadierCommand createTpBackCommand(final RegistriesProvider registriesProvider, final PluginMessenger pluginMessenger) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("TpBack")
                .requires(source -> (source instanceof Player) && source.hasPermission("teleportation.tpback"))
                .executes(context -> {
                    Player player = (Player) context.getSource();

                    // check if there is a location to teleport back to
                    if (!registriesProvider.getLastLocationsRegistry().isRegistered(player)) {
                        sendMessage(player, Component.text("Wenn du dich zuvor teleportiert hast, warte bitte einen Moment (wenige Sekunden) und führe dann den Command erneut aus.", NamedTextColor.RED));
                        return Command.SINGLE_SUCCESS;
                    }
                    LastLocation lastLocation = registriesProvider.getLastLocationsRegistry().getLastLocation(player);
                    String[] coordinatesSplit = lastLocation.getCoordinates().split(",");
                    double x = Double.parseDouble(coordinatesSplit[0]);
                    double y = Double.parseDouble(coordinatesSplit[1]);
                    double z = Double.parseDouble(coordinatesSplit[2]);
                    pluginMessenger.teleportToCoords(player, lastLocation.server(), x, y, z, lastLocation.yaw(), lastLocation.pitch(), lastLocation.world());

                    return Command.SINGLE_SUCCESS;
                })
                .build());
    }

}