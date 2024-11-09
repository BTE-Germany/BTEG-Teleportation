package de.btegermany.teleportation.TeleportationVelocity.geo;

import com.kno10.reversegeocode.query.ReverseGeocoder;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class GeoData {

    public static final EarthGeneratorSettings BTE_GENERATOR_SETTINGS = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
    private final Logger logger;
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
        GeoLocation location = this.getLocation(lat, lon);
        if (location == null) {
            return Optional.empty();
        }

        if (!(location.getCountry().equals("Deutschland") || location.getCountry().equals("Schweiz") || location.getCountry().equals("Liechtenstein") || location.getCountry().equals("Österreich"))) {
            return Optional.empty();
        }

        if (location.getCity() != null) {
            for (GeoServer server : geoServers) {
                for (String city : server.getCities()) {
                    if (city.equalsIgnoreCase(location.getCity())) {
                        return Optional.of(server.getServer());
                    }
                }
            }
        }

        for (GeoServer server : geoServers) {
            for (String state : server.getStates()) {
                if (state.equalsIgnoreCase(location.getState())) {
                    return Optional.of(server.getServer());
                }
            }
        }

        return Optional.empty();
    }

    // returns the GeoLocation that belongs to the given coordinates
    public GeoLocation getLocation(double lat, double lon) {
        try {
            GeoLocation location = getOfflineLocation(lat, lon);
            if(location != null) {
                return location;
            }

            JSONObject response;

            if(cachedLocationResults.containsKey(lat) && cachedLocationResults.get(lat).containsKey(lon)) {
                response = cachedLocationResults.get(lat).get(lon);
            } else {
                URL url = new URL("https://nominatim.openstreetmap.org/reverse?lat=" + lat + "&lon=" + lon + "&format=json&zoom=10");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("User-Agent", "BuildTheEarthGermany-Teleportation");
                con.setRequestProperty("Accept", "application/json");

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder builder = new StringBuilder();
                reader.lines().forEach(builder::append);
                response = new JSONObject(new String(builder));
                con.disconnect();
            }

            if(response.has("address")) {
                if(!cachedLocationResults.containsKey(lat)) {
                    cachedLocationResults.put(lat, new HashMap<>());
                }
                Map<Double, JSONObject> cached2 = cachedLocationResults.get(lat);
                if(!cached2.containsKey(lon)) {
                    cached2.put(lon, null);
                }
                cached2.replace(lon, response);

                location = new GeoLocation(lat, lon);
                JSONObject address = response.getJSONObject("address");

                if(address.has("city")) {
                    location.setCity(address.getString("city"));
                }
                if(address.has("state")) {
                    location.setState(address.getString("state"));
                }
                if(address.has("country")) {
                    location.setCountry(address.getString("country"));
                }

                return location;
            }
            this.logger.error("Error nominatim: {}", response);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public GeoLocation getOfflineLocation(double lat, double lon) {

        if(this.rgc == null) {
            return null;
        }

        GeoLocation location = new GeoLocation(lat, lon);
        StringBuilder cityBuilder = new StringBuilder();
        StringBuilder stateBuilder = new StringBuilder();
        StringBuilder countryBuilder = new StringBuilder();
        for(String s : rgc.lookup((float) lon, (float) lat)) {
            if(s.endsWith("6")) {
                for(char c : s.toCharArray()) {
                    if(!String.valueOf(c).matches("[ [^\\s+]]")) {
                        break;
                    }
                    cityBuilder.append(c);
                }
            }
            if(s.endsWith("4")) {
                for(char c : s.toCharArray()) {
                    if(!String.valueOf(c).matches("[ [^\\s+]]")) {
                        break;
                    }
                    stateBuilder.append(c);
                }
            }
            if(s.endsWith("2")) {
                for(char c : s.toCharArray()) {
                    if(!String.valueOf(c).matches("[ [^\\s+]]")) {
                        break;
                    }
                    countryBuilder.append(c);
                }
            }
        }
        /*if(cityBuilder.toString().isEmpty() || stateBuilder.toString().isEmpty() || countryBuilder.toString().isEmpty()) {
            return null;
        }*/
        location.setCity(cityBuilder.toString());
        location.setState(stateBuilder.toString());
        location.setCountry(countryBuilder.toString());

        return location;
    }

    public void setGeoServers(List<GeoServer> geoServers) {
        this.geoServers = geoServers;
    }

    public List<GeoServer> getGeoServers() {
        return geoServers;
    }
}
