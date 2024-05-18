package de.btegermany.teleportation.TeleportationBukkit.message;


import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageWithResponse;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import org.bukkit.entity.Player;

import java.util.List;

public class PluginMessenger {

    private final TeleportationBukkit plugin;
    private final RegistriesProvider registriesProvider;

    public PluginMessenger(TeleportationBukkit plugin, RegistriesProvider registriesProvider) {
        this.plugin = plugin;
        this.registriesProvider = registriesProvider;
    }

    public void send(PluginMessage pluginMessage) {
        List<? extends Player> onlinePlayers = (List<? extends Player>) plugin.getServer().getOnlinePlayers();
        if(onlinePlayers.isEmpty()) {
            return;
        }
        if(pluginMessage instanceof PluginMessageWithResponse) {
            this.registriesProvider.getPluginMessagesWithResponseRegistry().register((PluginMessageWithResponse) pluginMessage);
        }
        onlinePlayers.get(0).sendPluginMessage(plugin, TeleportationBukkit.PLUGIN_CHANNEL, pluginMessage.getBytes());
    }

}
