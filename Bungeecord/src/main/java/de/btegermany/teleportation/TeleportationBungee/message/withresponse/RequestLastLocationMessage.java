package de.btegermany.teleportation.TeleportationBungee.message.withresponse;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageWithResponse;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.util.LastLocation;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.UUID;


public class RequestLastLocationMessage extends PluginMessageWithResponse {

    public RequestLastLocationMessage(ProxiedPlayer player, RegistriesProvider registriesProvider, Runnable callback) {
        super("last_location_request", dataInput -> {
            try {
                UUID playerUUID = UUID.fromString(dataInput.readUTF());
                double x = Double.parseDouble(dataInput.readUTF());
                double y = Double.parseDouble(dataInput.readUTF());
                double z = Double.parseDouble(dataInput.readUTF());
                float yaw = Float.parseFloat(dataInput.readUTF());
                float pitch = Float.parseFloat(dataInput.readUTF());
                if (player == null || !player.isConnected()) return;

                if (!playerUUID.equals(player.getUniqueId())) {
                    player.sendMessage(new TextComponent("Ein Fehler ist aufgetreten!"));
                    return;
                }

                LastLocation lastLocation = new LastLocation(x, y, z, yaw, pitch, player.getServer().getInfo());
                registriesProvider.getLastLocationsRegistry().register(playerUUID, lastLocation);

                callback.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        super.content.add(player.getUniqueId().toString());
    }

}
