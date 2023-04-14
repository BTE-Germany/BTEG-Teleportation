package de.btegermany.teleportation.TeleportationBungee.geo;

import com.kno10.reversegeocode.query.ReverseGeocoder;
import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public class GeoData {

    private final TeleportationBungee plugin;
    private List<GeoServer> geoServers;
    private final Map<Double, Map<Double, JSONObject>> cachedLocationResults;

    public GeoData(TeleportationBungee plugin) {
        this.plugin = plugin;
        this.cachedLocationResults = new HashMap<>();
    }

    public CompletableFuture<ServerInfo> getServerFromLocation(double lat, double lon) {
        return CompletableFuture.supplyAsync(() -> {
            GeoLocation location = getLocation(lat, lon);
            if (location == null) {
                return null;
            }

            if(!location.getCountry().equals("Deutschland")) {
                return null;
            }

            if (location.getCity() != null) {
                for (GeoServer server : geoServers) {
                    for (String city : server.getCities()) {
                        if (city.equalsIgnoreCase(location.getCity())) {
                            return server.getServerInfo();
                        }
                    }
                }
            }

            for (GeoServer server : geoServers) {
                for (String state : server.getStates()) {
                    if (state.equalsIgnoreCase(location.getState())) {
                        return server.getServerInfo();
                    }
                }
            }

            return null;
        });
    }

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
            plugin.getLogger().warning("Error nominatim: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public GeoLocation getOfflineLocation(double lat, double lon) {
        File osmLocationData = new File(plugin.getDataFolder(), "osm-location-data.bin");
        if(!osmLocationData.exists()) {
            plugin.getLogger().warning("File osm-location-data.bin is missing! Executing /tpll in online mode!");
            return null;
        }
        try (ReverseGeocoder rgc = new ReverseGeocoder(osmLocationData.getAbsolutePath())) {
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
            if(cityBuilder.toString().isEmpty() || stateBuilder.toString().isEmpty() || countryBuilder.toString().isEmpty()) {
                return null;
            }
            location.setCity(cityBuilder.toString());
            location.setState(stateBuilder.toString());
            location.setCountry(countryBuilder.toString());

            return location;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setGeoServers(List<GeoServer> geoServers) {
        this.geoServers = geoServers;
    }

    public List<GeoServer> getGeoServers() {
        return geoServers;
    }
}
