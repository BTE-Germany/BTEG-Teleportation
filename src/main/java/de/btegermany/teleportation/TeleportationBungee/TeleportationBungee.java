package de.btegermany.teleportation.TeleportationBungee;

import de.btegermany.teleportation.TeleportationBungee.command.*;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.listener.PluginMsgListener;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.util.Database;
import de.btegermany.teleportation.TeleportationBungee.util.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.util.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Plugin;

public class TeleportationBungee extends Plugin {

    public static String PLUGIN_CHANNEL = "bungeecord:btegtp";
    private static TeleportationBungee instance;
    private Database database;

    @Override
    public void onEnable() {
        instance = this;

        RegistriesProvider registriesProvider = new RegistriesProvider();
        PluginMessenger pluginMessenger = new PluginMessenger();
        Utils utils = new Utils(pluginMessenger, registriesProvider);
        GeoData geoData = new GeoData(this);
        geoData.loadGeoServers();
        database = new Database(this);
        database.connect();

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TeleportCommand(utils));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaCommand(utils, registriesProvider));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpacceptCommand(utils, registriesProvider));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaDenyCommand(registriesProvider));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaCancelCommand(utils));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpHereCommand(utils));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpBackCommand(registriesProvider));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpllCommand(geoData, pluginMessenger));

        ProxyServer.getInstance().registerChannel(PLUGIN_CHANNEL);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMsgListener(pluginMessenger, database, geoData, registriesProvider));
    }

    @Override
    public void onDisable() {
        database.disconnect();
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
