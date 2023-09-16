package de.btegermany.teleportation.TeleportationBungee;

import de.btegermany.teleportation.TeleportationBungee.command.*;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.listener.PluginMsgListener;
import de.btegermany.teleportation.TeleportationBungee.listener.ServerLeaveListener;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.terramap.PlayerSyncPacket;
import de.btegermany.teleportation.TeleportationBungee.terramap.PluginHelloPacket;
import de.btegermany.teleportation.TeleportationBungee.terramap.RegisterForUpdatePacket;
import de.btegermany.teleportation.TeleportationBungee.terramap.TerramapListener;
import de.btegermany.teleportation.TeleportationBungee.data.ConfigReader;
import de.btegermany.teleportation.TeleportationBungee.data.Database;
import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.util.Utils;
import fr.thesmyler.bungee2forge.BungeeToForgePlugin;
import fr.thesmyler.bungee2forge.api.ForgeChannel;
import fr.thesmyler.bungee2forge.api.ForgeChannelRegistry;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TeleportationBungee extends Plugin {

    private static TeleportationBungee instance;
    public static final String PLUGIN_CHANNEL = "bungeecord:btegtp";
    public static final ForgeChannel terramapMapSyncChannel = ForgeChannelRegistry.instance().get("terramap:mapsync");
    public static final ForgeChannel terramapPluginChannel = ForgeChannelRegistry.instance().get("terramap:sh");
    private Database database;
    private RegistriesProvider registriesProvider;
    private GeoData geoData;
    private ScheduledExecutorService scheduledExecutorServiceCheckStateBorders;
    private final EarthGeneratorSettings bteGeneratorSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);

    @Override
    public void onEnable() {
        instance = this;

        BungeeToForgePlugin.onEnable(this);
        terramapMapSyncChannel.registerPacket(0, RegisterForUpdatePacket.class);
        terramapMapSyncChannel.registerPacket(1, PlayerSyncPacket.class);
        terramapPluginChannel.registerPacket(0, PluginHelloPacket.class);

        this.registriesProvider = new RegistriesProvider();
        this.geoData = new GeoData(this);
        PluginMessenger pluginMessenger = new PluginMessenger();
        Utils utils = new Utils(pluginMessenger, this.registriesProvider);
        ConfigReader configReader = new ConfigReader(this, this.geoData);
        configReader.readServers();
        this.database = new Database(configReader);
        this.database.connect();

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TeleportCommand(utils));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaCommand(utils, this.registriesProvider));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpacceptCommand(utils, this.registriesProvider));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaDenyCommand(this.registriesProvider));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaCancelCommand(utils));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpHereCommand(utils));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpBackCommand(this.registriesProvider));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpllCommand(this.geoData, pluginMessenger));

        ProxyServer.getInstance().registerChannel(PLUGIN_CHANNEL);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMsgListener(pluginMessenger, this.database, this.geoData, this.registriesProvider));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new TerramapListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerLeaveListener(this.registriesProvider));

        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            Collection<BukkitPlayer> players = this.registriesProvider.getBukkitPlayersRegistry().getBukkitPlayers().values();
            if(players.isEmpty()) {
                return;
            }
            BukkitPlayer[] playersArray = players.toArray(new BukkitPlayer[0]);
            PlayerSyncPacket playerSyncPacket = new PlayerSyncPacket(playersArray, this.geoData);
            terramapMapSyncChannel.send(playerSyncPacket, ProxyServer.getInstance().getPlayers().toArray(new ProxiedPlayer[0]));
        }, 0, 500, TimeUnit.MILLISECONDS);

        //startStateBorderCheck();

        //DatabaseConverter databaseConverter = new DatabaseConverter(this, database, new File(this.getDataFolder(), "BTEGTeleportationBungee.db"));
        //databaseConverter.convertDbFileToDatabase();
    }

    @Override
    public void onDisable() {
        this.database.disconnect();

        terramapMapSyncChannel.deregisterAllPackets();
        terramapPluginChannel.deregisterAllPackets();
        BungeeToForgePlugin.onDisable(this);

        //this.scheduledExecutorServiceCheckStateBorders.shutdownNow();
    }

    public void startStateBorderCheck() {
        this.scheduledExecutorServiceCheckStateBorders = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorServiceCheckStateBorders.scheduleAtFixedRate(() -> {
            this.registriesProvider.getBukkitPlayersRegistry().getBukkitPlayers().forEach((uuid, bukkitPlayer) -> {
                try {
                    double[] coords = this.bteGeneratorSettings.projection().toGeo(bukkitPlayer.getX(), bukkitPlayer.getZ());
                    ServerInfo serverInfo = this.geoData.getServerFromLocation(coords[1], coords[0]);
                    if(!bukkitPlayer.getServerInfo().equals(serverInfo) && this.geoData.getGeoServers().stream().anyMatch(geoServer -> geoServer.getServerInfo().equals(serverInfo) && geoServer.isEarthServer()) && this.geoData.getGeoServers().stream().anyMatch(geoServer -> geoServer.getServerInfo().equals(bukkitPlayer.getServerInfo()) && geoServer.isEarthServer())) {
                        bukkitPlayer.getProxiedPlayer().sendMessage(TeleportationBungee.getFormattedMessage("Dieses Bundesland liegt auf einem anderen Server, du wirst daher auf den richtigen Server gesendet!"));
                        ProxyServer.getInstance().getPluginManager().dispatchCommand(bukkitPlayer.getProxiedPlayer(), "tpll " + coords[1] + " " + coords[0] + " yaw=" + bukkitPlayer.getYaw() + " pitch=" + bukkitPlayer.getPitch());
                    }
                } catch (OutOfProjectionBoundsException e) {
                    throw new RuntimeException(e);
                }
            });
        }, 0, 2, TimeUnit.SECONDS);
    }

    public static BaseComponent[] getFormattedMessage(String text) {
        String[] words = text.split(" ");
        StringBuilder builder = new StringBuilder("§b§lBTEG §7»");
        for(String word : words) {
            builder.append(" §6").append(word);
        }
        return new ComponentBuilder(new String(builder)).create();
    }

    public static TeleportationBungee getInstance() {
        return instance;
    }
}
