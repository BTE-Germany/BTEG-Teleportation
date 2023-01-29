package de.btegermany.teleportation.TeleportationBungee;

import java.util.*;

import de.btegermany.teleportation.TeleportationBungee.LastLocation;

import de.btegermany.teleportation.TeleportationBungee.commands.*;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoServer;
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

        ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMsgListener());

        ProxyServer.getInstance().registerChannel(PLUGIN_CHANNEL);

        GeoData.loadGeoServers();
        test();
    }

    public static BaseComponent[] getFormattedMessage(String text) {
        String[] words = text.split(" ");
        StringBuilder builder = new StringBuilder("§b§lBTEG §7»");
        for(String word : words) {
            builder.append(" §6").append(word);
        }
        return new ComponentBuilder(new String(builder)).create();
    }

    private static void test() {
        getInstance().getLogger().info("3 BW - " + GeoData.getServerFromLocation(49.01358814174579, 8.40423530806287).getName());
        getInstance().getLogger().info("3 NI - " + GeoData.getServerFromLocation(52.612235447713495, 9.603228248837176).getName());
        getInstance().getLogger().info("2 BY - " + GeoData.getServerFromLocation(48.45938638485063, 12.654102115456647).getName());
        getInstance().getLogger().info("2 HE - " + GeoData.getServerFromLocation(50.25781963461422, 8.805246132913922).getName());
    }

    public static TeleportationBungee getInstance() {
        return instance;
    }
}
