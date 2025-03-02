package de.btegermany.teleportation.TeleportationBungee;

import de.btegermany.teleportation.TeleportationBungee.command.*;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.listener.PluginMsgListener;
import de.btegermany.teleportation.TeleportationBungee.listener.ServerLeaveListener;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.data.ConfigReader;
import de.btegermany.teleportation.TeleportationBungee.data.Database;
import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.util.Utils;
import de.btegermany.teleportation.TeleportationBungee.util.Warp;
import de.btegermany.teleportation.TeleportationBungee.util.WarpIdsManager;
import fr.thesmyler.bungee2forge.api.ForgeChannel;
import fr.thesmyler.bungee2forge.api.ForgeChannelRegistry;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TeleportationBungee extends Plugin {

    private static TeleportationBungee instance;
    public static final String PLUGIN_CHANNEL = "bungeecord:btegtp";
    public static final ForgeChannel TERRAMAP_MAP_SYNC_CHANNEL = ForgeChannelRegistry.instance().get("terramap:mapsync");
    public static final ForgeChannel TERRAMAP_PLUGIN_CHANNEL = ForgeChannelRegistry.instance().get("terramap:sh");
    private Database database;
    private WarpIdsManager warpIdsManager;
    private RegistriesProvider registriesProvider;
    private GeoData geoData;
    private PluginMessenger pluginMessenger;
    private ConfigReader configReader;
    private ScheduledExecutorService scheduledExecutorServiceCheckStateBorders;
    private ScheduledExecutorService scheduledExecutorServiceSendWarpCities;
    private ScheduledExecutorService scheduledExecutorServiceSendWarpTags;
    private Warp eventWarp = null;

    @Override
    public void onEnable() {
        instance = this;

        // enable support for Terramap
        // mod is not available for newer versions, therefore not active
        /*BungeeToForgePlugin.onEnable(this);
        TERRAMAP_MAP_SYNC_CHANNEL.registerPacket(0, RegisterForUpdatePacket.class);
        TERRAMAP_MAP_SYNC_CHANNEL.registerPacket(1, PlayerSyncPacket.class);
        TERRAMAP_PLUGIN_CHANNEL.registerPacket(0, PluginHelloPacket.class);*/

        // initialize objects
        this.geoData = new GeoData(this);
        this.configReader = new ConfigReader(this, this.geoData);
        this.database = new Database(this.configReader);
        this.database.connect();
        this.warpIdsManager = new WarpIdsManager(database);
        this.registriesProvider = new RegistriesProvider(this.database, this);
        this.registriesProvider.getWarpsRegistry().loadWarps();
        this.pluginMessenger = new PluginMessenger(this.registriesProvider);
        this.configReader.readServers(this.registriesProvider.getWarpsRegistry());
        this.eventWarp = this.configReader.readEventWarp(this.registriesProvider.getWarpsRegistry());
        Utils utils = new Utils(this.pluginMessenger, this.registriesProvider);

        // register commands
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TeleportCommand(utils, this.registriesProvider, this.pluginMessenger));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaCommand(utils, this.registriesProvider));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpacceptCommand(utils, this.registriesProvider, this.pluginMessenger));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaDenyCommand(this.registriesProvider));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaCancelCommand(utils));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpHereCommand(utils, this.registriesProvider, this.pluginMessenger));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpBackCommand(this.registriesProvider, this.pluginMessenger));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpllCommand(this.geoData, this.pluginMessenger, this.registriesProvider));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new EventCommand(this.registriesProvider, this.pluginMessenger));

        // register plugin channel
        ProxyServer.getInstance().registerChannel(PLUGIN_CHANNEL);

        // register listeners
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMsgListener(this.pluginMessenger, this.database, this.geoData, this.registriesProvider));
        //ProxyServer.getInstance().getPluginManager().registerListener(this, new TerramapListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerLeaveListener(this.registriesProvider));

        // schedule task to send Terramap data
        /*ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            Collection<BukkitPlayer> players = this.registriesProvider.getBukkitPlayersRegistry().getBukkitPlayers().values();
            if(players.isEmpty()) {
                return;
            }
            BukkitPlayer[] playersArray = players.toArray(new BukkitPlayer[0]);
            PlayerSyncPacket playerSyncPacket = new PlayerSyncPacket(playersArray, this.geoData);
            TERRAMAP_MAP_SYNC_CHANNEL.send(playerSyncPacket, ProxyServer.getInstance().getPlayers().toArray(new ProxiedPlayer[0]));
        }, 0, 500, TimeUnit.MILLISECONDS);*/

        // schedule task to check if the players are on the right server. If not they will be teleported to the right server
        this.startStateBorderCheck();

        // schedule task to send cities warps are located in and warp tags to all servers
        this.scheduleSendWarpCities();
        this.scheduleSendWarpTags();

        //DatabaseConverter databaseConverter = new DatabaseConverter(this, database, new File(this.getDataFolder(), "BTEGTeleportationBungee.db"));
        //databaseConverter.convertDbFileToDatabase();
    }

    @Override
    public void onDisable() {
        this.database.disconnect();

        // disable Terramap
        /*TERRAMAP_MAP_SYNC_CHANNEL.deregisterAllPackets();
        TERRAMAP_PLUGIN_CHANNEL.deregisterAllPackets();
        BungeeToForgePlugin.onDisable(this);*/

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
                    ServerInfo serverInfo = this.geoData.getServerFromLocation(coords[1], coords[0]);
                    if(!bukkitPlayer.getServerInfo().equals(serverInfo) && this.geoData.getGeoServers().stream().anyMatch(geoServer -> geoServer.getServerInfo().equals(serverInfo) && geoServer.isEarthServer()) && this.geoData.getGeoServers().stream().anyMatch(geoServer -> geoServer.getServerInfo().equals(bukkitPlayer.getServerInfo()) && geoServer.isEarthServer())) {
                        bukkitPlayer.getProxiedPlayer().sendMessage(TeleportationBungee.getFormattedMessage("Dieses Bundesland liegt auf einem anderen Server, du wirst daher auf den richtigen Server gesendet!"));
                        ProxyServer.getInstance().getPluginManager().dispatchCommand(bukkitPlayer.getProxiedPlayer(), "tpll " + coords[1] + " " + coords[0] + " yaw=" + bukkitPlayer.getYaw() + " pitch=" + bukkitPlayer.getPitch());
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

    public static BaseComponent[] getFormattedMessage(String text) {
        String[] words = text.split(" ");
        StringBuilder builder = new StringBuilder("ᾠ");
        for(String word : words) {
            builder.append(" §6").append(word);
        }
        return new ComponentBuilder(new String(builder)).create();
    }

    public static TeleportationBungee getInstance() {
        return instance;
    }

    public WarpIdsManager getWarpIdsManager() {
        return this.warpIdsManager;
    }

    public Warp getEventWarp() {
        return eventWarp;
    }

    public void setEventWarp(Warp warp) {
        this.eventWarp = warp;
        this.configReader.saveEventWarp();
    }
}
