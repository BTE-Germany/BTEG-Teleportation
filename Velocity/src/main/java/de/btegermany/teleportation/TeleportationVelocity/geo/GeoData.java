package de.btegermany.teleportation.TeleportationVelocity.geo;

import com.alpsbte.alpslib.geo.AdminLevel;
import com.alpsbte.alpslib.geo.GeoLocation;
import com.alpsbte.alpslib.geo.nominatim.NominatimHandler;
import com.alpsbte.alpslib.geo.rgc.RgcGeoLocation;
import com.alpsbte.alpslib.geo.rgc.RgcHandler;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationAPI.State;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


public class GeoData {

    public static final EarthGeneratorSettings BTE_GENERATOR_SETTINGS = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
    private static final String NOMINATIM_USER_AGENT = "BuildTheEarthGermany-Teleportation";

    private final Logger logger;
    @Getter
    @Setter
    private List<GeoServer> geoServers;
    private final RgcHandler rgcHandler;
    private final NominatimHandler nominatimHandler;

    public GeoData(Logger logger, File dataDirectory) {
        this.logger = logger;

        // initialize core objects for location data
        this.nominatimHandler = new NominatimHandler(logger, NOMINATIM_USER_AGENT, "de");
        File osmLocationData = new File(dataDirectory, "osm-location-data.bin");
        if (!osmLocationData.exists()) {
            this.rgcHandler = null;
            this.logger.warn("File osm-location-data.bin is missing! Executing /tpll in online mode!");
            return;
        }
        this.rgcHandler = new RgcHandler(osmLocationData, logger, true);
    }

    // returns the server the location with the given coordinates is stored on
    public CompletableFuture<Optional<RegisteredServer>> getServerFromLocation(double lat, double lon) {
        return this.getServerFromLocationCheck(lat, lon, null);
    }

    // returns the server the location with the given coordinates is stored on. Meant to be used for checking coordinates, especially repeated requests in order to reduce logs for locations outside of Germany
    public CompletableFuture<Optional<RegisteredServer>> getServerFromLocationCheck(double lat, double lon, Player player) {
        return CompletableFuture.supplyAsync(() -> {
            GeoLocation location = getLocation(lat, lon).join();
            if (location == null) {
                this.logger.warn("Could not determine location for coordinates: {}, {}", lat, lon);
                return Optional.empty();
            }

            String country = location.get(AdminLevel.COUNTRY);
            if (!(country.equals("Deutschland") || country.equals("Schweiz") || country.equals("Liechtenstein") || country.equals("Österreich"))) {
                // only log in relevant cases (single tp request or on Terra) and ignore e.g. on Lobby
                if (player == null || (player.getCurrentServer().isPresent() && player.getCurrentServer().get().getServerInfo().getName().startsWith("Terra"))) {
                    this.logger.warn("Location is not in Germany: {}", country);
                }
                return Optional.empty();
            }

            String city = location.get(DEAdminLevel.CITY);
            if (city != null) {
                for (GeoServer server : geoServers) {
                    for (String serverCity : server.cities()) {
                        if (serverCity.equalsIgnoreCase(city)) {
                            return Optional.of(server.server());
                        }
                    }
                }
            }

            String state = location.get(DEAdminLevel.STATE);
            for (GeoServer server : geoServers) {
                for (State serverState : server.states()) {
                    if (serverState.displayName.equalsIgnoreCase(state)) {
                        return Optional.of(server.server());
                    }
                }
            }

            this.logger.warn("Could not find server for location: {}", location);
            return Optional.empty();
        });
    }

    // returns the GeoLocation that belongs to the given coordinates
    public CompletableFuture<GeoLocation> getLocation(double lat, double lon) {
        return CompletableFuture.supplyAsync(() -> {
            RgcGeoLocation location = this.getOfflineLocation(lat, lon).join();
            if (location != null) {
                return location;
            }

            // error during offline rgc, use nominatim instead
            return this.nominatimHandler.locationFromCoordinates(lat, lon).join();
        });
    }

    public CompletableFuture<RgcGeoLocation> getOfflineLocation(double lat, double lon) {
        if (this.rgcHandler == null) {
            return null;
        }

        return this.rgcHandler.locationFromCoordinates(lat, lon);
    }

}
