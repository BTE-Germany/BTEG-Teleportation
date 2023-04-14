package de.btegermany.teleportation.TeleportationBukkit.message;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class PluginMessenger {

    private final TeleportationBukkit plugin;

    public PluginMessenger(TeleportationBukkit plugin) {
        this.plugin = plugin;
    }

    public void send(PluginMessage pluginMessage) {
        List<? extends Player> onlinePlayers = (List<? extends Player>) plugin.getServer().getOnlinePlayers();
        if(onlinePlayers.size() == 0) {
            return;
        }
        onlinePlayers.get(0).sendPluginMessage(plugin, TeleportationBukkit.PLUGIN_CHANNEL, pluginMessage.getBytes());
    }

}
