package de.btegermany.teleportation.TeleportationVelocity.message.withresponse;

import com.velocitypowered.api.proxy.Player;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageWithResponse;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.LastLocation;

import java.util.UUID;


public class RequestLastLocationMessage extends PluginMessageWithResponse {

    public RequestLastLocationMessage(Player player, RegistriesProvider registriesProvider, Runnable callback) {
        super("last_location_request", dataInput -> {
            UUID playerUUID = UUID.fromString(dataInput.readUTF());
            double x = Double.parseDouble(dataInput.readUTF());
            double y = Double.parseDouble(dataInput.readUTF());
            double z = Double.parseDouble(dataInput.readUTF());
            float yaw = Float.parseFloat(dataInput.readUTF());
            float pitch = Float.parseFloat(dataInput.readUTF());
            if (player == null || player.getCurrentServer().isEmpty()) return;

            LastLocation lastLocation = new LastLocation(x, y, z, yaw, pitch, player.getCurrentServer().get().getServer());
            registriesProvider.getLastLocationsRegistry().register(playerUUID, lastLocation);

            callback.run();
        });
        super.content.add(player.getUniqueId().toString());
    }

}
