package de.btegermany.teleportation.TeleportationBukkit.registry;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;

public class RegistriesProvider {

    private final MultiplePagesGuisRegistry multiplePagesGuisRegistry;
    private final PlayersEnteringDeleteWarpIdRegistry playersEnteringDeleteWarpIdRegistry;
    private final WarpsInCreationRegistry warpsInCreationRegistry;
    private final PlayersEnteringChangeWarpIdRegistry playersEnteringChangeWarpIdRegistry;
    private final WarpsGettingChangedRegistry warpsGettingChangedRegistry;
    private final LobbyCitiesRegistry lobbyCitiesRegistry;
    private final CitiesRegistry citiesRegistry;

    public RegistriesProvider(TeleportationBukkit plugin) {
        this.multiplePagesGuisRegistry = new MultiplePagesGuisRegistry();
        this.playersEnteringDeleteWarpIdRegistry = new PlayersEnteringDeleteWarpIdRegistry();
        this.warpsInCreationRegistry = new WarpsInCreationRegistry();
        this.playersEnteringChangeWarpIdRegistry = new PlayersEnteringChangeWarpIdRegistry();
        this.warpsGettingChangedRegistry = new WarpsGettingChangedRegistry();
        this.lobbyCitiesRegistry = new LobbyCitiesRegistry(plugin, "lobbycities.yml");
        this.citiesRegistry = new CitiesRegistry();
    }

    // Getters

    public MultiplePagesGuisRegistry getMultiplePagesGuisRegistry() {
        return multiplePagesGuisRegistry;
    }

    public PlayersEnteringDeleteWarpIdRegistry getPlayersEnteringDeleteWarpIdRegistry() {
        return playersEnteringDeleteWarpIdRegistry;
    }

    public WarpsInCreationRegistry getWarpsInCreationRegistry() {
        return warpsInCreationRegistry;
    }

    public PlayersEnteringChangeWarpIdRegistry getPlayersEnteringChangeWarpIdRegistry() {
        return playersEnteringChangeWarpIdRegistry;
    }

    public WarpsGettingChangedRegistry getWarpsGettingChangedRegistry() {
        return warpsGettingChangedRegistry;
    }

    public LobbyCitiesRegistry getLobbyCitiesRegistry() {
        return lobbyCitiesRegistry;
    }

    public CitiesRegistry getCitiesRegistry() {
        return citiesRegistry;
    }
}
