package de.btegermany.teleportation.TeleportationBungee.data;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoServer;
import de.btegermany.teleportation.TeleportationBungee.registry.WarpsRegistry;
import de.btegermany.teleportation.TeleportationBungee.util.Warp;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
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

    // read the servers config and add them as GeoServers to GeoData
    public void readServers(WarpsRegistry warpsRegistry) {
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
                if(serverInfo == null) {
                    this.plugin.getLogger().info("Couldn't find Server '" + serverName + "'");
                    continue;
                }
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
                String normenWarpName = config.getString(serverName + ".normen-warp");
                Warp normenWarp = normenWarpName.isEmpty() ? null : warpsRegistry.getWarps().stream().filter(warp -> warp.getName().equals(normenWarpName)).findFirst().orElse(null);

                geoServers.add(new GeoServer(serverInfo, states, cities, tpllPassthrough, isEarthServer, showPlayersOnTerramap, normenWarp));
            }
            geoData.setGeoServers(geoServers);

        } catch (IOException e) {
            plugin.getLogger().warning("Config unter \"" + configFile.getPath() + "\" konnte nicht geladen werden!");
            e.printStackTrace();
        }
    }

    // read the database config and return the data (url, user, password)
    public List<String> readDatabaseConfig() {
        List<String> data = new ArrayList<>();

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

            data.add(config.getString("database.mysql.url"));
            data.add(config.getString("database.mysql.user"));
            data.add(config.getString("database.mysql.password"));
            return data;

        } catch (IOException e) {
            plugin.getLogger().warning("Config unter \"" + configFile.getPath() + "\" konnte nicht geladen werden!");
            e.printStackTrace();
        }
        return null;
    }

    public Warp readEventWarp(WarpsRegistry warpsRegistry) {
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

            int warpId = config.getInt("eventwarp", -1);
            if (warpId == -1) {
                return null;
            }
            return warpsRegistry.getWarp(warpId);

        } catch (IOException e) {
            plugin.getLogger().warning("Config unter \"" + configFile.getPath() + "\" konnte nicht geladen werden!");
            e.printStackTrace();
        }
        return null;
    }

    public void saveEventWarp() {
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

            Warp warp = TeleportationBungee.getInstance().getEventWarp();
            config.set("eventwarp", warp != null ? warp.getId() : null);
            provider.save(config, configFile);

        } catch (IOException e) {
            plugin.getLogger().warning("Config unter \"" + configFile.getPath() + "\" konnte nicht geladen werden!");
            e.printStackTrace();
        }
    }

}
