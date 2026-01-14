package de.btegermany.teleportation.TeleportationVelocity.command;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.mojang.brigadier.Command;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationVelocity.data.ConfigHandler;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class NormsCommand {

    public static BrigadierCommand createNormsCommand(final ProxyServer proxyServer, final ConfigHandler configHandler, final RegistriesProvider registriesProvider, final PluginMessenger pluginMessenger) {
        String[] normenServerAndWorld = configHandler.readNormenServerAndWorld();
        String normenServerName = normenServerAndWorld[0];
        String normenWorld = normenServerAndWorld[1];

        float[] normenYawAndPitch = configHandler.readNormenYawAndPitch();
        float normenYaw = normenYawAndPitch[0];
        float normenPitch = normenYawAndPitch[1];

        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("norms")
                .requires(source -> source instanceof Player)
                .executes(context -> {
                    Player player = (Player) context.getSource();

                    if (normenServerName == null || normenServerName.isEmpty()) {
                        sendMessage(player, Component.text("Aktuell ist kein Server für Normen definiert.", NamedTextColor.GOLD));
                        return Command.SINGLE_SUCCESS;
                    }

                    if (normenWorld == null || normenWorld.isEmpty()) {
                        sendMessage(player, Component.text("Aktuell ist keine Welt für Normen definiert.", NamedTextColor.GOLD));
                        return Command.SINGLE_SUCCESS;
                    }

                    sendMessage(player, Component.text("Du wirst zu den Normen teleportiert.", NamedTextColor.GOLD));
                    Optional<RegisteredServer> serverOptional = proxyServer.getServer(normenServerName);
                    if (serverOptional.isEmpty()) {
                        sendMessage(player, Component.text("Der Server ist gerade nicht verfügbar.", NamedTextColor.RED));
                        return Command.SINGLE_SUCCESS;
                    }
                    RegisteredServer normenServer = serverOptional.get();

                    RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, registriesProvider, () -> {
                        pluginMessenger.teleportToNormen(player, normenServer, normenWorld, normenYaw, normenPitch);
                    });
                    if (player.getCurrentServer().isEmpty()) {
                        return Command.SINGLE_SUCCESS;
                    }
                    pluginMessenger.sendMessageToServers(requestLastLocationMessage, player.getCurrentServer().get().getServer());

                    return Command.SINGLE_SUCCESS;
                })
                .build()
        );
    }

}
