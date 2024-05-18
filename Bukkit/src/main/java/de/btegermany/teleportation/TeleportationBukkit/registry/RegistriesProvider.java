package de.btegermany.teleportation.TeleportationBukkit.registry;

import de.btegermany.teleportation.TeleportationAPI.registry.PluginMessagesWithResponseRegistry;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;

public class RegistriesProvider {

    private final MultiplePagesGuisRegistry multiplePagesGuisRegistry;
    private final PlayersEnteringDeleteWarpIdRegistry playersEnteringDeleteWarpIdRegistry;
    private final PlayersEnteringChangeWarpIdRegistry playersEnteringChangeWarpIdRegistry;
    private final LobbyCitiesRegistry lobbyCitiesRegistry;
    private final CitiesRegistry citiesRegistry;
    private final WarpTagsRegistry warpTagsRegistry;
    private final PluginMessagesWithResponseRegistry pluginMessagesWithResponseRegistry;

    public RegistriesProvider(TeleportationBukkit plugin) {
        this.multiplePagesGuisRegistry = new MultiplePagesGuisRegistry();
        this.playersEnteringDeleteWarpIdRegistry = new PlayersEnteringDeleteWarpIdRegistry();
        this.playersEnteringChangeWarpIdRegistry = new PlayersEnteringChangeWarpIdRegistry();
        this.lobbyCitiesRegistry = new LobbyCitiesRegistry(plugin, "lobbycities.yml");
        this.citiesRegistry = new CitiesRegistry();
        this.warpTagsRegistry = new WarpTagsRegistry();
        this.pluginMessagesWithResponseRegistry = new PluginMessagesWithResponseRegistry();
    }

    // Getters

    public MultiplePagesGuisRegistry getMultiplePagesGuisRegistry() {
        return multiplePagesGuisRegistry;
    }

    public PlayersEnteringDeleteWarpIdRegistry getPlayersEnteringDeleteWarpIdRegistry() {
        return playersEnteringDeleteWarpIdRegistry;
    }

    public PlayersEnteringChangeWarpIdRegistry getPlayersEnteringChangeWarpIdRegistry() {
        return playersEnteringChangeWarpIdRegistry;
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
