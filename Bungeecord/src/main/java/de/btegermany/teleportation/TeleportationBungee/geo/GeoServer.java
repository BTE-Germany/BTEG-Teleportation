package de.btegermany.teleportation.TeleportationBungee.geo;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;

public class GeoServer {

    private final ServerInfo serverInfo;
    private final List<String> states;
    private final List<String> cities;
    private final boolean tpllPassthrough;
    private final boolean isEarthServer;
    private final boolean showPlayersOnTerramap;
    private final String normenWarp;

    public GeoServer(ServerInfo serverInfo, List<String> states, List<String> cities, boolean tpllPassthrough, boolean isEarthServer, boolean showPlayersOnTerramap, String normenWarp) {
        this.serverInfo = serverInfo;
        this.states = states;
        this.cities = cities;
        this.tpllPassthrough = tpllPassthrough;
        this.isEarthServer = isEarthServer;
        this.showPlayersOnTerramap = showPlayersOnTerramap;
        this.normenWarp = normenWarp;
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

    public boolean isTpllPassthrough() {
        return tpllPassthrough;
    }

    public boolean isEarthServer() {
        return isEarthServer;
    }

    public boolean shouldShowPlayersOnTerramap() {
        return showPlayersOnTerramap;
    }

    public String getNormenWarp() {
        return normenWarp;
    }

}
