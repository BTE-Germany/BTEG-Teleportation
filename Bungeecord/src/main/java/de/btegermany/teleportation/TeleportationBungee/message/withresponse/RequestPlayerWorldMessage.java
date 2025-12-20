package de.btegermany.teleportation.TeleportationBungee.message.withresponse;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageWithResponse;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.function.Consumer;

public class RequestPlayerWorldMessage extends PluginMessageWithResponse {

    public RequestPlayerWorldMessage(ProxiedPlayer player, Consumer<String> callback) {
        super("player_world_request", dataInput -> {
            try {
                String world = dataInput.readUTF();
                callback.accept(world);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        super.content.add(player.getUniqueId().toString());
    }

}
