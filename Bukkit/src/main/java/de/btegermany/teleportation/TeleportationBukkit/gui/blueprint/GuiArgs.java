package de.btegermany.teleportation.TeleportationBukkit.gui.blueprint;

import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import org.bukkit.entity.Player;

public record GuiArgs(Player player, PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {}
