package de.btegermany.teleportation.TeleportationBungee.geo;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;

public class GeoServer {

    ServerInfo serverInfo;
    List<String> states;

    public GeoServer(ServerInfo serverInfo, List<String> states) {
        this.serverInfo = serverInfo;
        this.states = states;
    }



    // Getters
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public List<String> getStates() {
        return states;
    }
}
