package de.btegermany.teleportation.TeleportationVelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationVelocity.command.*;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoData;
import de.btegermany.teleportation.TeleportationVelocity.listener.PluginMsgListener;
import de.btegermany.teleportation.TeleportationVelocity.listener.ServerLeaveListener;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.data.ConfigReader;
import de.btegermany.teleportation.TeleportationVelocity.data.Database;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.util.Utils;
import de.btegermany.teleportation.TeleportationVelocity.util.WarpIdsManager;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


// test /lobby /bl
@Plugin(id = "teleportation-velocity", name = "Teleportation Velocity", version = "2.0.0", url = "bte-germany.de", description = "This plugin covers most teleportation functions across the network.", authors = {"Leander", "JaskerX"})
public class TeleportationVelocity {

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final File dataDirectory;

    public static final MinecraftChannelIdentifier PLUGIN_CHANNEL = MinecraftChannelIdentifier.create("bteg", "teleportation");
    private String prefix;
    private Database database;
    private RegistriesProvider registriesProvider;
    private GeoData geoData;
    private PluginMessenger pluginMessenger;
    private ScheduledExecutorService scheduledExecutorServiceCheckStateBorders;
    private ScheduledExecutorService scheduledExecutorServiceSendWarpCities;
    private ScheduledExecutorService scheduledExecutorServiceSendWarpTags;

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
        ConfigReader configReader = new ConfigReader(this.proxyServer, this.logger, this.dataDirectory, this.geoData);
        this.prefix = configReader.readPrefix();
        this.database = new Database(configReader);
        this.database.connect();
        WarpIdsManager warpIdsManager = new WarpIdsManager(this.database);
        this.registriesProvider = new RegistriesProvider(this, this.database, this.logger, warpIdsManager);
        this.registriesProvider.getWarpsRegistry().loadWarps();
        this.pluginMessenger = new PluginMessenger(this.proxyServer, this.registriesProvider);
        configReader.readServers(this.registriesProvider.getWarpsRegistry());
        Utils utils = new Utils(this, this.proxyServer, this.pluginMessenger, this.registriesProvider);

        // register commands
        CommandManager commandManager = this.proxyServer.getCommandManager();
        commandManager.register(TpCommand.createTpCommand(this, this.proxyServer, utils, this.registriesProvider, this.pluginMessenger));
        commandManager.register(TpaCommand.createTpaCommand(this, this.proxyServer, utils, this.registriesProvider));
        commandManager.register(commandManager.metaBuilder("tpaccept").aliases("tpaaccept").plugin(this).build(), TpacceptCommand.createTpacceptCommand(this, this.proxyServer, utils, this.registriesProvider, this.pluginMessenger));
        commandManager.register(TpaDenyCommand.createTpaDenyCommand(this, this.proxyServer, this.registriesProvider));
        commandManager.register(TpaCancelCommand.createTpaCancelCommand(this.proxyServer, utils));
        commandManager.register(TpHereCommand.createTpHereCommand(this, this.proxyServer, utils, this.registriesProvider, this.pluginMessenger));
        commandManager.register(TpBackCommand.createTpBackCommand(this, this.proxyServer, this.registriesProvider, this.pluginMessenger));
        commandManager.register(commandManager.metaBuilder("tpll").aliases("tpl").plugin(this).build(), new TpllCommand(this, this.proxyServer, this.geoData, this.pluginMessenger, this.registriesProvider));
        commandManager.register(EventCommand.createEventCommand(this, this.proxyServer, this.registriesProvider, this.pluginMessenger));
        commandManager.register(commandManager.metaBuilder("lobby").aliases("l").plugin(this).build(), LobbyCommand.createLobbyCommand(this, this.proxyServer));
        commandManager.register(commandManager.metaBuilder("bl").aliases("bundesland").plugin(this).build(), BlCommand.createBlCommand(this, this.proxyServer));

        // register plugin channel
        this.proxyServer.getChannelRegistrar().register(PLUGIN_CHANNEL);

        // register listeners
        this.proxyServer.getEventManager().register(this, new PluginMsgListener(this, this.pluginMessenger, this.database, this.geoData, this.registriesProvider, this.proxyServer, warpIdsManager));
        this.proxyServer.getEventManager().register(this, new ServerLeaveListener(this.registriesProvider));

        // schedule task to check if the players are on the right server. If not they will be teleported to the right server
        this.startStateBorderCheck();

        // schedule task to send cities warps are located in and warp tags to all servers
        this.scheduleSendWarpCities();
        this.scheduleSendWarpTags();

        //DatabaseConverter databaseConverter = new DatabaseConverter(this, database, new File(this.getDataFolder(), "BTEGTeleportationBungee.db"));
        //databaseConverter.convertDbFileToDatabase();
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
        this.scheduledExecutorServiceCheckStateBorders.scheduleAtFixedRate(() -> {
            this.registriesProvider.getBukkitPlayersRegistry().getBukkitPlayers().forEach((uuid, bukkitPlayer) -> {
                try {
                    double[] coords = GeoData.BTE_GENERATOR_SETTINGS.projection().toGeo(bukkitPlayer.getX(), bukkitPlayer.getZ());
                    RegisteredServer server = this.geoData.getServerFromLocation(coords[1], coords[0]).orElse(null);
                    if(!bukkitPlayer.getServer().equals(server) && this.geoData.getGeoServers().stream().anyMatch(geoServer -> geoServer.getServer().equals(server) && geoServer.isEarthServer()) && this.geoData.getGeoServers().stream().anyMatch(geoServer -> geoServer.getServer().equals(bukkitPlayer.getServer()) && geoServer.isEarthServer())) {
                        bukkitPlayer.getProxiedPlayer().sendMessage(Component.text(this.prefix + " Dieses Bundesland liegt auf einem anderen Server, du wirst daher auf den richtigen Server gesendet!", NamedTextColor.GOLD));
                        this.proxyServer.getCommandManager().executeAsync(bukkitPlayer.getProxiedPlayer(), "tpll " + coords[1] + " " + coords[0] + " yaw=" + bukkitPlayer.getYaw() + " pitch=" + bukkitPlayer.getPitch());
                    }
                } catch (OutOfProjectionBoundsException e) {
                    e.printStackTrace();
                }
            });
        }, 0, 3, TimeUnit.SECONDS);
    }

    private void scheduleSendWarpCities() {
        this.scheduledExecutorServiceSendWarpCities = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorServiceSendWarpCities.scheduleAtFixedRate(() -> {
            this.pluginMessenger.sendWarpCitiesToServers(this.registriesProvider.getWarpsRegistry().getWarps());
        }, 0, 10, TimeUnit.SECONDS);
    }

    private void scheduleSendWarpTags() {
        this.scheduledExecutorServiceSendWarpTags = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorServiceSendWarpTags.scheduleAtFixedRate(() -> {
            this.pluginMessenger.sendWarpTagsToServers(this.registriesProvider.getWarpTagsRegistry().getTags());
        }, 5, 10, TimeUnit.SECONDS);
    }

    public String getPrefix() {
        return prefix;
    }
}
