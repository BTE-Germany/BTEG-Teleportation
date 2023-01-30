package de.btegermany.teleportation.TeleportationBungee.geo;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;

public class GeoServer {

    ServerInfo serverInfo;
    List<String> states;
    List<String> cities;

    public GeoServer(ServerInfo serverInfo, List<String> states, List<String> cities) {
        this.serverInfo = serverInfo;
        this.states = states;
        this.cities = cities;
    }



    // Getters
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public List<String> getStates() {
        return states;
    }

    public List<String> getCities() {
        return cities;
    }
}
