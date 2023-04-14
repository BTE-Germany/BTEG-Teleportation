package de.btegermany.teleportation.TeleportationBungee.util;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ConfigReader {

    private final TeleportationBungee plugin;
    private final GeoData geoData;

    public ConfigReader(TeleportationBungee plugin, GeoData geoData) {
        this.plugin = plugin;
        this.geoData = geoData;
    }

    public void readServers() {
        ConfigurationProvider provider = YamlConfiguration.getProvider(YamlConfiguration.class);
        File dir = plugin.getDataFolder();
        if(!dir.getParentFile().exists()) dir.getParentFile().mkdir();
        if(!dir.exists()) dir.mkdir();
        File configFile = new File(dir, "server.yaml");

        try {
            if(!configFile.exists()) {
                try (InputStream inputStream = plugin.getResourceAsStream(configFile.getName())) {
                    FileUtils.copyInputStreamToFile(inputStream, configFile);
                }
            }
            Configuration config = provider.load(configFile);

            List<GeoServer> geoServers = new ArrayList<>();
            for(String serverName : config.getKeys()) {
                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(serverName);
                List<String> states = new ArrayList<>(config.getStringList(serverName + ".bundesländer"));
                List<String> cities = new ArrayList<>(config.getStringList(serverName + ".städte"));
                boolean tpllPassthrough = false;
                Object tpllPassthroughRaw = config.get(serverName + ".tpll-passthrough");
                if(tpllPassthroughRaw != null) {
                    tpllPassthrough = (boolean) tpllPassthroughRaw;
                }
                boolean isEarthServer = false;
                Object isEarthServerRaw = config.get(serverName + ".isEarthServer");
                if(isEarthServerRaw != null) {
                    isEarthServer = (boolean) isEarthServerRaw;
                }
                boolean showPlayersOnTerramap = true;
                Object showPlayersOnTerramapRaw = config.get(serverName + ".showPlayersOnTerramap");
                if(showPlayersOnTerramapRaw != null) {
                    showPlayersOnTerramap = (boolean) showPlayersOnTerramapRaw;
                }

                geoServers.add(new GeoServer(serverInfo, states, cities, tpllPassthrough, isEarthServer, showPlayersOnTerramap));
            }
            geoData.setGeoServers(geoServers);

        } catch (IOException e) {
            plugin.getLogger().warning("Config unter \"" + configFile.getPath() + "\" konnte nicht geladen werden!");
            e.printStackTrace();
        }
    }

    public String readDatabasePath() {
        ConfigurationProvider provider = YamlConfiguration.getProvider(YamlConfiguration.class);
        File dir = plugin.getDataFolder();
        if(!dir.getParentFile().exists()) dir.getParentFile().mkdir();
        if(!dir.exists()) dir.mkdir();
        File configFile = new File(dir, "config.yaml");

        try {
            if(!configFile.exists()) {
                try (InputStream inputStream = plugin.getResourceAsStream(configFile.getName())) {
                    FileUtils.copyInputStreamToFile(inputStream, configFile);
                }
            }
            Configuration config = provider.load(configFile);

            String dbPath = config.getString("sqlite-database-path");
            if(dbPath == null) {
                plugin.getLogger().warning("No database path provided!");
            }
            return dbPath;

        } catch (IOException e) {
            plugin.getLogger().warning("Config unter \"" + configFile.getPath() + "\" konnte nicht geladen werden!");
            e.printStackTrace();
        }
        return null;
    }

}
