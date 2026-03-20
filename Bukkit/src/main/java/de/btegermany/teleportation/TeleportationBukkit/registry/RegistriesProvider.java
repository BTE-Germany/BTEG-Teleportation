package de.btegermany.teleportation.TeleportationBukkit.registry;

import de.btegermany.teleportation.TeleportationAPI.registry.PluginMessagesWithResponseRegistry;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;

public class RegistriesProvider {

    private final LobbyCitiesRegistry lobbyCitiesRegistry;
    private final CitiesRegistry citiesRegistry;
    private final WarpTagsRegistry warpTagsRegistry;
    private final PluginMessagesWithResponseRegistry pluginMessagesWithResponseRegistry;

    public RegistriesProvider(TeleportationBukkit plugin) {
        this.lobbyCitiesRegistry = new LobbyCitiesRegistry(plugin, "lobbycities.yml", plugin.getLogger());
        this.citiesRegistry = new CitiesRegistry();
        this.warpTagsRegistry = new WarpTagsRegistry();
        this.pluginMessagesWithResponseRegistry = new PluginMessagesWithResponseRegistry();
    }

    public LobbyCitiesRegistry getLobbyCitiesRegistry() {
        return lobbyCitiesRegistry;
    }

    public CitiesRegistry getCitiesRegistry() {
        return citiesRegistry;
    }

    public WarpTagsRegistry getWarpTagsRegistry() {
        return warpTagsRegistry;
    }

    public PluginMessagesWithResponseRegistry getPluginMessagesWithResponseRegistry() {
        return pluginMessagesWithResponseRegistry;
    }
}
