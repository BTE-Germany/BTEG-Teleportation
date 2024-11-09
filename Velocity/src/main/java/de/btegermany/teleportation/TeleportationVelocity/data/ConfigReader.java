package de.btegermany.teleportation.TeleportationVelocity.data;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoData;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoServer;
import de.btegermany.teleportation.TeleportationVelocity.registry.WarpsRegistry;
import de.btegermany.teleportation.TeleportationVelocity.util.Warp;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigReader {

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final File dataDirectory;
    private final GeoData geoData;
    private CommentedConfigurationNode serverRoot;
    private CommentedConfigurationNode configRoot;

    public ConfigReader(ProxyServer proxyServer, Logger logger, File dataDirectory, GeoData geoData) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.geoData = geoData;

        final HoconConfigurationLoader serverLoader = HoconConfigurationLoader.builder()
                .path(new File(this.dataDirectory, "server.conf").toPath())
                .build();
        try {
            this.serverRoot = serverLoader.load();
        } catch (IOException e) {
            this.serverRoot = null;
            this.logger.error("An error occurred while loading this configuration: {}", e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
        }

        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(new File(this.dataDirectory, "config.conf").toPath())
                .build();
        try {
            this.configRoot = loader.load();
        } catch (IOException e) {
            this.configRoot = null;
            this.logger.error("An error occurred while loading this configuration: {}", e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
            System.exit(1);
        }
    }

    // read the servers config and add them as GeoServers to GeoData
    public void readServers(WarpsRegistry warpsRegistry) {
        if (this.serverRoot == null) {
            return;
        }

        List<GeoServer> geoServers = new ArrayList<>();
        for(Map.Entry<Object, CommentedConfigurationNode> entry : this.serverRoot.childrenMap().entrySet()) {
            Optional<RegisteredServer> registeredServerOptional = proxyServer.getServer(entry.getKey().toString());
            if (registeredServerOptional.isEmpty()) {
                this.logger.warn("Couldn't find Server '{}'", entry.getKey().toString());
                continue;
            }

            List<String> states = entry.getValue().node("bundesländer").childrenList().stream().map(CommentedConfigurationNode::getString).toList();
            List<String> cities = entry.getValue().node("städte").childrenList().stream().map(CommentedConfigurationNode::getString).toList();

            boolean tpllPassthrough = entry.getValue().node("tpll-passthrough").getBoolean();
            boolean isEarthServer = entry.getValue().node("isEarthServer").getBoolean();
            boolean showPlayersOnTerramap = entry.getValue().node("showPlayersOnTerramap").getBoolean();
            String normenWarpName = entry.getValue().node("normen-warp").getString();
            Warp normenWarp = normenWarpName == null ? null : warpsRegistry.getWarps().stream().filter(warp -> warp.getName().equals(normenWarpName)).findFirst().orElse(null);

            geoServers.add(new GeoServer(registeredServerOptional.get(), states, cities, tpllPassthrough, isEarthServer, showPlayersOnTerramap, normenWarp));
        }
        this.geoData.setGeoServers(geoServers);
    }

    // read the database config and return the data (url, user, password)
    public List<String> readDatabaseConfig() {
        List<String> data = new ArrayList<>();

        if (this.configRoot == null) {
            return data;
        }

        CommentedConfigurationNode mysqlNode = this.configRoot.node("database", "mysql");
        data.add(mysqlNode.node("url").getString());
        data.add(mysqlNode.node("user").getString());
        data.add(mysqlNode.node("password").getString());

        return data;
    }

    public String readPrefix() {
        if (this.configRoot == null) {
            return "";
        }

        return this.configRoot.node("prefix").getString("");
    }

}
