package de.btegermany.teleportation.TeleportationVelocity.geo;

import com.kno10.reversegeocode.query.ReverseGeocoder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationAPI.State;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class GeoData {

    public static final EarthGeneratorSettings BTE_GENERATOR_SETTINGS = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
    private final Logger logger;
    @Getter
    @Setter
    private List<GeoServer> geoServers;
    private final Map<Double, Map<Double, JSONObject>> cachedLocationResults;
    private ReverseGeocoder rgc;

    public GeoData(Logger logger, File dataDirectory) {
        this.logger = logger;
        this.cachedLocationResults = new HashMap<>();

        // initialize core objects for location data
        File osmLocationData = new File(dataDirectory, "osm-location-data.bin");
        if (!osmLocationData.exists()) {
            this.rgc = null;
            this.logger.warn("File osm-location-data.bin is missing! Executing /tpll in online mode!");
            return;
        }
        try {
            this.rgc = new ReverseGeocoder(osmLocationData.getAbsolutePath());
        } catch (IOException e) {
            this.rgc = null;
            this.logger.warn("File osm-location-data.bin is missing! Executing /tpll in online mode!");
        }
    }

    // returns the server the location with the given coordinates is stored on
    public Optional<RegisteredServer> getServerFromLocation(double lat, double lon) {
        return this.getServerFromLocationCheck(lat, lon, null);
    }

    // returns the server the location with the given coordinates is stored on. Meant to be used for checking coordinates, especially repeated requests in order to reduce logs for locations outside of Germany
    public Optional<RegisteredServer> getServerFromLocationCheck(double lat, double lon, Player player) {
        GeoLocation location = getLocation(lat, lon);
        if (location == null) {
            this.logger.warn("Could not determine location for coordinates: {}, {}", lat, lon);
            return Optional.empty();
        }

        if (!(location.country().equals("Deutschland") || location.country().equals("Schweiz") || location.country().equals("Liechtenstein") || location.country().equals("Österreich"))) {
            // only log in relevant cases (single tp request or on Terra) and ignore e.g. on Lobby
            if (player == null || (player.getCurrentServer().isPresent() && player.getCurrentServer().get().getServerInfo().getName().startsWith("Terra"))) {
                this.logger.warn("Location is not in Germany: {}", location.country());
            }
            return Optional.empty();
        }

        if (location.city() != null) {
            for (GeoServer server : geoServers) {
                for (String city : server.cities()) {
                    if (city.equalsIgnoreCase(location.city())) {
                        return Optional.of(server.server());
                    }
                }
            }
        }

        for (GeoServer server : geoServers) {
            for (State state : server.states()) {
                if (state.displayName.equalsIgnoreCase(location.state())) {
                    return Optional.of(server.server());
                }
            }
        }

        this.logger.warn("Could not find server for location: {}", location);
        return Optional.empty();
    }

    // returns the GeoLocation that belongs to the given coordinates
    public GeoLocation getLocation(double lat, double lon) {
        try {
            GeoLocation location = getOfflineLocation(lat, lon);
            if (location != null) {
                return location;
            }

            JSONObject response;

            if (cachedLocationResults.containsKey(lat) && cachedLocationResults.get(lat).containsKey(lon)) {
                response = cachedLocationResults.get(lat).get(lon);
            } else {
                URL url = URI.create("https://nominatim.openstreetmap.org/reverse?lat=" + lat + "&lon=" + lon + "&format=json&zoom=10").toURL();
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("User-Agent", "BuildTheEarthGermany-Teleportation");
                con.setRequestProperty("Accept", "application/json");

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder builder = new StringBuilder();
                reader.lines().forEach(builder::append);
                response = new JSONObject(new String(builder));
                con.disconnect();
            }

            if (response.has("address")) {
                if(!cachedLocationResults.containsKey(lat)) {
                    cachedLocationResults.put(lat, new HashMap<>());
                }
                Map<Double, JSONObject> cached2 = cachedLocationResults.get(lat);
                if (!cached2.containsKey(lon)) {
                    cached2.put(lon, null);
                }
                cached2.replace(lon, response);

                GeoLocation.GeoLocationBuilder locationBuilder = GeoLocation.builder()
                        .latitude(lat)
                        .longitude(lon);

                JSONObject address = response.getJSONObject("address");

                if (address.has("city")) {
                    locationBuilder.city(address.getString("city"));
                }
                if (address.has("state")) {
                    locationBuilder.state(address.getString("state"));
                }
                if (address.has("country")) {
                    locationBuilder.country(address.getString("country"));
                }

                return locationBuilder.build();
            }
            this.logger.error("Error nominatim: {}", response);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public GeoLocation getOfflineLocation(double lat, double lon) {

        if (this.rgc == null) {
            return null;
        }

        GeoLocation.GeoLocationBuilder locationBuilder = GeoLocation.builder()
                .latitude(lat)
                .longitude(lon);

        StringBuilder cityBuilder = new StringBuilder();
        StringBuilder stateBuilder = new StringBuilder();
        StringBuilder countryBuilder = new StringBuilder();
        for (String s : rgc.lookup((float) lon, (float) lat)) {
            if (s.endsWith("6")) {
                for (char c : s.toCharArray()) {
                    if (!String.valueOf(c).matches("[ [^\\s+]]")) {
                        break;
                    }
                    cityBuilder.append(c);
                }
            }
            if (s.endsWith("4")) {
                for (char c : s.toCharArray()) {
                    if (!String.valueOf(c).matches("[ [^\\s+]]")) {
                        break;
                    }
                    stateBuilder.append(c);
                }
            }
            if (s.endsWith("2")) {
                for (char c : s.toCharArray()) {
                    if (!String.valueOf(c).matches("[ [^\\s+]]")) {
                        break;
                    }
                    countryBuilder.append(c);
                }
            }
        }
        /*if(cityBuilder.toString().isEmpty() || stateBuilder.toString().isEmpty() || countryBuilder.toString().isEmpty()) {
            return null;
        }*/
        locationBuilder
                .city(cityBuilder.toString())
                .state(stateBuilder.toString())
                .country(countryBuilder.toString());

        return locationBuilder.build();
    }

}
