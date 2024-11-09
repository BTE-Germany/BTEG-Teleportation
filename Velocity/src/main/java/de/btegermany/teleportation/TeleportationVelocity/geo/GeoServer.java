package de.btegermany.teleportation.TeleportationVelocity.geo;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationVelocity.util.Warp;

import java.util.List;

public class GeoServer {

    private final RegisteredServer server;
    private final List<String> states;
    private final List<String> cities;
    private final boolean tpllPassthrough;
    private final boolean isEarthServer;
    private final boolean showPlayersOnTerramap;
    private final Warp normenWarp;

    // a server inside the network specified by the servers config
    public GeoServer(RegisteredServer server, List<String> states, List<String> cities, boolean tpllPassthrough, boolean isEarthServer, boolean showPlayersOnTerramap, Warp normenWarp) {
        this.server = server;
        this.states = states;
        this.cities = cities;
        this.tpllPassthrough = tpllPassthrough;
        this.isEarthServer = isEarthServer;
        this.showPlayersOnTerramap = showPlayersOnTerramap;
        this.normenWarp = normenWarp;
    }



    // Getters
    public RegisteredServer getServer() {
        return server;
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

    public Warp getNormenWarp() {
        return normenWarp;
    }

}
