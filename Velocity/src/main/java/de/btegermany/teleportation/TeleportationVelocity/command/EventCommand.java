package de.btegermany.teleportation.TeleportationVelocity.command;

import com.mojang.brigadier.Command;
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

    public static BrigadierCommand createEventCommand(final TeleportationVelocity plugin, final ProxyServer proxyServer, final RegistriesProvider registriesProvider, final PluginMessenger pluginMessenger) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("event")
                .requires(source -> source instanceof Player)
                .executes(context -> {
                    Player player = (Player) context.getSource();

                    Optional<Warp> warp = registriesProvider.getWarpsRegistry().getWarps().stream().filter(warp1 -> warp1.getName().equalsIgnoreCase("event")).findFirst();
                    if (warp.isEmpty()) {
                        player.sendMessage(Component.text(plugin.getPrefix() + " Gerade findet kein Event statt.", NamedTextColor.GOLD));
                        return Command.SINGLE_SUCCESS;
                    }

                    Optional<ServerConnection> serverConnectionOptional = player.getCurrentServer();
                    if (serverConnectionOptional.isEmpty()) {
                        return Command.SINGLE_SUCCESS;
                    }

                    RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, registriesProvider, () -> {
                        pluginMessenger.performCommand(player, "nwarp event");
                    });
                    pluginMessenger.sendMessageToServers(requestLastLocationMessage, serverConnectionOptional.get().getServer());

                    return Command.SINGLE_SUCCESS;
                })
                .build());
    }

}
