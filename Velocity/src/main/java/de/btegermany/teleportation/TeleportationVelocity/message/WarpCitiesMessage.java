package de.btegermany.teleportation.TeleportationVelocity.message;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import de.btegermany.teleportation.TeleportationVelocity.util.Warp;

import java.util.Set;
import java.util.stream.Collectors;

public class WarpCitiesMessage extends PluginMessage {

    public WarpCitiesMessage(Set<Warp> warps) {
        super("list_cities", MessageType.NORMAL);
        super.content.addAll(warps.stream().map(Warp::getCity).collect(Collectors.toSet()));
    }

}
