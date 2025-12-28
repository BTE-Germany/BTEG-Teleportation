package de.btegermany.teleportation.TeleportationBukkit.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import de.btegermany.teleportation.TeleportationBukkit.util.WarpInCreation;

import java.util.List;

public class CreateWarpMessage extends PluginMessage {

    public CreateWarpMessage(WarpInCreation warp) {
        super("warp_create", MessageType.NORMAL);
        super.content.addAll(List.of(
                warp.getPlayer().getUniqueId().toString(),
                warp.getName(),
                warp.getCity(),
                warp.getState().displayName,
                String.valueOf(warp.getPlayer().getLocation().getX()),
                String.valueOf(warp.getPlayer().getLocation().getZ()),
                warp.getHeadId() != null ? warp.getHeadId() : "null",
                String.valueOf(warp.getPlayer().getLocation().getYaw()),
                String.valueOf(warp.getPlayer().getLocation().getPitch()),
                String.valueOf(warp.getPlayer().getLocation().getY()),
                warp.getPlayer().getWorld().getName()));
    }

}
