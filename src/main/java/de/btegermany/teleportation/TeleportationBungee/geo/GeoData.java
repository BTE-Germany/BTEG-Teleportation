package de.btegermany.teleportation.TeleportationBungee.geo;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class GeoData {

    public static List<GeoServer> geoServers;

    public static ServerInfo getServerFromLocation(double lat, double lon) {
        GeoLocation location = getLocation(lat, lon, 10);
        if (location == null) {
            return null;
        }

        for(GeoServer server : geoServers) {
            for(String state : server.getStates()) {
                if(state.equalsIgnoreCase(location.getState())) {
                    return server.getServerInfo();
                }
            }
        }

        return null;
    }

    public static GeoLocation getLocation(double lat, double lon, int zoom) {
        try {
            URL url = new URL("https://nominatim.openstreetmap.org/reverse?lat=" + lat + "&lon=" + lon + "&format=json&zoom=" + zoom);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Accept", "application/json");

            GeoLocation location = new GeoLocation(lat, lon);
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            reader.lines().forEach(builder::append);
            JSONObject response = new JSONObject(new String(builder));
            con.disconnect();

            if(response.has("address")) {
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

        } catch (IOException e) {}

        return null;
    }

    public static void loadGeoServers() {
        geoServers = new ArrayList<>();
        ConfigurationProvider provider = YamlConfiguration.getProvider(YamlConfiguration.class);
        File dir = TeleportationBungee.getInstance().getDataFolder();
        if(!dir.getParentFile().exists()) dir.getParentFile().mkdir();
        if(!dir.exists()) dir.mkdir();
        File configFile = new File(dir, "config.yaml");

        try {
            Configuration config;
            if(!configFile.exists()) {
                configFile.createNewFile();
                config = provider.load(configFile);
                for(String server : ProxyServer.getInstance().getServers().keySet()) {
                    config.set(server, "");
                }
                provider.save(config, configFile);
            } else {
                config = provider.load(configFile);
            }

            for(String server : config.getKeys()) {
                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
                List<String> states = new ArrayList<>(config.getStringList(server));
                geoServers.add(new GeoServer(serverInfo, states));
            }

        } catch (IOException e) {
            TeleportationBungee.getInstance().getLogger().info("Config unter \"" + configFile.getPath() + "\" (und damit die Aufteilung der Bundesländer auf die Server) konnte nicht geladen werden!");
            e.printStackTrace();
        }
    }

}
