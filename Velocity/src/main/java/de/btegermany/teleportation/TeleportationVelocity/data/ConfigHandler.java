package de.btegermany.teleportation.TeleportationVelocity.data;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationAPI.State;
import de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoData;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoServer;
import de.btegermany.teleportation.TeleportationVelocity.registry.WarpsRegistry;
import de.btegermany.teleportation.TeleportationVelocity.util.Warp;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigHandler {

    private final TeleportationVelocity plugin;
    private final ProxyServer proxyServer;
    private final Logger logger;
    private final File dataDirectory;
    private final GeoData geoData;
    private final Map<Config, HoconConfigurationLoader> configLoaders;
    private final Map<Config, CommentedConfigurationNode> configRoots;

    public ConfigHandler(TeleportationVelocity plugin, ProxyServer proxyServer, Logger logger, File dataDirectory, GeoData geoData) {
        this.plugin = plugin;
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.geoData = geoData;
        this.configLoaders = new HashMap<>();
        this.configRoots = new HashMap<>();

        this.loadConfigRoot(Config.DEFAULT);
        this.loadConfigRoot(Config.SERVER);
        this.loadConfigRoot(Config.VALUES);
    }

    private void loadConfigRoot(Config config) {
        final HoconConfigurationLoader configLoader = HoconConfigurationLoader.builder()
                .path(new File(this.dataDirectory, config.getFileName()).toPath())
                .build();
        this.configLoaders.put(config, configLoader);

        try {
            CommentedConfigurationNode configNode = configLoader.load();
            this.configRoots.put(config, configNode);
        } catch (IOException e) {
            this.logger.error("Loading config {} failed", config.getFileName(), e);
        }
    }

    private void updateNode(ConfigurationNode node, Object value, Config config) {
        try {
            node.set(value);
            this.configLoaders.get(config).save(this.configRoots.get(config));
        } catch (ConfigurateException e) {
            this.logger.warn("Updating node for config {} failed", config.getFileName(), e);
        }
    }

    // read the servers config and add them as GeoServers to GeoData
    public void readServers() {
        if (!this.configRoots.containsKey(Config.SERVER)) {
            return;
        }
        CommentedConfigurationNode configServer = this.configRoots.get(Config.SERVER);

        List<GeoServer> geoServers = new ArrayList<>();
        for(Map.Entry<Object, CommentedConfigurationNode> entry : configServer.childrenMap().entrySet()) {
            Optional<RegisteredServer> registeredServerOptional = proxyServer.getServer(entry.getKey().toString());
            if (registeredServerOptional.isEmpty()) {
                this.logger.warn("Couldn't find Server '{}'", entry.getKey().toString());
                continue;
            }

            List<State> states = entry.getValue().node("bundesländer").childrenList().stream().map(CommentedConfigurationNode::getString).map(State::getStateFromInput).toList();
            List<String> cities = entry.getValue().node("städte").childrenList().stream().map(CommentedConfigurationNode::getString).toList();

            boolean tpllPassthrough = entry.getValue().node("tpll-passthrough").getBoolean();
            boolean isEarthServer = entry.getValue().node("isEarthServer").getBoolean();

            geoServers.add(new GeoServer(registeredServerOptional.get(), states, cities, tpllPassthrough, isEarthServer));
        }
        this.geoData.setGeoServers(geoServers);
    }

    public TextComponent readPrefix() {
        if (!this.configRoots.containsKey(Config.DEFAULT)) {
            return Component.empty();
        }
        CommentedConfigurationNode config = this.configRoots.get(Config.DEFAULT);

        return Component.text(config.node("prefix").getString(""));
    }

    // read the database config and return the data (url, user, password)
    public List<String> readDataConfig() {
        List<String> data = new ArrayList<>();

        if (!this.configRoots.containsKey(Config.DEFAULT)) {
            return data;
        }
        CommentedConfigurationNode config = this.configRoots.get(Config.DEFAULT);
        config.childrenList().forEach(ConfigurationNode::getString);

        CommentedConfigurationNode mysqlNode = config.node("database", "mysql");
        data.add(mysqlNode.node("url").getString());
        data.add(mysqlNode.node("user").getString());
        data.add(mysqlNode.node("password").getString());

        return data;
    }

    public String[] readNormenServerAndWorld() {
        if (!this.configRoots.containsKey(Config.DEFAULT)) {
            return null;
        }
        CommentedConfigurationNode config = this.configRoots.get(Config.DEFAULT);

        String normenServer = config.node("normen", "server").getString();
        String normenWorld = config.node("normen", "world").getString();

        return new String[] {normenServer, normenWorld};
    }

    public float[] readNormenYawAndPitch() {
        if (!this.configRoots.containsKey(Config.DEFAULT)) {
            return null;
        }
        CommentedConfigurationNode config = this.configRoots.get(Config.DEFAULT);

        String[] yawAndPitch = config.node("normen", "rotation").getString("0/0").split("/");
        float yaw = Float.parseFloat(yawAndPitch[0]);
        float pitch = Float.parseFloat(yawAndPitch[1]);

        return new float[] {yaw, pitch};
    }

    public Warp readEventWarp(WarpsRegistry warpsRegistry) {
        if (!this.configRoots.containsKey(Config.VALUES)) {
            return null;
        }
        CommentedConfigurationNode config = this.configRoots.get(Config.VALUES);

        int warpId = config.node("eventwarp").getInt(-1);
        if (warpId == -1) {
            return null;
        }
        return warpsRegistry.getWarp(warpId);
    }

    public void saveEventWarp() {
        if (!this.configRoots.containsKey(Config.VALUES)) {
            return;
        }
        CommentedConfigurationNode config = this.configRoots.get(Config.VALUES);

        Warp warp = this.plugin.getEventWarp();
        this.updateNode(
                config.node("eventwarp"),
                warp != null ? warp.getId() : null,
                Config.VALUES
        );
    }

    @Getter
    private enum Config {
        DEFAULT ("config.conf"),
        SERVER ("server.conf"),
        VALUES ("values.conf");

        private final String fileName;

        Config(String fileName) {
            this.fileName = fileName;
        }
    }
}
