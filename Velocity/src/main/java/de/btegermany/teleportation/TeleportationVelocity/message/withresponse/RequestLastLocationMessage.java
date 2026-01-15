package de.btegermany.teleportation.TeleportationVelocity.message.withresponse;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.velocitypowered.api.proxy.Player;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageWithResponse;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.LastLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;


public class RequestLastLocationMessage extends PluginMessageWithResponse {

    public RequestLastLocationMessage(Player player, RegistriesProvider registriesProvider, Runnable callback) {
        super("last_location_request", dataInput -> {
            UUID playerUUID = UUID.fromString(dataInput.readUTF());
            double x = Double.parseDouble(dataInput.readUTF());
            double y = Double.parseDouble(dataInput.readUTF());
            double z = Double.parseDouble(dataInput.readUTF());
            Float yaw = Float.parseFloat(dataInput.readUTF());
            Float pitch = Float.parseFloat(dataInput.readUTF());
            String world = dataInput.readUTF();
            if (player == null || player.getCurrentServer().isEmpty()) return;

            if (!playerUUID.equals(player.getUniqueId())) {
                sendMessage(player, Component.text("Ein Fehler ist aufgetreten!", NamedTextColor.RED));
                return;
            }

            LastLocation lastLocation = new LastLocation(x, y, z, yaw, pitch, world, player.getCurrentServer().get().getServer());
            registriesProvider.getLastLocationsRegistry().register(playerUUID, lastLocation);

            callback.run();
        });
        super.content.add(player.getUniqueId().toString());
    }

}
