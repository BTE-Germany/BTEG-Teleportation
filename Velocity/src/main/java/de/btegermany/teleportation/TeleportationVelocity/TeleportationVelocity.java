package de.btegermany.teleportation.TeleportationVelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationVelocity.command.*;
import de.btegermany.teleportation.TeleportationVelocity.data.ConfigHandler;
import de.btegermany.teleportation.TeleportationVelocity.data.Database;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoData;
import de.btegermany.teleportation.TeleportationVelocity.listener.PluginMsgListener;
import de.btegermany.teleportation.TeleportationVelocity.listener.ServerLeaveListener;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.Utils;
import de.btegermany.teleportation.TeleportationVelocity.util.Warp;
import de.btegermany.teleportation.TeleportationVelocity.util.WarpIdsManager;
import lombok.Getter;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Plugin(id = "bteg-teleportation-velocity", name = "BTEG Teleportation Velocity", version = "2.0.0", url = "bte-germany.de", description = "This plugin covers most teleportation functions across the network.", authors = {"Leander", "JaskerX"})
public class TeleportationVelocity {

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final File dataDirectory;

    public static final MinecraftChannelIdentifier PLUGIN_CHANNEL = MinecraftChannelIdentifier.create("bteg", "teleportation");
    private static TextComponent PREFIX;
    private Database database;
    @Getter
    private WarpIdsManager warpIdsManager;
    private RegistriesProvider registriesProvider;
    @Getter
    private GeoData geoData;
    private PluginMessenger pluginMessenger;
    private ConfigHandler configHandler;
    private ScheduledExecutorService scheduledExecutorServiceCheckStateBorders;
    private ScheduledExecutorService scheduledExecutorServiceSendWarpCities;
    private ScheduledExecutorService scheduledExecutorServiceSendWarpTags;
    @Getter
    private Warp eventWarp = null;

    @Inject
    public TeleportationVelocity(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectoryPath) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectoryPath.toFile();

        if (!this.dataDirectory.exists()) {
            this.dataDirectory.mkdir();
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // initialize objects
        this.geoData = new GeoData(this.logger, this.dataDirectory);
        this.configHandler = new ConfigHandler(this, this.proxyServer, this.logger, this.dataDirectory, this.geoData);
        PREFIX = configHandler.readPrefix();
        this.database = new Database(this.configHandler);
        this.database.connect();
        this.warpIdsManager = new WarpIdsManager(this.database);
        this.registriesProvider = new RegistriesProvider(this.database, this.logger, this.warpIdsManager);
        this.registriesProvider.getWarpsRegistry().loadWarps();
        this.pluginMessenger = new PluginMessenger(this.proxyServer, this.registriesProvider);
        this.configHandler.readServers();
        this.eventWarp = this.configHandler.readEventWarp(this.registriesProvider.getWarpsRegistry());
        Utils utils = new Utils(this.proxyServer, this.pluginMessenger, this.registriesProvider);

        // register commands
        CommandManager commandManager = this.proxyServer.getCommandManager();
        commandManager.register(commandManager.metaBuilder("tp").plugin(this).build(), TpCommand.createTpCommand(this, this.proxyServer, utils, this.registriesProvider, this.pluginMessenger));
        commandManager.register(commandManager.metaBuilder("tpa").plugin(this).build(), TpaCommand.createTpaCommand(this.proxyServer, utils, this.registriesProvider));
        commandManager.register(commandManager.metaBuilder("tpaccept").aliases("tpaaccept").plugin(this).build(), TpacceptCommand.createTpacceptCommand(this.proxyServer, utils, this.registriesProvider, this.pluginMessenger));
        commandManager.register(commandManager.metaBuilder("tpaDeny").plugin(this).build(), TpaDenyCommand.createTpaDenyCommand(this.proxyServer, this.registriesProvider));
        commandManager.register(commandManager.metaBuilder("tpaCancel").plugin(this).build(), TpaCancelCommand.createTpaCancelCommand(utils));
        commandManager.register(commandManager.metaBuilder("tpHere").plugin(this).build(), TpHereCommand.createTpHereCommand(this.proxyServer, utils, this.registriesProvider, this.pluginMessenger));
        commandManager.register(commandManager.metaBuilder("tpBack").plugin(this).build(), TpBackCommand.createTpBackCommand(this.registriesProvider, this.pluginMessenger));
        commandManager.register(commandManager.metaBuilder("tpll").aliases("tpl").plugin(this).build(), new TpllCommand(this.geoData, this.pluginMessenger, this.registriesProvider));
        commandManager.register(commandManager.metaBuilder("event").plugin(this).build(), EventCommand.createEventCommand(this, this.registriesProvider, this.pluginMessenger, this.proxyServer));
        commandManager.register(commandManager.metaBuilder("hub").aliases("lobby", "l").plugin(this).build(), HubCommand.createHubCommand(this.proxyServer));
        commandManager.register(commandManager.metaBuilder("bl").aliases("bundesland").plugin(this).build(), BlCommand.createBlCommand(this.geoData));
        commandManager.register(commandManager.metaBuilder("norms").aliases("normen", "norme").plugin(this).build(), NormsCommand.createNormsCommand(this.proxyServer, this.configHandler, this.registriesProvider, this.pluginMessenger));

        // register plugin channel
        this.proxyServer.getChannelRegistrar().register(PLUGIN_CHANNEL);

        // register listeners
        EventManager eventManager = this.proxyServer.getEventManager();
        eventManager.register(this, new PluginMsgListener(this.pluginMessenger, this.database, this.registriesProvider, this.proxyServer, this.logger, this.warpIdsManager));
        eventManager.register(this, new ServerLeaveListener(this.registriesProvider));

        // schedule task to check if the players are on the right server. If not they will be teleported to the right server
        this.startStateBorderCheck();

        // schedule task to send cities warps are located in and warp tags to all servers
        this.scheduleSendWarpCities();
        this.scheduleSendWarpTags();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        this.database.disconnect();

        this.scheduledExecutorServiceCheckStateBorders.shutdownNow();
        this.scheduledExecutorServiceSendWarpCities.shutdownNow();
        this.scheduledExecutorServiceSendWarpTags.shutdownNow();
    }

    private void startStateBorderCheck() {
        this.scheduledExecutorServiceCheckStateBorders = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorServiceCheckStateBorders.scheduleAtFixedRate(() ->
                this.registriesProvider.getBukkitPlayersRegistry().getBukkitPlayers().forEach((uuid, bukkitPlayer) -> {
                    try {
                        double[] coords = GeoData.BTE_GENERATOR_SETTINGS.projection().toGeo(bukkitPlayer.getX(), bukkitPlayer.getZ());
                        RegisteredServer server = this.geoData.getServerFromLocationCheck(coords[1], coords[0], bukkitPlayer.getProxiedPlayer()).orElse(null);
                        if (!bukkitPlayer.getServer().equals(server) && this.geoData.getGeoServers().stream().anyMatch(geoServer -> geoServer.server().equals(server) && geoServer.isEarthServer()) && this.geoData.getGeoServers().stream().anyMatch(geoServer -> geoServer.server().equals(bukkitPlayer.getServer()) && geoServer.isEarthServer())) {
                            sendMessage(bukkitPlayer.getProxiedPlayer(), Component.text("Dieses Bundesland liegt auf einem anderen Server, du wirst daher auf den richtigen Server gesendet!", NamedTextColor.GOLD));
                            this.proxyServer.getCommandManager().executeAsync(bukkitPlayer.getProxiedPlayer(), "tpll " + coords[1] + " " + coords[0] + " yaw=" + bukkitPlayer.getYaw() + " pitch=" + bukkitPlayer.getPitch());
                        }
                    } catch (OutOfProjectionBoundsException e) {
                        e.printStackTrace();
                    }
                }), 0, 3, TimeUnit.SECONDS);
    }

    private void scheduleSendWarpCities() {
        this.scheduledExecutorServiceSendWarpCities = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorServiceSendWarpCities.scheduleAtFixedRate(() -> this.pluginMessenger.sendWarpCitiesToServers(this.registriesProvider.getWarpsRegistry().getWarps()),
                0,
                10,
                TimeUnit.SECONDS);
    }

    private void scheduleSendWarpTags() {
        this.scheduledExecutorServiceSendWarpTags = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorServiceSendWarpTags.scheduleAtFixedRate(() -> this.pluginMessenger.sendWarpTagsToServers(this.registriesProvider.getWarpTagsRegistry().getTags()),
                5,
                10,
                TimeUnit.SECONDS);
    }

    public static Component getMessage(Component... components) {
        Component message = PREFIX.append(Component.text(" "));
        for (Component component : components) {
            message = message.append(component);
        }
        return message;
    }

    public static void sendMessage(CommandSource receiver, Component... components) {
        receiver.sendMessage(TeleportationVelocity.getMessage(components));
    }

    public void setEventWarp(Warp warp) {
        this.eventWarp = warp;
        this.configHandler.saveEventWarp();
    }

}
