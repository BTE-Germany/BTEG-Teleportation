package de.btegermany.teleportation.TeleportationVelocity.message.withresponse;

import com.velocitypowered.api.proxy.Player;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageWithResponse;

import java.util.function.Consumer;

public class RequestPlayerWorldMessage extends PluginMessageWithResponse {

    public RequestPlayerWorldMessage(Player player, Consumer<String> callback) {
        super("player_world_request", dataInput -> {
            String world = dataInput.readUTF();
            callback.accept(world);
        });

        super.content.add(player.getUniqueId().toString());
    }

}
