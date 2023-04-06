package de.btegermany.teleportation.TeleportationBungee.geo;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class GeoData {

    private final TeleportationBungee plugin;
    private List<GeoServer> geoServers;

    public GeoData(TeleportationBungee plugin) {
        this.plugin = plugin;
    }

    public ServerInfo getServerFromLocation(double lat, double lon) {
        GeoLocation location = getLocation(lat, lon, 10);
        if (location == null) {
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
    }

    public GeoLocation getLocation(double lat, double lon, int zoom) {
        try {
            URL url = new URL("https://nominatim.openstreetmap.org/reverse?lat=" + lat + "&lon=" + lon + "&format=json&zoom=" + zoom);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "BuildTheEarthGermany-Teleportation");
            con.setRequestProperty("Accept", "application/json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            reader.lines().forEach(builder::append);
            JSONObject response = new JSONObject(new String(builder));
            con.disconnect();

            if(response.has("address")) {
                GeoLocation location = new GeoLocation(lat, lon);
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

        } catch (IOException e) {
            return null;
        }

        return null;
    }

    public void loadGeoServers() {
        geoServers = new ArrayList<>();
        ConfigurationProvider provider = YamlConfiguration.getProvider(YamlConfiguration.class);
        File dir = plugin.getDataFolder();
        if(!dir.getParentFile().exists()) dir.getParentFile().mkdir();
        if(!dir.exists()) dir.mkdir();
        File configFile = new File(dir, "config.yaml");

        try {
            if(!configFile.exists()) {
                try (InputStream inputStream = plugin.getResourceAsStream("config.yaml")) {
                    FileUtils.copyInputStreamToFile(inputStream, configFile);
                }
            }
            Configuration config = provider.load(configFile);

            for(String server : config.getKeys()) {
                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
                List<String> states = new ArrayList<>(config.getStringList(server + ".bundesländer"));
                List<String> cities = new ArrayList<>(config.getStringList(server + ".städte"));
                geoServers.add(new GeoServer(serverInfo, states, cities));
            }

        } catch (IOException e) {
            plugin.getLogger().info("Config unter \"" + configFile.getPath() + "\" (und damit die Aufteilung der Bundesländer auf die Server) konnte nicht geladen werden!");
            e.printStackTrace();
        }
    }

    public List<GeoServer> getGeoServers() {
        return geoServers;
    }
}
