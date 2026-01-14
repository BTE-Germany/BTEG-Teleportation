package de.btegermany.teleportation.TeleportationVelocity.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import de.btegermany.teleportation.TeleportationVelocity.util.Warp;

import java.util.List;

public class RandomWarpMessage extends PluginMessage {

    public RandomWarpMessage(Warp warp) {
        super("random_warp", MessageType.NORMAL);
        super.content.addAll(List.of(
                String.valueOf(warp.getId()),
                warp.getName(),
                warp.getCity(),
                warp.getState(),
                String.valueOf(warp.getLatitude()),
                String.valueOf(warp.getLongitude()),
                String.valueOf(warp.getHeight()),
                String.valueOf(warp.getYaw()),
                String.valueOf(warp.getPitch()),
                String.valueOf(warp.getHeadId()),
                warp.getWorld()
        ));
    }

}
