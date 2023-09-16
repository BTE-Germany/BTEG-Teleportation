package de.btegermany.teleportation.TeleportationBukkit.message;

import de.btegermany.teleportation.TeleportationBukkit.util.WarpInCreation;

public class CreateWarpMessage extends PluginMessage {

    public CreateWarpMessage(WarpInCreation warp) {
        byteOutput.writeUTF("warp_create");
        byteOutput.writeUTF(warp.getPlayer().getUniqueId().toString());
        byteOutput.writeUTF(warp.getName());
        byteOutput.writeUTF(warp.getCity());
        byteOutput.writeUTF(warp.getState().displayName);
        byteOutput.writeUTF(String.valueOf(warp.getPlayer().getLocation().getX()));
        byteOutput.writeUTF(String.valueOf(warp.getPlayer().getLocation().getZ()));
        byteOutput.writeUTF(warp.getHeadId() != null ? warp.getHeadId() : "null");
        byteOutput.writeUTF(String.valueOf(warp.getPlayer().getLocation().getYaw()));
        byteOutput.writeUTF(String.valueOf(warp.getPlayer().getLocation().getPitch()));
        byteOutput.writeUTF(String.valueOf(warp.getPlayer().getLocation().getY()));
    }

}
