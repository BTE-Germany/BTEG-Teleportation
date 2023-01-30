package de.btegermany.teleportation.TeleportationBungee;

import java.util.*;

import de.btegermany.teleportation.TeleportationBungee.LastLocation;

import de.btegermany.teleportation.TeleportationBungee.commands.*;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.listeners.PluginMsgListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Plugin;

public class TeleportationBungee extends Plugin {

    public static HashMap<UUID, LastLocation> lastLocations = new HashMap<>();
    public static String PLUGIN_CHANNEL = "bungeecord:btegtp";
    public static TeleportationBungee instance;

    @Override
    public void onEnable() {
        instance = this;

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TeleportCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpacceptCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaDenyCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaCancelCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpHereCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpBackCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpllCommand());

        //ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMsgListener());

        ProxyServer.getInstance().registerChannel(PLUGIN_CHANNEL);

        GeoData.loadGeoServers();
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
