package de.btegermany.teleportation.TeleportationBukkit.gui.blueprint;

import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import org.bukkit.entity.Player;
import org.json.JSONArray;

public record MultiPageGuiArgs(Player player, PluginMessenger pluginMessenger, RegistriesProvider registriesProvider, JSONArray contentJSON) {

    public GuiArgs guiArgs() {
        return new GuiArgs(player, pluginMessenger, registriesProvider);
    }

}
