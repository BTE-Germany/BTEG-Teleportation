package de.btegermany.teleportation.TeleportationVelocity.geo;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationAPI.State;

import java.util.List;

// a server inside the network specified by the servers config
public record GeoServer(RegisteredServer server, List<State> states, List<String> cities, boolean tpllPassthrough, boolean isEarthServer) {}